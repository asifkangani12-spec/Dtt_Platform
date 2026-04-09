/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021,
 * All rights reserved.
 */
package ug.daes.ra.service.iface.implementation;
import java.math.BigInteger;
import java.util.*;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.ra.asserts.RAServiceAsserts;
import ug.daes.ra.config.SecureUrlValidator;
import ug.daes.ra.dto.*;

import ug.daes.ra.enums.LogMessageType;
import ug.daes.ra.exception.ErrorCodes;
import ug.daes.ra.exception.RAServiceException;
import ug.daes.ra.model.*;
import ug.daes.ra.model.RASubscriberStatusModel;
import ug.daes.ra.repository.iface.RaSubscriberCertificatesRepository;
import ug.daes.ra.repository.iface.RaSubscriberCompleteDetailsForAssistedRepo;
import ug.daes.ra.repository.iface.RaSubscriberCompleteDetailsRepositoy;
import ug.daes.ra.repository.iface.RaSubscriberDeviceRepository;
import ug.daes.ra.repository.iface.RaSubscriberRepository;
import ug.daes.ra.repository.iface.RaSubscriberStatusRepository;
import ug.daes.ra.request.entity.*;
import ug.daes.ra.response.entity.ApiResponseCertCount;
import ug.daes.ra.response.entity.DashboardDetails;
import ug.daes.ra.response.entity.Response;
import ug.daes.ra.response.entity.ServiceResponse;
import ug.daes.ra.service.iface.SubscriberRAServiceIface;

import ug.daes.ra.utils.Constant;
import ug.daes.ra.utils.NativeUtils;
import ug.daes.ra.utils.PropertiesConstants;

/**
 * The Class RASubscriberServiceIfaceImpl.
 */

@Service
public class SubscriberRAServiceIfaceImpl implements SubscriberRAServiceIface {

	private static final String CLASS = "SubscriberRAServiceIfaceImpl";
	private static final Logger logger = LoggerFactory.getLogger(SubscriberRAServiceIfaceImpl.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static SecureUrlValidator secureUrlValidator;
	@Value(value = "${ugpass.code}")
	private boolean ugPassCode;
	private static final String STATUS = "Status";
	private static final String SOMETHING_WENT_WRONG = "api.error.something.went.wrong.please.try.after.sometime";
	private final RaSubscriberStatusRepository raSubscriberStatusRepository;
	private final RaSubscriberRepository raSubscriberRepository;
	private final RaSubscriberCompleteDetailsRepositoy raSubscriberCompleteDetailsRepositoy;
	private final RaSubscriberCompleteDetailsForAssistedRepo raSubscriberCompleteDetailsForAssistedRepo;
	private final RaSubscriberDeviceRepository raSubscriberDeviceRepository;
	private final RaSubscriberCertificatesRepository raSubscriberCertificatesRepository;
	private final RestTemplate restTemplate;
	private final MessageSource messageSource;
	private final RAPKIServiceIfaceImpl rapkiServiceIfaceImpl;
	public SubscriberRAServiceIfaceImpl(
			RaSubscriberStatusRepository raSubscriberStatusRepository,
			RaSubscriberRepository raSubscriberRepository,
			RaSubscriberCompleteDetailsRepositoy raSubscriberCompleteDetailsRepositoy,
			RaSubscriberCompleteDetailsForAssistedRepo raSubscriberCompleteDetailsForAssistedRepo,
			RaSubscriberDeviceRepository raSubscriberDeviceRepository,
			RaSubscriberCertificatesRepository raSubscriberCertificatesRepository,
			RestTemplate restTemplate,
			MessageSource messageSource,
			RAPKIServiceIfaceImpl rapkiServiceIfaceImpl) {
		this.raSubscriberStatusRepository = raSubscriberStatusRepository;
		this.raSubscriberRepository = raSubscriberRepository;
		this.raSubscriberCompleteDetailsRepositoy = raSubscriberCompleteDetailsRepositoy;
		this.raSubscriberCompleteDetailsForAssistedRepo = raSubscriberCompleteDetailsForAssistedRepo;
		this.raSubscriberDeviceRepository = raSubscriberDeviceRepository;
		this.raSubscriberCertificatesRepository = raSubscriberCertificatesRepository;
		this.restTemplate = restTemplate;
		this.messageSource = messageSource;
		this.rapkiServiceIfaceImpl = rapkiServiceIfaceImpl;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.dt.ra.service.iface.RASubscriberServiceIface#
	 * getSubscriberDetailsBySubscriberDigitalId(java.lang.String)
	 */


	@Override
	public String getSubscriberDetailsBySearchType(int searchType, String searchValue)
			throws RAServiceException {
		try {
			logger.info("{} :: getSubscriberDetailsBySearchType() :: req :: type: {} | value: {}",
					CLASS, searchType, searchValue);

			String subscriberUniqueId = null;
			switch (searchType) {
				case 1:
					subscriberUniqueId = raSubscriberRepository.findByidDocTypeAndidDocNumber("1", searchValue);
					RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_NIN_NOT_FOUND);
					break;
				case 2:
					subscriberUniqueId = raSubscriberRepository.findByidDocTypeAndidDocNumber("3", searchValue);
					RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_PASSPORT_NOT_FOUND);
					break;
				case 3:
					subscriberUniqueId = raSubscriberRepository.findByemailId(searchValue);
					RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_EMAIL_NOT_FOUND);
					break;
				case 4:
					subscriberUniqueId = raSubscriberRepository.findBymobileNumber(searchValue);
					RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_MOBILE_NUMBER_NOT_FOUND);
					break;
				default:
					throw new RAServiceException(ErrorCodes.E_INVALID_REQUEST);
			}

