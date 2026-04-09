package ug.daes.ra.service.iface.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ug.daes.ra.asserts.RAServiceAsserts;
import ug.daes.ra.dto.LogModelDTO;
import ug.daes.ra.enums.CertificateStatus;
import ug.daes.ra.enums.LogMessageType;
import ug.daes.ra.enums.ServiceName;
import ug.daes.ra.exception.ErrorCodes;
import ug.daes.ra.exception.RAServiceException;
import ug.daes.ra.model.*;
import ug.daes.ra.repository.iface.*;
import ug.daes.ra.request.entity.GenerateSignature;
import ug.daes.ra.request.entity.PostRequest;
import ug.daes.ra.request.entity.RequestEntity;
import ug.daes.ra.response.entity.ServiceResponse;
import ug.daes.ra.service.iface.RALocalGenrateSignatureIface;
import ug.daes.ra.utils.Constant;
import ug.daes.ra.utils.NativeUtils;
import ug.daes.ra.utils.PropertiesConstants;
import ug.daes.ra.utils.RaKafkaSender;
import java.util.*;
import org.hibernate.HibernateException;

@Service
public class RALocalGenrateSignatureIfaceImpl implements RALocalGenrateSignatureIface {

	private static final Logger logger = LoggerFactory.getLogger(RALocalGenrateSignatureIfaceImpl.class);
	private static final String CLASS = "RALocalGenrateSignatureIfaceImpl";
	private static final String ERR_SOMETHING_WENT_WRONG = "api.error.something.went.wrong.please.try.after.sometime";
	private static final String SUBSCRIBER_ID = "subscriberUniqueId";
	private final ObjectMapper objectMapper;
	private final RaKafkaSender rabbitMQSender;
	private final RestTemplate restTemplate;
	private final RaSubscriberRepository raSubscriberRepository;
	private final RaSubscriberCertificatesRepository raSubscriberCertificatesRepository;
	private final RaSubscriberCertificatePinHistoryRepository pinHistoryRepository;
	private final RaSubscriberWrappedKeyRepository raSubscriberWrappedKeyRepository;
	private final RaOrganizationDetailsRepository raOrganizationDetailsRepository;
	private final RaOrganizationCertificatesRepository raOrganizationCertificatesRepository;
	private final RaOrganizationWrappedKeyRepository raOrganizationWrappedKeyRepository;
	private final MessageSource messageSource;

	public RALocalGenrateSignatureIfaceImpl(
            ObjectMapper objectMapper, RaKafkaSender rabbitMQSender, RestTemplate restTemplate,
            RaSubscriberRepository raSubscriberRepository, RaSubscriberCertificatesRepository raSubscriberCertificatesRepository,
            RaSubscriberCertificatePinHistoryRepository pinHistoryRepository, RaSubscriberWrappedKeyRepository raSubscriberWrappedKeyRepository,
            RaOrganizationDetailsRepository raOrganizationDetailsRepository, RaOrganizationCertificatesRepository raOrganizationCertificatesRepository,
            RaOrganizationWrappedKeyRepository raOrganizationWrappedKeyRepository, MessageSource messageSource) {
		this.objectMapper = objectMapper;
		this.rabbitMQSender = rabbitMQSender;
		this.restTemplate = restTemplate;
		this.raSubscriberRepository = raSubscriberRepository;
		this.raSubscriberCertificatesRepository = raSubscriberCertificatesRepository;
		this.pinHistoryRepository = pinHistoryRepository;
		this.raSubscriberWrappedKeyRepository = raSubscriberWrappedKeyRepository;
		this.raOrganizationDetailsRepository = raOrganizationDetailsRepository;
		this.raOrganizationCertificatesRepository = raOrganizationCertificatesRepository;
		this.raOrganizationWrappedKeyRepository = raOrganizationWrappedKeyRepository;
		this.messageSource = messageSource;
	}

