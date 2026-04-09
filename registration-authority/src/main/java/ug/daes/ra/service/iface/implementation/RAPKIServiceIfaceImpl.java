
package ug.daes.ra.service.iface.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ug.daes.ra.asserts.RAServiceAsserts;
import ug.daes.ra.config.SecureUrlValidator;
import ug.daes.ra.dto.ApiResponses;
import ug.daes.ra.dto.LogModelDTO;
import ug.daes.ra.enums.*;
import ug.daes.ra.exception.ErrorCodes;
import ug.daes.ra.exception.RAServiceException;
import ug.daes.ra.model.*;
import ug.daes.ra.repository.iface.*;
import ug.daes.ra.request.entity.*;
import ug.daes.ra.request.entity.RequestEntity;
import ug.daes.ra.response.entity.ServiceResponse;
import ug.daes.ra.service.iface.RAPKIServiceIface;
import ug.daes.ra.utils.*;

import java.util.*;

@Service
public class RAPKIServiceIfaceImpl implements RAPKIServiceIface {

	private static final Logger logger = LoggerFactory.getLogger(RAPKIServiceIfaceImpl.class);
	private static final String CLASS = "RAPKIServiceIfaceImpl";
	private static final String ERR_KEY = "api.error.something.went.wrong.please.try.after.sometime";
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${com.dt.pin.history.size}")
	private int pinHistorySize;

	@Value("${url.unlocked.AuthResetPin}")
	private String urlunlockedAuthResetPin;

	private final RaKafkaSender rabbitMQSender;
	private final RestTemplate restTemplate;
	private final RaSubscriberRepository raSubscriberRepository;
	private final RaSubscriberCertificatesRepository raSubscriberCertificatesRepository;
	private final RaSubscriberCertificatePinHistoryRepository pinHistoryRepository;
	private final RaSubscriberStatusRepository raSubscriberStatusRepository;
	private final RaSubscriberWrappedKeyRepository raSubscriberWrappedKeyRepository;
	private final RaOrganizationDetailsRepository raOrganizationDetailsRepository;
	private final RaOrganizationCertificatesRepository raOrganizationCertificatesRepository;
	private final RaOrganizationWrappedKeyRepository raOrganizationWrappedKeyRepository;
	private final MessageSource messageSource;
	private final SecureUrlValidator secureUrlValidator;

