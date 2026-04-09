
package ug.daes.ra.service.iface.implementation;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import ug.daes.ra.asserts.RAServiceAsserts;
import ug.daes.ra.config.SecureUrlValidator;
import ug.daes.ra.dto.*;
import ug.daes.ra.enums.*;
import ug.daes.ra.exception.ErrorCodes;
import ug.daes.ra.exception.RAServiceException;
import ug.daes.ra.model.*;
import ug.daes.ra.repository.iface.*;
import ug.daes.ra.request.entity.*;
import ug.daes.ra.request.entity.RequestEntity;
import ug.daes.ra.response.entity.CertificateData;
import ug.daes.ra.response.entity.ServiceResponse;
import ug.daes.ra.service.iface.RAServiceIface;
import ug.daes.ra.utils.Constant;
import ug.daes.ra.utils.NativeUtils;
import ug.daes.ra.utils.PropertiesConstants;
import ug.daes.ra.utils.RaKafkaSender;

import java.io.IOException;
import java.text.ParseException;

import java.util.*;

@Service
@EnableScheduling
public class RAServiceIfaceImpl implements RAServiceIface {

	private static final String CLASS = "RAServiceIfaceImpl";
	private static final Logger logger = LoggerFactory.getLogger(RAServiceIfaceImpl.class);
	private static final String SOMETHING_WENT_WRONG = "api.error.something.went.wrong.please.try.after.sometime";

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate;
	private final RaKafkaSender rabbitMQSender;
	private final RaSubscriberCertificatesRepository raSubscriberCertificatesRepository;
	private final RaSubscriberCertificatePinHistoryRepository pinHistoryRepository;
	private final SubscriberRADataRepository subscriberRADataRepository;
	private final RaSubscriberCertificateLifeCycleRepository lifeCycleRepository;
	private final RaSubscriberRepository raSubscriberRepository;
	private final RaSubscriberStatusRepository raSubscriberStatusRepository;
	private final RaSubscriberFcmTokenRepository raSubscriberFcmTokenRepository;
	private final RaSubscriberWrappedKeyRepository raSubscriberWrappedKeyRepository;
	private final RaOrganizationCertificatesRepository raOrganizationCertificatesRepository;
	private final RaOrganizationDetailsRepository raOrganizationDetailsRepository;
	private final MessageSource messageSource;
	private final SecureUrlValidator secureUrlValidator;

	@Autowired
	public RAServiceIfaceImpl(RestTemplate restTemplate,
							  RaKafkaSender rabbitMQSender,
							  RaSubscriberCertificatesRepository raSubscriberCertificatesRepository,
							  RaOrganizationCertificatesRepository raOrganizationCertificatesRepository,
							  RaOrganizationDetailsRepository raOrganizationDetailsRepository,
							  RaSubscriberCertificatePinHistoryRepository pinHistoryRepository,
							  SubscriberRADataRepository subscriberRADataRepository,
							  RaSubscriberCertificateLifeCycleRepository lifeCycleRepository,
							  RaSubscriberRepository raSubscriberRepository,
							  RaSubscriberStatusRepository raSubscriberStatusRepository,
							  RaSubscriberFcmTokenRepository raSubscriberFcmTokenRepository,
							  RaSubscriberWrappedKeyRepository raSubscriberWrappedKeyRepository,
							  MessageSource messageSource,
							  SecureUrlValidator secureUrlValidator) {
		this.restTemplate = restTemplate;
		this.rabbitMQSender = rabbitMQSender;
		this.raSubscriberCertificatesRepository = raSubscriberCertificatesRepository;
		this.pinHistoryRepository = pinHistoryRepository;
		this.subscriberRADataRepository = subscriberRADataRepository;
		this.lifeCycleRepository = lifeCycleRepository;
		this.raSubscriberRepository = raSubscriberRepository;
		this.raSubscriberStatusRepository = raSubscriberStatusRepository;
		this.raSubscriberFcmTokenRepository = raSubscriberFcmTokenRepository;
		this.raSubscriberWrappedKeyRepository = raSubscriberWrappedKeyRepository;
		this.messageSource = messageSource;
		this.secureUrlValidator = secureUrlValidator;
		this.raOrganizationCertificatesRepository = raOrganizationCertificatesRepository;
		this.raOrganizationDetailsRepository = raOrganizationDetailsRepository;
	}