	@Override
	public String generateSignatureForAgentSubscriber(LogModelDTO logModelDTO) throws RAServiceException {
		try {
			validateLogModel(logModelDTO);

			JsonNode jsonNode = objectMapper.readTree(logModelDTO.getCallStack());
			String suId = (jsonNode.has(SUBSCRIBER_ID)) ? jsonNode.get(SUBSCRIBER_ID).asText() : null;
			String hashData = (jsonNode.has("hash")) ? jsonNode.get("hash").asText() : null;

			if (suId == null || suId.isEmpty()) {
				throw new RAServiceException("Subscriber ID cannot be null or empty");
			}


			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(suId);
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			RASubscriberCertificates certs = raSubscriberCertificatesRepository
					.findByCertificateStatusAndsubscriberUniqueIdAndCertificateType(CertificateStatus.ACTIVE.toString(), suId, "SIGN");
			RAServiceAsserts.notNullorEmpty(certs, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			RASubscriberWrappedKey wrappedKey = raSubscriberWrappedKeyRepository.findBycertificateSerialNumber(certs.getCertificateSerialNumber());
			RASubscriberCertificatePinHistory pinHistory = pinHistoryRepository.findBysubscriberUniqueId(suId);


			GenerateSignature generateSignature = new GenerateSignature();
			generateSignature.setKeyId(certs.getPkiKeyId());
			generateSignature.setWrappedKey(wrappedKey.getWrappedKey());
			generateSignature.setCertificate(certs.getCertificateData());
			generateSignature.setSerialNumber(certs.getCertificateSerialNumber());
			generateSignature.setSubscriberUniqueId(suId);
			generateSignature.setHash(hashData);
			generateSignature.setHashData(true);


			String signingPins = pinHistory.getSigningCertificatePinList();
			List<String> pinList = Arrays.asList(signingPins.split(", "));
			generateSignature.setCurrentSigningPassword(pinList.get(pinList.size() - 1));


			return executePkiSignatureRequest(generateSignature, logModelDTO, ServiceName.DIGITALLY_SIGNING_FAILED);

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: generateSignatureForAgentSubscriber Error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericErrorMessage());
		}
	}

	@Override
	public String generateSignatureForAgentOrganization(LogModelDTO logModelDTO) throws RAServiceException {
		try {
			validateLogModel(logModelDTO);

			JsonNode jsonNode = objectMapper.readTree(logModelDTO.getCallStack());
			String orgId = (jsonNode.has(SUBSCRIBER_ID)) ? jsonNode.get(SUBSCRIBER_ID).asText() : null;
			String hashData = (jsonNode.has("hash")) ? jsonNode.get("hash").asText() : null;

			if (orgId == null || orgId.isEmpty()) {
				throw new RAServiceException("Organization Id cannot be null or empty");
			}

			RAOrganizationDetails orgDetails = raOrganizationDetailsRepository.findByOrganizationUid(orgId);
			RAServiceAsserts.notNullorEmpty(orgDetails, ErrorCodes.E_ORGANIZATION_DATA_NOT_FOUND);

			RAOrganizationCertificates orgCerts = raOrganizationCertificatesRepository
					.findByCertificateStatusAndOrganizationUniqueId(CertificateStatus.ACTIVE.toString(), orgId);
			RAServiceAsserts.notNullorEmpty(orgCerts, ErrorCodes.E_ACTIVE_CERTIFICATE_NOT_FOUND);

			RAOrganizationWrappedKey wrappedKey = raOrganizationWrappedKeyRepository.findBycertificateSerialNumber(orgCerts.getCertificateSerialNumber());

			GenerateSignature generateSignature = new GenerateSignature();
			generateSignature.setKeyId(orgCerts.getPkiKeyId());
			generateSignature.setWrappedKey(wrappedKey.getWrappedKey());
			generateSignature.setCertificate(orgCerts.getCertificateData());
			generateSignature.setSerialNumber(orgCerts.getCertificateSerialNumber());
			generateSignature.setSubscriberUniqueId(orgId);
			generateSignature.setHash(hashData);
			generateSignature.setHashData(true);

			return executePkiSignatureRequest(generateSignature, logModelDTO, ServiceName.OTHER);

		} catch (HibernateException e) {
			throw handleDatabaseException(e);
		} catch (RAServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("{} :: generateSignatureForAgentOrganization Error: {}", CLASS, e.getMessage());
			throw new RAServiceException(getGenericErrorMessage());
		}
	}