	public RAPKIServiceIfaceImpl(RaKafkaSender rabbitMQSender, RestTemplate restTemplate,
                                 RaSubscriberRepository raSubscriberRepository,
                                 RaSubscriberCertificatesRepository raSubscriberCertificatesRepository,
                                 RaSubscriberCertificatePinHistoryRepository pinHistoryRepository,
                                 RaSubscriberStatusRepository raSubscriberStatusRepository,
                                 RaSubscriberWrappedKeyRepository raSubscriberWrappedKeyRepository,
                                 RaOrganizationDetailsRepository raOrganizationDetailsRepository,
                                 RaOrganizationCertificatesRepository raOrganizationCertificatesRepository,
                                 RaOrganizationWrappedKeyRepository raOrganizationWrappedKeyRepository,
                                 MessageSource messageSource, SecureUrlValidator secureUrlValidator) {
		this.rabbitMQSender = rabbitMQSender;
		this.restTemplate = restTemplate;
		this.raSubscriberRepository = raSubscriberRepository;
		this.raSubscriberCertificatesRepository = raSubscriberCertificatesRepository;
		this.pinHistoryRepository = pinHistoryRepository;
		this.raSubscriberStatusRepository = raSubscriberStatusRepository;
		this.raSubscriberWrappedKeyRepository = raSubscriberWrappedKeyRepository;
		this.raOrganizationDetailsRepository = raOrganizationDetailsRepository;
		this.raOrganizationCertificatesRepository = raOrganizationCertificatesRepository;
		this.raOrganizationWrappedKeyRepository = raOrganizationWrappedKeyRepository;
		this.messageSource = messageSource;
		this.secureUrlValidator = secureUrlValidator;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String setPin(SetPinModel model) throws RAServiceException {
		try {
			RASubscriber sub = getSubscriberOrThrow(model.getSubscriberUniqueId());
			RASubscriberCertificates cert = getActiveCertByType(model.getSubscriberUniqueId(), model.getCertType());
			RASubscriberCertificatePinHistory history = getPinHistory(sub.getSubscriberUid());

			LogModelDTO logDto = prepareBaseLog(sub.getSubscriberUid(), TransactionType.BUSINESS);
			logDto.setGeoLocation(model.getGeoLocation());
			logDto.setCallStack(model.isChangePin() ? "CHANGE_PIN" : "SET_PIN");

			validatePinTransition(model, history);

			ServiceResponse sr = executePkiTransaction("SetPin", logDto);

			updatePinMetadata(model, cert, history, sr);
			return Constant.SUCCESS;

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: setPin unexpected error: {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public String generateSignature(GenerateSignature generateSignature) throws RAServiceException {
		try {
			RASubscriber sub = getSubscriberOrThrow(generateSignature.getSubscriberUniqueId());
			RASubscriberCertificates cert = getActiveCertByType(generateSignature.getSubscriberUniqueId(), generateSignature.getCertType());
			RASubscriberCertificatePinHistory history = pinHistoryRepository.findBysubscriberUniqueId(sub.getSubscriberUid());
			RASubscriberWrappedKey wk = raSubscriberWrappedKeyRepository.findBycertificateSerialNumber(cert.getCertificateSerialNumber());

			LogModelDTO logDto = prepareBaseLog(sub.getSubscriberUid(), TransactionType.BUSINESS);
			logDto.setServiceName(ServiceName.OTHER.toString());
			logDto.setSignatureType(SignatureType.DATA.toString());
			logDto.setCorrelationID(generateSignature.getCorrelationId());


			generateSignature.setKeyId(cert.getPkiKeyId());
			generateSignature.setWrappedKey(wk.getWrappedKey());
			generateSignature.setCertificate(cert.getCertificateData());
			generateSignature.setSerialNumber(cert.getCertificateSerialNumber());

			String pins = (generateSignature.getCertType() == 1)
					? history.getAuthenticationCertificatePinList()
					: history.getSigningCertificatePinList();

			RAServiceAsserts.notNullorEmpty(pins, (generateSignature.getCertType() == 1)
					? ErrorCodes.E_AUTHENTICATION_CERTIFICATE_PIN_NOT_SET
					: ErrorCodes.E_SIGNING_CERTIFICATE_PIN_NOT_SET);

			List<String> pinList = Arrays.asList(pins.split(", "));
			generateSignature.setCurrentSigningPassword(pinList.get(pinList.size() - 1));

			logDto.setCallStack(generateSignature.getGenerateSignatureData());
			ServiceResponse sr = executePkiTransaction(Constant.GENERATE_SIGNATURE, logDto);
			return sr.getSignature();

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: generateSignature error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public String generateSignatureOrganization(GenerateSignature generateSignature) throws RAServiceException {
		try {
			RAOrganizationDetails org = getOrgOrThrow(generateSignature.getSubscriberUniqueId());
			RAOrganizationCertificates cert = raOrganizationCertificatesRepository.findByCertificateStatusAndOrganizationUniqueId(
					Constant.SUBSCRIBER_STATUS, org.getOrganizationUid());

			RAServiceAsserts.notNullorEmpty(cert, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			LogModelDTO logDto = prepareBaseLog(org.getOrganizationUid(), TransactionType.BUSINESS);
			logDto.setServiceName(ServiceName.OTHER.toString());
			logDto.setSignatureType(SignatureType.DATA.toString());
			logDto.setCorrelationID(generateSignature.getCorrelationId());

			RAOrganizationWrappedKey wk = raOrganizationWrappedKeyRepository.findBycertificateSerialNumber(cert.getCertificateSerialNumber());
			generateSignature.setKeyId(cert.getPkiKeyId());
			generateSignature.setWrappedKey(wk.getWrappedKey());
			generateSignature.setCertificate(cert.getCertificateData());
			generateSignature.setSerialNumber(cert.getCertificateSerialNumber());

			logDto.setCallStack(generateSignature.getGenerateSignatureData());
			ServiceResponse sr = executePkiTransaction(Constant.GENERATE_SIGNATURE, logDto);
			return sr.getSignature();

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: generateSignatureOrg error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public String authenticatePKI(AuthenticatePKIModel authModel) throws RAServiceException {
		try {
			RASubscriber sub = getSubscriberOrThrow(authModel.getSubscriberUniqueId());
			RASubscriberCertificates cert = getActiveCertByType(authModel.getSubscriberUniqueId(), authModel.getCertType());

			LogModelDTO logDto = prepareBaseLog(sub.getSubscriberUid(), TransactionType.BUSINESS);
			logDto.setServiceName(ServiceName.PKI_AUTHENTICATED.toString());
			logDto.setCorrelationID(authModel.getCorrelationId());

			authModel.setKeyId(cert.getPkiKeyId());
			authModel.setCertificate(cert.getCertificateData());

			logDto.setCallStack(authModel.getauthenticatePKIData());
			ServiceResponse sr = executePkiTransaction(Constant.AUTHENTICATE_PKI, logDto);
			return sr.getStatus();

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: authenticatePKI error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String setPins(SetPinModelDto dto) throws RAServiceException {
		try {
			getSubscriberOrThrow(dto.getSubscriberUniqueId());
			List<RASubscriberCertificates> certs = raSubscriberCertificatesRepository.findByCertificateStatusAndsubscriberUniqueId(
					Constant.SUBSCRIBER_STATUS, dto.getSubscriberUniqueId());

			RAServiceAsserts.notNullorEmpty(certs, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			RASubscriberCertificatePinHistory history = getPinHistory(dto.getSubscriberUniqueId());

			for (RASubscriberCertificates cert : certs) {
				if (CertificateType.SIGN.toString().equals(cert.getCertificateType())) {
					history.setSigningCertificatePinList(dto.getSigningPassword());
					history.setSignPinSetDate(NativeUtils.getTimeStamp());
				} else {
					history.setAuthenticationCertificatePinList(dto.getAuthPassword());
					history.setAuthPinSetDate(NativeUtils.getTimeStamp());
				}
			}

			pinHistoryRepository.save(history);
			checkAndUpdateSubscriberStatus(dto.getSubscriberUniqueId(), history);
			return Constant.SUCCESS;

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (Exception e) {
			logger.error("{} :: setPins error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}


	private RASubscriber getSubscriberOrThrow(String suid) throws RAServiceException {
		RASubscriber sub = raSubscriberRepository.findBysubscriberUid(suid);
		RAServiceAsserts.notNullorEmpty(sub, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);
		return sub;
	}

	private RAOrganizationDetails getOrgOrThrow(String ouid) throws RAServiceException {
		RAOrganizationDetails org = raOrganizationDetailsRepository.findByOrganizationUid(ouid);
		RAServiceAsserts.notNullorEmpty(org, ErrorCodes.E_ORGANIZATION_DATA_NOT_FOUND);
		return org;
	}

	private RASubscriberCertificates getActiveCertByType(String suid, int certTypeIdx) throws RAServiceException {
		List<RASubscriberCertificates> certs = raSubscriberCertificatesRepository.findByCertificateStatusAndsubscriberUniqueId(
				Constant.SUBSCRIBER_STATUS, suid);

		String targetType = (certTypeIdx == 1) ? CertificateType.AUTH.toString() : CertificateType.SIGN.toString();

		return certs.stream()
				.filter(c -> targetType.equals(c.getCertificateType()))
				.findFirst()
				.orElseThrow(() -> new RAServiceException(ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND));
	}


	private ServiceResponse executePkiTransaction(String txType, LogModelDTO logDto) throws RAServiceException {
		try {
			PostRequest post = new PostRequest();
			post.setRequestBody(logDto.toString());
			post.setHashdata(logDto.toString().hashCode());

			RequestEntity request = new RequestEntity();
			request.setPostRequest(post);
			request.setTransactionType(txType);

			// Can throw RestClientException (Connection issues, timeouts, etc.)
			String pkiUrl = PropertiesConstants.getPkiUrl();
			ResponseEntity<String> response = restTemplate.postForEntity(pkiUrl, request, String.class);
			String body = response.getBody();

			if (Constant.TRANSACTION_TYPE_NOT_FOUND.equals(body)) {
				throw new RAServiceException(ErrorCodes.E_TRANSACTION_TYPE_NOT_FOUND);
			}
			if (Constant.REQUEST_IS_NOT_VALID.equals(body)) {
				throw new RAServiceException(ErrorCodes.E_REQUEST_DATA_IS_NOT_VALID);
			}

			// Can throw JsonProcessingException (Jackson parsing error)
			ServiceResponse sr = objectMapper.readValue(body, ServiceResponse.class);

			logDto.setCallStack(null);
			logDto.setLogMessage(Constant.RESPONSE);
			logDto.setEndTime(NativeUtils.getTimeStampString());

			if (Constant.FAIL.equals(sr.getStatus())) {
				logDto.setLogMessageType(LogMessageType.ERROR.toString());
				sendAuditLogSilently(logDto);
				ErrorCodes.setResponse(sr);
				throw new RAServiceException(sr.getErrorMessage());
			}

			logDto.setLogMessageType(LogMessageType.SUCCESS.toString());
			sendAuditLogSilently(logDto);
			return sr;

		} catch (org.springframework.web.client.RestClientException e) {

			throw new RAServiceException("PKI service communication failure during " + txType + " at " + PropertiesConstants.getPkiUrl(), e);

		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {

			throw new RAServiceException("Failed to parse PKI response for transaction: " + txType, e);

		} catch (RAServiceException e) {

			throw e;

		} catch (Exception e) {

			logger.error("{} :: executePkiTransaction unexpected error :: {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(getGenericError());
		}
	}

	/**
	 * Helper to ensure that audit logging failures do not crash the primary business logic.
	 */
	private void sendAuditLogSilently(LogModelDTO logDto) {
		try {
			rabbitMQSender.send(NativeUtils.getLogModel(logDto));
		} catch (Exception e) {
			logger.warn("{} :: Service log delivery failed :: {}", CLASS, e.getMessage());
		}
	}
	private void validatePinTransition(SetPinModel model, RASubscriberCertificatePinHistory history) throws RAServiceException {

		if (!model.isChangePin() && !model.isResetPIN()) {
			return;
		}

		boolean isSign = (model.getCertType() == 0);
		String current = isSign ? history.getSigningCertificatePinList() : history.getAuthenticationCertificatePinList();
		String other = isSign ? history.getAuthenticationCertificatePinList() : history.getSigningCertificatePinList();


		RAServiceAsserts.notNullorEmpty(current, isSign ?
				ErrorCodes.E_SIGNING_CERTIFICATE_PIN_NOT_SET : ErrorCodes.E_AUTHENTICATION_CERTIFICATE_PIN_NOT_SET);


		checkPinUniqueness(model.getSigningPassword(), current, other, isSign);
		processOperationSpecificLogic(model, current, isSign);
	}

	private void checkPinUniqueness(String newPin, String currentList, String otherList, boolean isSign) throws RAServiceException {

		NativeUtils.checkOldPasswords(currentList, newPin, isSign ?
				ErrorCodes.E_NEW_SIGNING_PIN_MATCHED_WITH_OLD_SIGNING_PIN :
				ErrorCodes.E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_OLD_AUTHENTICATION_PIN);

		if (otherList != null) {
			NativeUtils.checkCurrentPassword(otherList, newPin, isSign ?
					ErrorCodes.E_NEW_SIGNING_PIN_MATCHED_WITH_CURRENT_AUTHENTICATION_PIN :
					ErrorCodes.E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_CURRENT_SIGNING_PIN);
		}
	}

	private void processOperationSpecificLogic(SetPinModel model, String currentList, boolean isSign) throws RAServiceException {
		if (model.isChangePin()) {
			NativeUtils.checkOldCurrentPasswords(currentList, model.getOldSigningPassword(), isSign ?
					ErrorCodes.E_SIGNING_PIN_NOT_MATCHED : ErrorCodes.E_AUTH_PIN_NOT_MATCHED);
		}

		if (model.isResetPIN()) {
			List<String> pins = Arrays.asList(currentList.split(", "));
			model.setCurrentSigningPassword(pins.get(pins.size() - 1));
		}
	}

	private void updatePinMetadata(SetPinModel model, RASubscriberCertificates cert, RASubscriberCertificatePinHistory history, ServiceResponse sr) throws RAServiceException {
		try {
			boolean isSign = (model.getCertType() == 0);
			String oldList = isSign ? history.getSigningCertificatePinList() : history.getAuthenticationCertificatePinList();


			String newList = rotatePinHistory(oldList, model.getSigningPassword(), model.isChangePin() || model.isResetPIN());


			if (isSign) {
				history.setSigningCertificatePinList(newList);
				history.setSignPinSetDate(NativeUtils.getTimeStamp());
			} else {
				if (model.isResetPIN()) {
					unlockExternalAuthPin(model.getSubscriberUniqueId());
				}
				history.setAuthenticationCertificatePinList(newList);
				history.setAuthPinSetDate(NativeUtils.getTimeStamp());
			}

			pinHistoryRepository.save(history);

			RASubscriberWrappedKey wk = raSubscriberWrappedKeyRepository.findBycertificateSerialNumber(cert.getCertificateSerialNumber());
			if (wk != null) {
				wk.setWrappedKey(sr.getWrappedKey());
				raSubscriberWrappedKeyRepository.save(wk);
			}


			if (!model.isResetPIN() && !model.isChangePin()) {
				checkAndUpdateSubscriberStatus(model.getSubscriberUniqueId(), history);
			}

		} catch (java.text.ParseException | jakarta.persistence.PersistenceException e) {
			logger.error("{} :: updatePinMetadata DB/Parsing error for SUID {}: {}", CLASS, model.getSubscriberUniqueId(), e.getMessage());
			throw new RAServiceException(getGenericError());

		} catch (RAServiceException e) {

			throw e;

		} catch (Exception e) {
			logger.error("{} :: updatePinMetadata unexpected error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	private String rotatePinHistory(String currentList, String newPin, boolean isHistoryOp) {
		if (currentList == null || currentList.isEmpty()) return newPin;
		List<String> pins = new LinkedList<>(Arrays.asList(currentList.split(", ")));
		if (isHistoryOp && pins.size() >= pinHistorySize) {
			pins.remove(0);
		}
		pins.add(newPin);
		return String.join(", ", pins);
	}
	private void checkAndUpdateSubscriberStatus(String suid, RASubscriberCertificatePinHistory history) throws RAServiceException {
		try {
			if (history.getSignPinSetDate() != null && history.getAuthPinSetDate() != null) {
				RASubscriberStatusModel status = raSubscriberStatusRepository.findBysubscriberUid(suid);

				if (status != null) {
					status.setSubscriberStatus(Constant.SUBSCRIBER_STATUS);
					status.setUpdatedDate(NativeUtils.getTimeStamp());
					status.setSubscriberStatusDescription(Constant.SET_PIN_SUCCESS);
					raSubscriberStatusRepository.save(status);
				}
			}
		} catch (java.text.ParseException | jakarta.persistence.PersistenceException e) {

			logger.error("{} :: checkAndUpdateSubscriberStatus failure for SUID {}: {}", CLASS, suid, e.getMessage());


			throw new RAServiceException(getGenericError());
		}
	}

	private void unlockExternalAuthPin(String suid) {
		try {
			String url = urlunlockedAuthResetPin + "/" + suid;
			secureUrlValidator.validate(url);
			restTemplate.postForEntity(url, null, ApiResponses.class);
		} catch (Exception e) {
			logger.warn("{} :: External unlock failed for SUID: {}", CLASS, suid);
		}
	}

	private LogModelDTO prepareBaseLog(String identifier, TransactionType type) {
		LogModelDTO dto = new LogModelDTO();
		dto.setStartTime(NativeUtils.getTimeStampString());
		dto.setIdentifier(identifier);
		dto.setLogMessage(Constant.REQUEST);
		dto.setLogMessageType(LogMessageType.INFO.toString());
		dto.setTransactionType(type.toString());
		dto.setCorrelationID(NativeUtils.getUUId());
		dto.setTransactionID(NativeUtils.getUUId());
		dto.seteSealUsed(false);
		return dto;
	}

	private RASubscriberCertificatePinHistory getPinHistory(String suid) {
		RASubscriberCertificatePinHistory h = pinHistoryRepository.findBysubscriberUniqueId(suid);
		if (h == null) {
			h = new RASubscriberCertificatePinHistory();
			h.setSubscriberUniqueId(suid);
		}
		return h;
	}

	private RAServiceException handleDatabaseException(HibernateException e) {
		logger.error("{} :: Database Error: {}", CLASS, e.getMessage());
		return new RAServiceException(getGenericError());
	}

	private String getGenericError() {
		return messageSource.getMessage(ERR_KEY, null, Locale.ENGLISH);
	}
}