			RASubscriberCompleteDetails subscriberDetails = raSubscriberCompleteDetailsRepositoy.findBysubscriberUid(subscriberUniqueId);
			Object finalData;

			if (subscriberDetails == null) {
				RASubscriberCompleteDetailsForAssisted assisted = raSubscriberCompleteDetailsForAssistedRepo.findBysubscriberUid(subscriberUniqueId);
				RAServiceAsserts.notNullorEmpty(assisted, ErrorCodes.E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND);


				assisted.setPhoto(getBase64String(assisted.getPhoto()));
				assisted.setDateOfBirth(NativeUtils.getDate(assisted.getDateOfBirth()));

				String tempJson = objectMapper.writeValueAsString(assisted);
				RASubscriberCompleteDetails nonSmartPhoneDetails = objectMapper.readValue(tempJson, RASubscriberCompleteDetails.class);
				nonSmartPhoneDetails.setDeviceStatus("NA");
				nonSmartPhoneDetails.setDeviceRegistrationTime("NA");

				finalData = nonSmartPhoneDetails;
			} else {
				subscriberDetails.setPhoto(ugPassCode ? getBase64String(subscriberDetails.getPhoto()) : subscriberDetails.getPhoto());
				subscriberDetails.setDateOfBirth(NativeUtils.getDate(subscriberDetails.getDateOfBirth()));
				finalData = subscriberDetails;
			}


			ApiResponse response = AppUtil.createApiResponse(
					true,
					messageSource.getMessage("api.response.data.found", null, Locale.ENGLISH),
					finalData
			);