	private String executePkiSignatureRequest(GenerateSignature genSig, LogModelDTO logDto, ServiceName failService) throws RAServiceException {
		try {
			logDto.setCallStack(genSig.getGenerateSignatureData());
			PostRequest postRequest = new PostRequest();
			postRequest.setRequestBody(logDto.toString());
			postRequest.setHashdata(logDto.toString().hashCode());

			RequestEntity requestEntity = new RequestEntity();
			requestEntity.setPostRequest(postRequest);
			requestEntity.setTransactionType(Constant.GENERATE_SIGNATURE);

			ResponseEntity<String> response = restTemplate.postForEntity(PropertiesConstants.getPkiUrl(), requestEntity, String.class);
			String responseBody = response.getBody();

			if (Constant.TRANSACTION_TYPE_NOT_FOUND.equals(responseBody)) {
				throw new RAServiceException(ErrorCodes.E_TRANSACTION_TYPE_NOT_FOUND);
			}
			if (Constant.REQUEST_IS_NOT_VALID.equals(responseBody)) {
				throw new RAServiceException(ErrorCodes.E_REQUEST_DATA_IS_NOT_VALID);
			}

			ServiceResponse serviceResponse = objectMapper.readValue(responseBody, ServiceResponse.class);

			logDto.setCallStack(null);
			logDto.setLogMessage(Constant.RESPONSE);
			logDto.setEndTime(NativeUtils.getTimeStampString());

			if (Constant.FAIL.equals(serviceResponse.getStatus())) {
				logDto.setLogMessageType(LogMessageType.ERROR.toString());
				logDto.setServiceName(failService.toString());
				sendAuditLog(logDto);

				ErrorCodes.setResponse(serviceResponse);
				String businessMsg = ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(serviceResponse.getErrorMessage()));
				throw new RAServiceException(businessMsg != null ? businessMsg : serviceResponse.getErrorMessage());
			}

			logDto.setLogMessageType(LogMessageType.SUCCESS.toString());
			sendAuditLog(logDto);
			return serviceResponse.getSignature();

		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			throw new RAServiceException("PKI Signature Failure: Unable to parse JSON response for subscriber: " + genSig.getSubscriberUniqueId(), e);

		} catch (org.springframework.web.client.RestClientException e) {
			throw new RAServiceException("PKI Signature Failure: Communication error while connecting to PKI service at " + PropertiesConstants.getPkiUrl(), e);

		} catch (RAServiceException e) {
			throw e;

		} catch (Exception e) {
			logger.error("{} :: executePkiSignatureRequest unexpected failure :: {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(ERR_SOMETHING_WENT_WRONG, null, Locale.ENGLISH));
		}
	}

	private void validateLogModel(LogModelDTO logModelDTO) throws RAServiceException {
		if (Objects.isNull(logModelDTO) || logModelDTO.getCallStack() == null) {
			throw new RAServiceException("logModel or callStack cannot be null");
		}
	}

	private void sendAuditLog(LogModelDTO dto) {
		try {
			rabbitMQSender.send(NativeUtils.getLogModel(dto));
		} catch (Exception e) {
			logger.error("Failed to send audit log to Kafka", e);
		}
	}

	private RAServiceException handleDatabaseException(Exception e) {
		logger.error("{} :: Database Exception: {}", CLASS, e.getMessage());
		return new RAServiceException(getGenericErrorMessage());
	}

	private String getGenericErrorMessage() {
		return messageSource.getMessage(ERR_SOMETHING_WENT_WRONG, null, Locale.ENGLISH);
	}
}