	@Override
	public String issueCertificate(RARequestDTO request) throws RAServiceException {
		try {
			validateSubscriberStatus(request.getSubscriberUniqueId());
			RASubscriberRaData raData = validateRaData(request.getSubscriberUniqueId());

			List<RASubscriberCertificates> certificates = getActiveCertificates(request.getSubscriberUniqueId());

			if (certificates.size() == 2) {
				throw new RAServiceException(ErrorCodes.E_SUBSCRIBER_CERTIFICATES_ARE_ACTIVE);
			}

			if (certificates.isEmpty()) {
				processNewCertificatePair(request, raData);
			} else {
				processSingleExistingCertificate(request, certificates.get(0), raData);
			}

			return Constant.REQUEST_FOR_ISSUING_SIGN_AND_AUTH_PROCESS_SUCCESSFULLY;
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: issueCertificate error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String revokeCertificate(RARequestDTO requestBody) throws RAServiceException {
		try {
			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(requestBody.getSubscriberUniqueId());
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			List<RASubscriberCertificates> activeCerts = getActiveCertificates(requestBody.getSubscriberUniqueId());
			if (activeCerts.isEmpty()) {
				throw new RAServiceException(ErrorCodes.E_SUBSCRIBER_CERTIFICATES_ARE_REVOKED);
			}

			String reason = getRevocationReasonName(requestBody.getReasonId());

			for (RASubscriberCertificates cert : activeCerts) {
				executeRevocation(cert, requestBody, reason);
			}

			updateSubscriberStatus(requestBody.getSubscriberUniqueId(), Constant.CERT_REVOKED, Constant.CERTIFICATES_ARE_REVOKED_SUCCESSFULLY);
			sendStatusNotification(requestBody.getSubscriberUniqueId(), subscriber.getFullName(), CertificateStatus.REVOKED);

			return Constant.SUCCESS;
		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: revokeCertificate error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	@Scheduled(cron = "0 0 0 * * ?")
	public String checkCertificateStatus() throws RAServiceException {
		try {
			logger.info("{} :: checkCertificateStatus() :: start", CLASS);
			List<RASubscriberCertificates> expiredCerts = raSubscriberCertificatesRepository.findByCertificateStatusExpired();

			for (RASubscriberCertificates cert : expiredCerts) {
				processExpiredCertificate(cert);
			}

			return Constant.COMPLETED;
		} catch (Exception e) {
			logger.error("{} :: checkCertificateStatus unexpected error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String issueCertificateCallBack(Map<String, String> response) throws RAServiceException {
		try {
			ServiceResponse serviceResponse = objectMapper.readValue(response.get(Constant.CALLBACK_RESPONSE), ServiceResponse.class);
			String suid = response.get(Constant.CALLBACK_SUID);

			if (Constant.CALLBACK_SUCCESS.equals(serviceResponse.getStatus())) {
				processSuccessCallback(response, serviceResponse, suid);
			} else {
				processFailureCallback(response, serviceResponse, suid);
			}

			return serviceResponse.getStatus();
		} catch (IOException | PersistenceException e) {
			logger.error("{} :: issueCertificateCallBack error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public String verifyCertficatesPins(VerifyCertificatesPins certificatesPins) throws RAServiceException {
		try {
			getSubscriberOrThrow(certificatesPins.getSubscriberUId());
			List<RASubscriberCertificates> certificates = getActiveCertificates(certificatesPins.getSubscriberUId());

			if (certificates.isEmpty()) {
				throw new RAServiceException(ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);
			}

			for (RASubscriberCertificates cert : certificates) {
				verifySingleCertificatePin(cert, certificatesPins);
			}

			return Constant.PIN_VERIFICATION_SUCCESS;
		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: verifyCertficatesPins error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public String getCertificateDetailsBySubscriberUniqueId(String suid) throws RAServiceException {
		try {
			RASubscriber sub = raSubscriberRepository.findBysubscriberUid(suid);
			RAServiceAsserts.notNullorEmpty(sub, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			RASubscriberCertificates cert = raSubscriberCertificatesRepository.findTopBySubscriberUniqueIdOrderByCreationDateDesc(suid);
			RAServiceAsserts.notNullorEmpty(cert, ErrorCodes.E_CERTIFICATES_NOT_ISSUED);

			CertificateData data = new CertificateData();
			data.setCertStatus(cert.getCertificateStatus());
			data.setExpiryDate(cert.getCertificateEndDate().toString().split(" ")[0]);
			data.setIssueDate(cert.getCertificateStartDate().toString().split(" ")[0]);
			data.setStatus(true);

			return data.toString();
		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		}
	}

	@Override
	public String getCertificateLifeCycleLogsBySubscriberUniqueId(String suid) throws RAServiceException {
		try {
			getSubscriberOrThrow(suid);
			List<RASubscriberCertificateLifeCycle> logs = lifeCycleRepository.findBysubscriberUniqueId(suid);
			RAServiceAsserts.notNullorEmpty(logs.size(), Constant.CERTIFICATE_LOGS_NOT_FOUND);
			return logs.toString();
		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		}
	}



	private void executeRevocation(RASubscriberCertificates cert, RARequestDTO body, String reason) throws RAServiceException {
		try {
			LogModelDTO log = createBaseLogModel(cert.getSubscriberUniqueId(), ServiceName.CERTIFICATE_REVOKED);

			RevokeCertificateRequest req = new RevokeCertificateRequest();
			req.setReasonId(body.getReasonId());
			req.setSerialNumber(cert.getCertificateSerialNumber());
			log.setCallStack(req.toString());

			ServiceResponse res = callPkiCore(Constant.REVOKE_CERTIFICATE, log);

			if (Constant.FAIL.equals(res.getStatus())) {
				ErrorCodes.setResponse(res);
				throw new RAServiceException(res.getErrorMessage());
			}

			cert.setCertificateStatus(CertificateStatus.REVOKED.toString());
			cert.setRevocationReason(reason);
			cert.setUpdatedDate(NativeUtils.getTimeStamp());
			raSubscriberCertificatesRepository.save(cert);

			saveLifeCycleRecord(cert, reason, CertificateStatus.REVOKED);

		} catch (ParseException | PersistenceException e) {
			logger.error("{} :: executeRevocation DB/Parsing Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	private void processExpiredCertificate(RASubscriberCertificates cert) throws RAServiceException {
		try {
			cert.setCertificateStatus(CertificateStatus.EXPIRED.toString());
			cert.setUpdatedDate(NativeUtils.getTimeStamp());
			raSubscriberCertificatesRepository.save(cert);

			saveLifeCycleRecord(cert, null, CertificateStatus.EXPIRED);
			updateSubscriberStatus(cert.getSubscriberUniqueId(), Constant.CERT_EXPIRED, Constant.CERTIFICATES_ARE_EXPIRED);

			RASubscriber sub = raSubscriberRepository.findBysubscriberUid(cert.getSubscriberUniqueId());
			if (sub != null) {
				sendStatusNotification(cert.getSubscriberUniqueId(), sub.getFullName(), CertificateStatus.EXPIRED);
			}

			auditAction(cert.getSubscriberUniqueId(), null, LogMessageType.SUCCESS, "Certificate Expired Audit");

		} catch (ParseException | PersistenceException e) {
			logger.error("{} :: processExpiredCertificate Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	private void verifySingleCertificatePin(RASubscriberCertificates cert, VerifyCertificatesPins pins) throws RAServiceException {
		try {
			RASubscriberCertificatePinHistory history = pinHistoryRepository.findBysubscriberUniqueId(cert.getSubscriberUniqueId());
			RASubscriberWrappedKey wrappedKey = raSubscriberWrappedKeyRepository.findBycertificateSerialNumber(cert.getCertificateSerialNumber());

			RAServiceAsserts.notNullorEmpty(history, ErrorCodes.E_SUBSCRIBER_STATUS_DATA_NOT_FOUND);
			RAServiceAsserts.notNullorEmpty(wrappedKey, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			LogModelDTO log = createBaseLogModel(pins.getSubscriberUId(), null);

			String pinStr = CertificateType.SIGN.toString().equals(cert.getCertificateType())
					? history.getSigningCertificatePinList() : history.getAuthenticationCertificatePinList();

			List<String> pinList = Arrays.asList(pinStr.split(", "));
			pins.setCurrentSigningPassword(pinList.get(pinList.size() - 1));
			pins.setWrappedKey(wrappedKey.getWrappedKey());

			log.setCallStack(pins.toString());
			ServiceResponse res = callPkiCore(Constant.VERIFY_PIN, log);

			if (Constant.FAIL.equals(res.getStatus())) {
				ErrorCodes.setResponse(res);
				throw new RAServiceException(res.getErrorMessage());
			}

			auditAction(pins.getSubscriberUId(), null, LogMessageType.SUCCESS, "PIN Verified");

		} catch (PersistenceException e) {
			logger.error("{} :: verifySingleCertificatePin DB Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}
	private ServiceResponse callPkiCore(String txType, LogModelDTO log) throws RAServiceException {
		try {
			PostRequest post = new PostRequest();
			post.setRequestBody(log.toString());
			post.setHashdata(log.toString().hashCode());

			RequestEntity entity = new RequestEntity();
			entity.setPostRequest(post);
			entity.setTransactionType(txType);

			secureUrlValidator.validate(PropertiesConstants.getPkiUrl());
			ResponseEntity<String> resp = restTemplate.postForEntity(PropertiesConstants.getPkiUrl(), entity, String.class);

			if (Constant.TRANSACTION_TYPE_NOT_FOUND.equals(resp.getBody())) {
				throw new RAServiceException(ErrorCodes.E_TRANSACTION_TYPE_NOT_FOUND);
			}

			return objectMapper.readValue(resp.getBody(), ServiceResponse.class);

		} catch (IOException e) {
			logger.error("{} :: callPkiCore IO/JSON Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: callPkiCore Unexpected Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	private void saveLifeCycleRecord(RASubscriberCertificates cert, String reason, CertificateStatus status) throws RAServiceException {
		try {
			RASubscriberCertificateLifeCycle lc = new RASubscriberCertificateLifeCycle();
			lc.setCertificateSerialNumber(cert.getCertificateSerialNumber());
			lc.setCertificateStatus(status.toString());
			lc.setRevokedReason(reason);
			lc.setSubscriberUniqueId(cert.getSubscriberUniqueId());
			lc.setCertificateType(cert.getCertificateType());

			// NativeUtils.getTimeStamp() usually throws ParseException
			lc.setCreationDate(NativeUtils.getTimeStamp());

			lifeCycleRepository.save(lc);
		} catch (ParseException  | PersistenceException e) {
			logger.error("{} :: saveLifeCycleRecord error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}
	private void updateSubscriberStatus(String suid, String status, String desc) throws RAServiceException {
		try {
			RASubscriberStatusModel model = raSubscriberStatusRepository.findBysubscriberUid(suid);
			if (model != null) {
				model.setSubscriberStatus(status);
				model.setSubscriberStatusDescription(desc);
				model.setUpdatedDate(NativeUtils.getTimeStamp());
				raSubscriberStatusRepository.save(model);
			}
		} catch (ParseException | PersistenceException e) {
			logger.error("{} :: updateSubscriberStatus error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}
	private void sendStatusNotification(String suid, String name, CertificateStatus status) {
		try {
			String token = raSubscriberFcmTokenRepository.findBysubscriberUid(suid);
			NotificationDataDTO data = new NotificationDataDTO();
			data.setTitle(Constant.HI + name);

			NotificationContextDTO ctx = new NotificationContextDTO();
			ctx.setPrefCertificateStatus(status);
			data.setNotificationContext(ctx);

			String body = status == CertificateStatus.REVOKED ? Constant.YOUR_CERTIFICATES_ARE_REVOKED_SUCCESSFULLY : Constant.YOUR + "Certificate" + Constant.CERTIFIACTE_IS_EXPIRED;
			data.setBody(body);

			PushNotificationRequest req = new PushNotificationRequest();
			req.setTo(token);
			req.setPriority(Constant.HIGH);
			req.setData(status == CertificateStatus.REVOKED ? data.getRevokeCertNotificationData() : data.getExpiredCertNotificationData());

			restTemplate.postForEntity(PropertiesConstants.getNotification(), new HttpEntity<>(req.getNotificationRquest(), createJsonHeaders()), String.class);
		} catch (Exception e) {
			logger.error("Notification failed for SUID: {}", suid);
		}
	}

	// --- Base Utility Helpers ---

	private LogModelDTO createBaseLogModel(String suid, ServiceName service) {
		LogModelDTO log = new LogModelDTO();
		log.setStartTime(NativeUtils.getTimeStampString());
		log.setIdentifier(suid);
		log.setTransactionType(TransactionType.BUSINESS.toString());
		log.setLogMessageType(LogMessageType.INFO.toString());
		log.setCorrelationID(NativeUtils.getUUId());
		log.setTransactionID(NativeUtils.getUUId());
		log.setServiceName(service != null ? service.toString() : null);
		return log;
	}

	private void auditAction(String suid, ServiceName service, LogMessageType type, String msg) {
		LogModelDTO log = createBaseLogModel(suid, service);
		log.setLogMessage(msg);
		log.setLogMessageType(type.toString());
		log.setEndTime(NativeUtils.getTimeStampString());
		try {
			rabbitMQSender.send(NativeUtils.getLogModel(log));
		} catch (Exception e) {
			logger.warn("Audit failed");
		}
	}

	private RAServiceException handleDatabaseException(HibernateException e) {
		logger.error("{} :: Database Exception :: {}", CLASS, e.getMessage());
		return new RAServiceException(getGenericError());
	}

	private String getGenericError() {
		return messageSource.getMessage(SOMETHING_WENT_WRONG, null, Locale.ENGLISH);
	}

	private HttpHeaders createJsonHeaders() {
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.APPLICATION_JSON);
		return h;
	}

	// Standard business logic validations
	private void validateSubscriberStatus(String suid) throws RAServiceException {
		RASubscriberStatusModel status = raSubscriberStatusRepository.findBysubscriberUid(suid);
		RAServiceAsserts.notNullorEmpty(status, ErrorCodes.E_SUBSCRIBER_STATUS_DATA_NOT_FOUND);
	}

	private RASubscriberRaData validateRaData(String suid) throws RAServiceException {
		RASubscriberRaData raData = subscriberRADataRepository.findBysubscriberUniqueId(suid);
		RAServiceAsserts.notNullorEmpty(raData, ErrorCodes.E_SUBSCRIBER_RA_DATA_NOT_FOUND);
		return raData;
	}

	private List<RASubscriberCertificates> getActiveCertificates(String suid) {
		return raSubscriberCertificatesRepository.findByCertificateStatusAndsubscriberUniqueId(CertificateStatus.ACTIVE.toString(), suid);
	}

	private RASubscriber getSubscriberOrThrow(String suid) throws RAServiceException {
		RASubscriber sub = raSubscriberRepository.findBysubscriberUid(suid);
		RAServiceAsserts.notNullorEmpty(sub, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);
		return sub;
	}

	private String determineCertType(String type) throws RAServiceException {
		if ("AUTH".equals(type)) return CertificateType.AUTH.toString();
		if ("SIGN".equals(type)) return CertificateType.SIGN.toString();
		throw new RAServiceException(ErrorCodes.E_CERTIFICATE_TYPE_NOT_FOUND);
	}

	private String getRevocationReasonName(String id) throws RAServiceException {
		return switch (id) {
			case "1" -> RevokeReason.KEY_COMPROMISED.toString();
			case "-2" -> RevokeReason.NO_REASON.toString();
			case "3" -> RevokeReason.AFFILIATION_CHANGED.toString();
			case "4" -> RevokeReason.SUPERSEDED.toString();
			case "5" -> RevokeReason.CESSATION_OF_OPERATION.toString();
			case "6" -> RevokeReason.CERTIFICATE_HOLD.toString();
			case "9" -> RevokeReason.PRIVILEGE_WITHDRAWN.toString();
			default -> throw new RAServiceException(ErrorCodes.E_REVOKE_REASON_NOT_FOUND);
		};
	}

	// --- Callback Logic Helpers ---
	private void processSuccessCallback(Map<String, String> response, ServiceResponse sr, String suid) throws RAServiceException {
		try {
			RASubscriberCertificates cert = new RASubscriberCertificates();
			cert.setPkiKeyId(response.get(Constant.PKI_KEY_ID));
			cert.setCertificateData(sr.getCertificate());
			cert.setCertificateStatus(CertificateStatus.ACTIVE.toString());
			cert.setCertificateType(determineCertType(response.get(Constant.CALLBACK_CERT_TYPE)));
			cert.setCertificateSerialNumber(sr.getCertificateSerialNumber());

			// Handle ParseException from NativeUtils
			cert.setCertificateStartDate(NativeUtils.getTimeStamp(sr.getIssueDate()));
			cert.setCertificateEndDate(NativeUtils.getTimeStamp(sr.getExpiryDate()));

			cert.setSubscriberUniqueId(suid);
			cert.setCreationDate(NativeUtils.getTimeStamp());
			raSubscriberCertificatesRepository.save(cert);

			RASubscriberWrappedKey wrapped = new RASubscriberWrappedKey();
			wrapped.setCertificateSerialNumber(sr.getCertificateSerialNumber());
			wrapped.setWrappedKey(sr.getWrappedKey());
			raSubscriberWrappedKeyRepository.save(wrapped);

			saveLifeCycleRecord(cert, null, CertificateStatus.ACTIVE);
			checkAndFinalizeIssuance(suid);

		} catch (ParseException  | PersistenceException e) {
			logger.error("{} :: processSuccessCallback error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}
	private void processFailureCallback(Map<String, String> response, ServiceResponse sr, String suid) throws RAServiceException {
		try {
			saveLifeCycleRecordSimple(suid, sr.getStatus(), determineCertType(response.get(Constant.CALLBACK_CERT_TYPE)));
			auditAction(suid, ServiceName.CERTIFICATE_PAIR_ISSUED, LogMessageType.ERROR, "Issuance Failed Callback");
		} catch (Exception e) {
			throw new RAServiceException(getGenericError());
		}
	}

	private void saveLifeCycleRecordSimple(String suid, String status, String type) throws RAServiceException {
		try {
			RASubscriberCertificateLifeCycle lc = new RASubscriberCertificateLifeCycle();
			lc.setSubscriberUniqueId(suid);
			lc.setCertificateStatus(status);
			lc.setCertificateType(type);

			lc.setCreationDate(NativeUtils.getTimeStamp());

			lifeCycleRepository.save(lc);
		} catch (ParseException | PersistenceException e) {
			logger.error("{} :: saveLifeCycleRecordSimple error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	private void checkAndFinalizeIssuance(String suid) throws RAServiceException {
		try {
			List<RASubscriberCertificates> certs = getActiveCertificates(suid);
			if (certs.size() == 2) {
				updateSubscriberStatus(suid, Constant.PIN_SET_REQUIRED, Constant.CERTIFICATES_ISSUED_SUCCESSFULLY);

				RASubscriber sub = raSubscriberRepository.findBysubscriberUid(suid);
				sendStatusNotification(suid, sub.getFullName(), CertificateStatus.ACTIVE);

				auditAction(suid, ServiceName.CERTIFICATE_PAIR_ISSUED, LogMessageType.SUCCESS, "Issuance Completed");
			}
		} catch (RAServiceException e) {
			throw e; // Pass through existing business exceptions
		} catch (Exception e) {
			logger.error("{} :: checkAndFinalizeIssuance unexpected error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	private void processNewCertificatePair(RARequestDTO request, RASubscriberRaData raData) throws RAServiceException {
		processCertificateRequest(request, NativeUtils.generatePKIKeyId(), CertificateType.SIGN, raData);
		processCertificateRequest(request, NativeUtils.generatePKIKeyId(), CertificateType.AUTH, raData);
	}

	private void processSingleExistingCertificate(RARequestDTO request, RASubscriberCertificates existing, RASubscriberRaData raData) throws RAServiceException {
		CertificateType next = CertificateType.AUTH.toString().equals(existing.getCertificateType()) ? CertificateType.SIGN : CertificateType.AUTH;
		processCertificateRequest(request, NativeUtils.generatePKIKeyId(), next, raData);
	}

	private void processCertificateRequest(RARequestDTO request, String keyId, CertificateType type, RASubscriberRaData raData) throws RAServiceException {
		IssueCertificateRequest issueRequest = new IssueCertificateRequest();
		issueRequest.setSubscriberUniqueId(request.getSubscriberUniqueId());
		issueRequest.setKeyID(keyId);
		issueRequest.setCommonName(raData.getCommonName());
		issueRequest.setCountryName(raData.getCountryName());

		PostRequest postRequest = new PostRequest();
		postRequest.setRequestBody(issueRequest.toString());
		postRequest.setHashdata(issueRequest.toString().hashCode());
		postRequest.setPkiKeyID(keyId);
		postRequest.setCertificateType(type.toString());
		postRequest.setCallbackURI(PropertiesConstants.getIssueCertificateCallbackUrl());

		try {
			callPkiCore(Constant.ISSUE_CERTIFICATE, createBaseLogModel(request.getSubscriberUniqueId(), null));
		} catch (Exception e) {
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ApiResponse expireSubscriberCert(ExpireSubscriberCertRequestDTO expireDto) throws RAServiceException {
		try {
			RASubscriber sub = (expireDto.getEmail() != null && !expireDto.getEmail().isEmpty())
					? (RASubscriber) raSubscriberRepository.getbyEmailId(expireDto.getEmail())
					: raSubscriberRepository.findLatestByMobileNo(expireDto.getMobileNumber());

			RAServiceAsserts.notNullorEmpty(sub, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			List<RASubscriberCertificates> certs = raSubscriberCertificatesRepository.findBySubscriberUniqueIdToExpireCert(sub.getSubscriberUid());
			RAServiceAsserts.notNullorEmpty(certs, ErrorCodes.E_CERTIFICATES_NOT_ISSUED);

			for (RASubscriberCertificates cert : certs) {
				if ("EXPIRED".equals(cert.getCertificateStatus())) continue;
				processExpiredCertificate(cert);
			}
			return AppUtil.createApiResponse(true, "Certificate Expired successfully", null);
		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (Exception e) {
			throw new RAServiceException(getGenericError());
		}
	}


	@Override
	public String getCertificateDataByCertificateType(String subscriberUid, String certType) throws RAServiceException {
		try {
			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(subscriberUid);
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			RASubscriberCertificates cert = raSubscriberCertificatesRepository
					.findByCertificateStatusAndsubscriberUniqueIdAndCertificateType(
							CertificateStatus.ACTIVE.toString(), subscriberUid, certType);

			RAServiceAsserts.notNullorEmpty(cert, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			if (cert.getCertificateType().equals(certType)) {
				return cert.getCertificateData();
			} else {
				throw new RAServiceException(ErrorCodes.E_CERTIFICATE_TYPE_NOT_FOUND);
			}
		} catch (PersistenceException e) {
			logger.error("{} :: getCertificateDataByCertificateType :: Database Exception: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: getCertificateDataByCertificateType :: Unexpected Error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public String getOrganizationCertificateDataByCertificateType(String orgId, String certType) throws RAServiceException {
		try {
			logger.info("{} :: getOrganizationCertificateDataByCertificateType(), orgId >> {} certType >> {}", CLASS, orgId, certType);

			RAOrganizationDetails org = raOrganizationDetailsRepository.findByOrganizationUid(orgId);
			RAServiceAsserts.notNullorEmpty(org, ErrorCodes.E_ORGANIZATION_DATA_NOT_FOUND);

			RAOrganizationCertificates cert = raOrganizationCertificatesRepository
					.findByCertificateStatusAndOrganizationUniqueId(CertificateStatus.ACTIVE.toString(), orgId);

			RAServiceAsserts.notNullorEmpty(cert, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			if (cert.getCertificateType().equals(certType)) {
				return cert.getCertificateData();
			} else {
				throw new RAServiceException(ErrorCodes.E_CERTIFICATE_TYPE_NOT_FOUND);
			}
		} catch (PersistenceException e) {
			logger.error("{} :: getOrganizationCertificateDataByCertificateType :: Database Exception: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: getOrganizationCertificateDataByCertificateType :: Unexpected Error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public ApiResponse getCertificateDataByCertificateTypeForAgent(String subscriberUid, String certType) throws RAServiceException {
		try {
			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(subscriberUid);
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			RASubscriberCertificates cert = raSubscriberCertificatesRepository
					.findByCertificateStatusAndsubscriberUniqueIdAndCertificateType(
							CertificateStatus.ACTIVE.toString(), subscriberUid, certType);

			RAServiceAsserts.notNullorEmpty(cert, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			RASubscriberWrappedKey wrappedKey = raSubscriberWrappedKeyRepository
					.findBycertificateSerialNumber(cert.getCertificateSerialNumber());

			SubscriberCertForAgentDto dto = new SubscriberCertForAgentDto();
			dto.setSubscriberCertificate(cert.getCertificateData());
			dto.setWrappedKey(wrappedKey != null ? wrappedKey.getWrappedKey() : null);

			if (cert.getCertificateType().equals(certType)) {
				return AppUtil.createApiResponse(true,
						messageSource.getMessage("api.response.subcriber.certificate.details", null, Locale.ENGLISH),
						dto);
			} else {
				throw new RAServiceException(ErrorCodes.E_CERTIFICATE_TYPE_NOT_FOUND);
			}
		} catch (PersistenceException e) {
			logger.error("{} :: getCertificateDataByCertificateTypeForAgent :: Database Exception: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: getCertificateDataByCertificateTypeForAgent :: Unexpected Error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}

	@Override
	public ApiResponse getOrganizationCertificateDataByCertificateTypeForAgent(String orgId, String certType) throws RAServiceException {
		try {
			logger.info("{} :: getOrganizationCertificateDataByCertificateTypeForAgent(), orgId >> {} certType >> {}", CLASS, orgId, certType);

			RAOrganizationDetails org = raOrganizationDetailsRepository.findByOrganizationUid(orgId);
			RAServiceAsserts.notNullorEmpty(org, ErrorCodes.E_ORGANIZATION_DATA_NOT_FOUND);

			RAOrganizationCertificates cert = raOrganizationCertificatesRepository
					.findByCertificateStatusAndOrganizationUniqueId(CertificateStatus.ACTIVE.toString(), orgId);

			RAServiceAsserts.notNullorEmpty(cert, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			SubscriberCertForAgentDto dto = new SubscriberCertForAgentDto();
			dto.setOrganizationCertificate(cert.getCertificateData());
			dto.setOrgWrappedKey(cert.getWrappedKey());

			if (cert.getCertificateType().equals(certType)) {
				return AppUtil.createApiResponse(true,
						messageSource.getMessage("api.response.organization.certificate", null, Locale.ENGLISH),
						dto);
			} else {
				throw new RAServiceException(ErrorCodes.E_CERTIFICATE_TYPE_NOT_FOUND);
			}
		} catch (PersistenceException e) {
			logger.error("{} :: getOrganizationCertificateDataByCertificateTypeForAgent :: Database Exception: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: getOrganizationCertificateDataByCertificateTypeForAgent :: Unexpected Error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericError());
		}
	}
	/**
	 * Gets the revoke reasons.
	 *
	 * @return the revoke reasons in JSON format
	 */
	public String getRevokeReasons() {
		return """
        [
          {"index": "1",  "reason": "KEY_COMPROMISED"},
          {"index": "-2", "reason": "NO_REASON_CODE"},
          {"index": "3",  "reason": "AFFILIATION_CHANGED"},
          {"index": "4",  "reason": "SUPERSEDED"},
          {"index": "5",  "reason": "CESSATION_OF_OPERATION"},
          {"index": "6",  "reason": "CERTIFICATE_HOLD"},
          {"index": "9",  "reason": "PRIVILEGE_WITHDRAWN"}
        ]
        """;
	}

}