			return objectMapper.writeValueAsString(response);

		} catch (JsonProcessingException e) {

			throw new RAServiceException("Subscriber Data Mapping failed: " + e.getMessage());

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | GenericJDBCException e) {

			logger.error("{} :: Database failure during subscriber lookup: {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG, null, Locale.ENGLISH));

		} catch (RAServiceException e) {

			throw e;

		} catch (RuntimeException e) {
			logger.error("{} :: Unexpected runtime error in getSubscriberDetailsBySearchType: {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG, null, Locale.ENGLISH));
		}
	}

public String getBase64String(String uri) throws RAServiceException {
	try {
		logger.info("{} :: getBase64String() :: URI :: {}", CLASS, uri);


		secureUrlValidator.validate(uri);


		HttpHeaders headersForGet = new HttpHeaders();
		HttpEntity<Object> requestEntityForGet = new HttpEntity<>(headersForGet);


		ResponseEntity<Resource> downloadUrlResult = restTemplate.exchange(uri, HttpMethod.GET, requestEntityForGet, Resource.class);
		logger.info("{} :: getBase64String() :: Status Code :: {}", CLASS, downloadUrlResult.getStatusCode());

		byte[] buffer = restTemplate.getForObject(uri, byte[].class);

		if (buffer == null || buffer.length == 0) {
			logger.error("{} :: getBase64String() :: Downloaded buffer is null or empty", CLASS);
			throw new RAServiceException(messageSource.getMessage("api.error.download.failed", null, Locale.ENGLISH));
		}

		return Base64.getEncoder().encodeToString(buffer);

	} catch (RestClientException e) {
		logger.error("{} :: getBase64String() :: RestClientException :: {}", CLASS, e.getMessage());
		throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG, null, Locale.ENGLISH));
	} catch (Exception e) {
		logger.error("{} :: getBase64String() :: Unexpected Exception :: {}", CLASS, e.getMessage(), e);
		throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG, null, Locale.ENGLISH));
	}
}
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.dt.ra.service.iface.RASubscriberServiceIface#getCountOfAllSubscribers ()
	 */

	@Override
	public String getCountOfAllSubscribers() throws RAServiceException {
		try {
			logger.info("{} :: getCountOfAllSubscribers().", CLASS);

			DashboardDetails dashboardDetails = new DashboardDetails();

			int totalCount = raSubscriberStatusRepository.getSubscriberCount();
			dashboardDetails.setSubscriberCount(totalCount);

			int activeCount = raSubscriberStatusRepository.getActiveSubscriberCount();
			dashboardDetails.setActiveSubscriberCount(activeCount);

			dashboardDetails.setInActiveSubscriberCount(totalCount - activeCount);

			logger.info("{} :: getCountOfAllSubscribers() :: Success.", CLASS);

			return objectMapper.writeValueAsString(dashboardDetails);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {
			logger.error("{} :: Database Exception in getCountOfAllSubscribers(): {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG,
					null, Locale.ENGLISH));

		} catch (JsonProcessingException e) {
			logger.error("{} :: JSON Conversion Error: {}", CLASS, e.getMessage());
			throw new RAServiceException("Error processing dashboard data");

		} catch (RuntimeException e) {
			logger.error("{} :: Unexpected Runtime Exception in getCountOfAllSubscribers(): {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG,
					null, Locale.ENGLISH));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.dt.ra.service.iface.SubscriberRAServiceIface#updateSubscriberStatus(com.
	 * dt.ra.service.requestentity.UpdateSubscriberStatus)
	 */
	@Override
	public String updateSubscriberStatus(RARequestDTO detailsRequest) throws RAServiceException {
		try {
			logger.info("{} :: updateSubscriberStatus() :: req :: detailsRequest :: {}", CLASS, detailsRequest);

			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(detailsRequest.getSubscriberUniqueId());
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			RASubscriberStatusModel subscriberStatus = raSubscriberStatusRepository
					.findBysubscriberUid(detailsRequest.getSubscriberUniqueId());
			RAServiceAsserts.notNullorEmpty(subscriberStatus, ErrorCodes.E_SUBSCRIBER_STATUS_DATA_NOT_FOUND);

			subscriberStatus.setSubscriberStatus(detailsRequest.getSubscriberStatus());
			subscriberStatus.setSubscriberStatusDescription(detailsRequest.getDescription());


			subscriberStatus.setUpdatedDate(NativeUtils.getTimeStamp());

			raSubscriberStatusRepository.save(subscriberStatus);

			logger.info("{} :: updateSubscriberStatus(). Success.", CLASS);
			return subscriberStatus.getSubscriberStatus();

		} catch (java.text.ParseException e) {

			throw new RAServiceException("Date parsing error during status update: " + e.getMessage());

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | GenericJDBCException e) {

			throw new RAServiceException("Database error during status update: " + e.getMessage());

		} catch (RAServiceException e) {
			throw e;

		} catch (RuntimeException e) {
			throw new RAServiceException("Unexpected system error in updateSubscriberStatus: " + e.getMessage());
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dt.ra.service.iface.SubscriberRAServiceIface#
	 * updateDeviceStatusAndSubscriberStatus(java.lang.String)
	 */
	@Override
	public String updateDeviceStatusAndSubscriberStatus(String subscriberUniqueId) throws RAServiceException {
		try {
			logger.info("{} :: updateDeviceStatusAndSubscriberStatus() :: Started for UID: {}", CLASS, subscriberUniqueId);

			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);

			RASubscriberStatusModel subscriberStatus = raSubscriberStatusRepository.findBysubscriberUid(subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriberStatus, ErrorCodes.E_SUBSCRIBER_STATUS_DATA_NOT_FOUND);

			RASubscriberDevice subscriberDevice = raSubscriberDeviceRepository.findBysubscriberUid(subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriberDevice, ErrorCodes.E_SUBSCRIBER_DEVICE_DATA_NOT_FOUND);

			subscriberDevice.setDeviceStatus(Constant.DISABLED);
			subscriberDevice.setUpdatedDate(NativeUtils.getTimeStampString());
			raSubscriberDeviceRepository.save(subscriberDevice);

			logger.info("{} :: updateDeviceStatusAndSubscriberStatus() :: Success for UID: {}", CLASS, subscriberUniqueId);

			return messageSource.getMessage("api.response.subscriber.device.de.registeration.successful", null, Locale.ENGLISH);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {


			throw new RAServiceException("Database failure during de-registration for UID [" + subscriberUniqueId + "]: " + e.getMessage());

		} catch (RAServiceException e) {


			throw e;

		} catch (RuntimeException e) {


			throw new RAServiceException("Unexpected system error during de-registration for UID [" + subscriberUniqueId + "]: " + e.getMessage());
		}
	}
	@Override
	public String getSubscriberList(int type, String value) throws RAServiceException {
		try {
			logger.info("{} :: getSubscriberList() :: search type: {} | value: {}", CLASS, type, value);

			List<String> subscriberList;
			switch (type) {
				case 1:
					subscriberList = raSubscriberRepository.getSubscriberListByDocTypeAndDocNumber("1", value);
					RAServiceAsserts.notNullorEmpty(subscriberList, ErrorCodes.E_NIN_NOT_FOUND);
					break;
				case 2:
					subscriberList = raSubscriberRepository.getSubscriberListByDocTypeAndDocNumber("3", value);
					RAServiceAsserts.notNullorEmpty(subscriberList, ErrorCodes.E_PASSPORT_NOT_FOUND);
					break;
				case 3:
					subscriberList = raSubscriberRepository.getSubscriberListByEmailId(value);
					RAServiceAsserts.notNullorEmpty(subscriberList, ErrorCodes.E_EMAIL_NOT_FOUND);
					break;
				case 4:
					subscriberList = raSubscriberRepository.getSubscriberListByMobileNo(value);
					RAServiceAsserts.notNullorEmpty(subscriberList, ErrorCodes.E_MOBILE_NUMBER_NOT_FOUND);
					break;
				default:
					throw new RAServiceException(ErrorCodes.E_INVALID_REQUEST);
			}

			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(subscriberList);

		} catch (JsonProcessingException e) {

			throw new RAServiceException("JSON Formatting error for subscriber list (Type " + type + "): " + e.getMessage());

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | GenericJDBCException e) {

			throw new RAServiceException("Database error retrieving subscriber list: " + e.getMessage());

		} catch (RAServiceException e) {

			throw e;

		} catch (RuntimeException e) {
			throw new RAServiceException("Unexpected system error in getSubscriberList: " + e.getMessage());
		}
	}
	@Override
	public ApiResponseCertCount getCountOfAllCertificates() throws RAServiceException {
		try {
			logger.info("{} :: getCountOfAllCertificates() :: Fetching certificate count.", CLASS);

			int count = raSubscriberCertificatesRepository.getAllCertificateCount();

			logger.info("{} :: getCountOfAllCertificates() :: Success. Count: {}", CLASS, count);
			return new ApiResponseCertCount(true, "Certificate count fetched successfully", count);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {

			logger.error("{} :: Database Exception in getCountOfAllCertificates(): {}", CLASS, e.getMessage(), e);

			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG,
					null, Locale.ENGLISH));

		} catch (Exception e) {

			logger.error("{} :: Unexpected System Error in getCountOfAllCertificates(): {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG,
					null, Locale.ENGLISH));
		}
	}
	@Override
	public ApiResponseCertCount getSubscriberAndCertCount() {
		try {
			Object[] result = raSubscriberCertificatesRepository.getSubscriberAndCertCount();

			Map<String, BigInteger> subAndCertCountDTO = buildSubscriberAndCertMap(result);

			CountResponseDTO countResponseDTO = new CountResponseDTO();

			SubscriberCountDTO subscriberCountDTO = new SubscriberCountDTO(
					subAndCertCountDTO.get("subscriber_count"),
					subAndCertCountDTO.get("active_subscriber_count"),
					subAndCertCountDTO.get("inactive_subscriber_count"),
					subAndCertCountDTO.get("disable_subscriber_count"),
					subAndCertCountDTO.get("cert_revoke_subscriber_count"),
					subAndCertCountDTO.get("cert_expired_subscriber_count"),
					subAndCertCountDTO.get("onboarded_subscriber_count")
			);

			CertificateCountDTO certificateCountDTO = new CertificateCountDTO(
					subAndCertCountDTO.get("active_cert_count"),
					subAndCertCountDTO.get("revoke_cert_count"),
					subAndCertCountDTO.get("expired_cert_count"),
					subAndCertCountDTO.get("cert_count")
			);

			countResponseDTO.setSubscriberCount(subscriberCountDTO);
			countResponseDTO.setCertificateCount(certificateCountDTO);

			return new ApiResponseCertCount(
					true,
					messageSource.getMessage(
							"api.response.subscribers.and.certificates.count.fetched.successfully",
							null, Locale.ENGLISH
					),
					countResponseDTO
			);

		} catch (Exception e) {
			logger.error("Unexpected exception", e);
			return new ApiResponseCertCount(
					false,
					messageSource.getMessage(
							"api.error.subscribers.and.certificates.count.fetched.failed",
							null, Locale.ENGLISH
					),
					null
			);
		}
	}
	private Map<String, BigInteger> buildSubscriberAndCertMap(Object r) {

		Map<String, BigInteger> map = new HashMap<>();

		Object[] row = switch (r) {

			case Object[] outer when outer.length > 0 && outer[0] instanceof Object[] inner
					-> inner;

			case Object[] outer
					-> outer;

			default
					-> throw new IllegalArgumentException("Unexpected type: " + r.getClass());
		};

		map.put("subscriber_count",              toBig(row[0]));
		map.put("active_subscriber_count",       toBig(row[1]));
		map.put("inactive_subscriber_count",     toBig(row[2]));
		map.put("disable_subscriber_count",      toBig(row[3]));
		map.put("cert_revoke_subscriber_count",  toBig(row[4]));
		map.put("cert_expired_subscriber_count", toBig(row[5]));
		map.put("onboarded_subscriber_count",    toBig(row[6]));
		map.put("active_cert_count",             toBig(row[7]));
		map.put("revoke_cert_count",             toBig(row[8]));
		map.put("expired_cert_count",            toBig(row[9]));
		map.put("cert_count",                    toBig(row[10]));

		return map;
	}

	private BigInteger toBig(Object o) {
		if (o == null) return BigInteger.ZERO;

		if (o instanceof BigInteger bigInteger)
			return bigInteger;

		if (o instanceof Number number)
			return BigInteger.valueOf(number.longValue());

		throw new IllegalArgumentException("Unexpected type: " + o.getClass());
	}



	@Override
	public String getSubscriberName(String subscriberUniqueId) throws RAServiceException {
		try {
			logger.info("{} :: getSubscriberName() :: UID :: {}", CLASS, subscriberUniqueId);


			RASubscriber subscriber = raSubscriberRepository.findBysubscriberUid(subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriber, ErrorCodes.E_SUBSCRIBER_DATA_NOT_FOUND);


			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(subscriber.getFullName());

		} catch (JsonProcessingException e) {
			logger.error("{} :: JSON Mapping Exception in getSubscriberName(): {}", CLASS, e.getMessage());
			throw new RAServiceException("Error processing name data");

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {

			logger.error("{} :: Database Exception in getSubscriberName(): {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG,
					null, Locale.ENGLISH));

		} catch (RAServiceException e) {

			logger.error("{} :: RAServiceException in getSubscriberName() :: {}", CLASS, e.getMessage());
		return null;

		} catch (Exception e) {

			logger.error("{} :: Unexpected System Exception in getSubscriberName(): {}", CLASS, e.getMessage(), e);
			throw new RAServiceException(messageSource.getMessage(SOMETHING_WENT_WRONG,
					null, Locale.ENGLISH));
		}
	}
	@Override
	public ApiResponse compareImage(String suid, String capturedImage) throws RAServiceException {
		RASubscriberCompleteDetailsForAssisted subscriberCompleteDetails = raSubscriberCompleteDetailsForAssistedRepo.findBysubscriberUid(suid);


		RAServiceAsserts.notNullorEmpty(subscriberCompleteDetails, ErrorCodes.E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND);

		String originalPhoto = null;
		try {
			if (ugPassCode) {
				originalPhoto = getBase64String(subscriberCompleteDetails.getPhoto());
			} else {
				originalPhoto = subscriberCompleteDetails.getPhoto();
			}
		} catch (RuntimeException e) {

			logger.error("{} :: compareImage() :: Image processing failed :: {}", CLASS, e.getMessage());
			throw new RAServiceException(ErrorCodes.E_RA_POST_REQUEST_FAILED);
		}

		CompareFaceRequest compareFaceRequest = new CompareFaceRequest();
		compareFaceRequest.setStoredImage(originalPhoto);
		compareFaceRequest.setCapturedImage(capturedImage);

		LogModelDTO logModelDTO = new LogModelDTO();
		logModelDTO.setCallStack(compareFaceRequest.toStringForNative());

		PostRequest postCompareRequest = new PostRequest();
		postCompareRequest.setRequestBody(logModelDTO.toString());
		postCompareRequest.setHashdata(logModelDTO.toString().hashCode());

		RequestEntity requestEntity = new RequestEntity();
		requestEntity.setPostRequest(postCompareRequest);
		requestEntity.setTransactionType(Constant.COMPARE_IMAGE);

		logger.info("{} :: compareImage() :: request :: {}", CLASS, requestEntity.getPostRequest().getRequestBody());

		ResponseEntity<String> httpResponse = restTemplate.postForEntity(PropertiesConstants.getPkiUrl(), requestEntity, String.class);


		if (Constant.TRANSACTION_TYPE_NOT_FOUND.equals(httpResponse.getBody())) {
			throw new RAServiceException(ErrorCodes.E_TRANSACTION_TYPE_NOT_FOUND);
		}
		if (Constant.REQUEST_IS_NOT_VALID.equals(httpResponse.getBody())) {
			throw new RAServiceException(ErrorCodes.E_REQUEST_DATA_IS_NOT_VALID);
		}

		ServiceResponse serviceResponse;
		try {

			serviceResponse = objectMapper.readValue(httpResponse.getBody(), ServiceResponse.class);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			logger.error("{} :: compareImage() :: JSON Parsing Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(ErrorCodes.E_RA_POST_REQUEST_FAILED);
		}

		logger.info("{} :: compareImage() :: Native response :: {}", CLASS, serviceResponse.getStatus());

		logModelDTO.setCallStack(null);
		logModelDTO.setLogMessage(Constant.RESPONSE);
		logModelDTO.setEndTime(NativeUtils.getTimeStampString());

		if (Constant.FAIL.equals(serviceResponse.getStatus())) {
			ErrorCodes.setResponse(serviceResponse);
			logger.info("{} :: compareImage :: error :: {}", CLASS, serviceResponse.getErrorMessage());
			logModelDTO.setLogMessageType(LogMessageType.ERROR.toString());
			throw new RAServiceException(serviceResponse.getErrorMessage());
		}

		return AppUtil.createApiResponse(true, STATUS, serviceResponse);
	}


	@Override
	public ApiResponse getFeatures(String suid, String capturedImage) throws RAServiceException {
		CompareFaceRequest compareFaceRequest = new CompareFaceRequest();
		compareFaceRequest.setCapturedImage(capturedImage);

		LogModelDTO logModelDTO = new LogModelDTO();
		logModelDTO.setCallStack(compareFaceRequest.toStringForNative());

		PostRequest postCompareRequest = new PostRequest();
		postCompareRequest.setRequestBody(logModelDTO.toString());
		postCompareRequest.setHashdata(logModelDTO.toString().hashCode());

		RequestEntity requestEntity = new RequestEntity();
		requestEntity.setPostRequest(postCompareRequest);
		requestEntity.setTransactionType(Constant.GET_FEATURES);

		logger.info("{} :: getFeatures() :: request :: {}", CLASS, requestEntity.getPostRequest().getRequestBody());

		ResponseEntity<String> httpResponse;
		try {
			httpResponse = restTemplate.postForEntity(PropertiesConstants.getPkiUrl(), requestEntity, String.class);
		} catch (org.springframework.web.client.RestClientException e) {
			logger.error("{} :: getFeatures() :: RestTemplate Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(ErrorCodes.E_RA_POST_REQUEST_FAILED);
		}

		if (Constant.TRANSACTION_TYPE_NOT_FOUND.equals(httpResponse.getBody())) {
			throw new RAServiceException(ErrorCodes.E_TRANSACTION_TYPE_NOT_FOUND);
		}

		if (Constant.REQUEST_IS_NOT_VALID.equals(httpResponse.getBody())) {
			throw new RAServiceException(ErrorCodes.E_REQUEST_DATA_IS_NOT_VALID);
		}

		ServiceResponse serviceResponse;
		try {
			serviceResponse = objectMapper.readValue(httpResponse.getBody(), ServiceResponse.class);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			logger.error("{} :: getFeatures() :: JSON Parsing Error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(ErrorCodes.E_RA_POST_REQUEST_FAILED);
		}

		logger.error("{} :: getFeatures() :: native response body :: {}", CLASS, httpResponse.getBody());
		logger.info("{} :: getFeatures() :: Native response :: {}", CLASS, serviceResponse.getStatus());

		logModelDTO.setCallStack(null);
		logModelDTO.setLogMessage(Constant.RESPONSE);
		logModelDTO.setEndTime(NativeUtils.getTimeStampString());

		if (Constant.FAIL.equals(serviceResponse.getStatus())) {
			ErrorCodes.setResponse(serviceResponse);
			logger.info("{} :: getFeatures :: error :: {}", CLASS, serviceResponse.getErrorMessage());
			logModelDTO.setLogMessageType(LogMessageType.ERROR.toString());
			throw new RAServiceException(serviceResponse.getErrorMessage());
		}

		return AppUtil.createApiResponse(true, STATUS, serviceResponse);
	}

	@Override
	public Response getSignForQr(QrData qrData) throws RAServiceException {
		try {
			logger.info("{} :: getSignForQr() :: Processing request for OrgId: {}", CLASS, qrData.getOrgId());

			QrData qrResponse = new QrData();


			GenerateSignature generateSignature = new GenerateSignature();
			generateSignature.setSubscriberUniqueId(qrData.getOrgId());
			generateSignature.setCertType(0);
			generateSignature.setHashData(true);
			generateSignature.setHash(qrData.getDataHash());


			String response = rapkiServiceIfaceImpl.generateSignatureOrganization(generateSignature);
			qrResponse.setSignedDataHash(response);

			if (qrData.getKeyHash() != null && !qrData.getKeyHash().isEmpty()) {
				GenerateSignature generateSignature2 = new GenerateSignature();
				generateSignature2.setSubscriberUniqueId(qrData.getOrgId());
				generateSignature2.setCertType(0);
				generateSignature2.setHashData(true);
				generateSignature2.setHash(qrData.getKeyHash());

				String response2 = rapkiServiceIfaceImpl.generateSignatureOrganization(generateSignature2);
				qrResponse.setSignedKeyHash(response2);
			}

			logger.info("{} :: getSignForQr() :: QR Signature generated successfully.", CLASS);
			return new Response(true, "QR Signature generated successfully", qrResponse);

		} catch (RAServiceException e) {

			throw e;

		} catch (RuntimeException e) {

			throw new RAServiceException("Unexpected runtime error while signing QR data for Org [" + qrData.getOrgId() + "]: " + e.getMessage());

		} catch (Exception e) {

			throw new RAServiceException("System failure during QR signing process: " + e.getMessage());
		}
	}

	@Override
	public ApiResponse getSubscriberPhoto(String suid) throws RAServiceException {
		RASubscriberCompleteDetails assisted = raSubscriberCompleteDetailsRepositoy.findBysubscriberUid(suid);


		RAServiceAsserts.notNullorEmpty(assisted, ErrorCodes.E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND);

		String photo = null;
		try {
			if (ugPassCode) {
				if (assisted.getPhoto() == null) {
					throw new RAServiceException(ErrorCodes.E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND);
				}
				photo = getBase64String(assisted.getPhoto());
			} else {
				photo = assisted.getPhoto();
			}
		} catch (RuntimeException e) {
			logger.error("{} :: getSubscriberPhoto() :: Image conversion error :: {}", CLASS, e.getMessage());
			throw new RAServiceException(ErrorCodes.E_SOMETHING_WENT_WRONG);
		}

		return AppUtil.createApiResponse(true, "Photo", photo);
	}
}