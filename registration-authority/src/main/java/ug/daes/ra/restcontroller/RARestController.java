/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021,
 * All rights reserved.
 */
package ug.daes.ra.restcontroller;

import java.util.Locale;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import ug.daes.DAESService;
import ug.daes.PKICoreServiceException;
import ug.daes.Result;
import ug.daes.ra.asserts.RAServiceAsserts;

import ug.daes.ra.dto.ExpireSubscriberCertRequestDTO;
import ug.daes.ra.dto.RARequestDTO;
import ug.daes.ra.exception.ErrorCodes;
import ug.daes.ra.request.entity.AgentCert;
import ug.daes.ra.request.entity.CompareFaceRequest;
import ug.daes.ra.request.entity.QrData;

import ug.daes.ra.response.entity.ApiResponseCertCount;
import ug.daes.ra.response.entity.Response;
import ug.daes.ra.service.iface.RAServiceIface;
import ug.daes.ra.service.iface.SubscriberRAServiceIface;
import ug.daes.ra.service.iface.implementation.RAServiceIfaceImpl;



/**
 * The Class RASubscriberRestController.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/")
public class RARestController {

	/** The Constant CLASS. */
	private static final String CLASS = "RASubscriberRestController";
	private static final String ERROR = "Error ";
	private static final String API_ERROR = "api.error.please.try.after.some.time.server.down";
	private static final String UNEXPECTED_ERROR = "Unexpected exception";
	/** The Constant logger. */
	private static final Logger logger =
			LoggerFactory.getLogger(RARestController.class);

	private final SubscriberRAServiceIface subscriberServiceIfaceImpl;
	private final RAServiceIfaceImpl raServiceIfaceImpl;
	private final RAServiceIface raServiceIface;
	private final MessageSource messageSource;

	public RARestController(
			SubscriberRAServiceIface subscriberServiceIfaceImpl,
			RAServiceIfaceImpl raServiceIfaceImpl,
			RAServiceIface raServiceIface,
			MessageSource messageSource
			) {

		this.subscriberServiceIfaceImpl = subscriberServiceIfaceImpl;
		this.raServiceIfaceImpl = raServiceIfaceImpl;
		this.raServiceIface = raServiceIface;
		this.messageSource = messageSource;
	}


	/**
	 * Gets the certificate data by certificate type.
	 *
	 * @param subscriberUid the subscriber uid
	 * @param certType      the cert type
	 * @return the certificate data by certificate type
	 */
	@GetMapping(value = "get/service/certificate/data/{subscriberUid}/{certType}", produces = "application/json")
	public String getCertificateDataByCertificateType(@PathVariable String subscriberUid,
													  @PathVariable String certType) {
		try {
			logger.info(
					"{} :: getCertificateDataByCertificateType() :: request :: suid :: {} :: certtype :: {}",
					CLASS,
					subscriberUid,
					certType
			);
			RAServiceAsserts.notNullorEmpty(subscriberUid, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(certType, ErrorCodes.E_INVALID_REQUEST);
			String response = raServiceIfaceImpl.getCertificateDataByCertificateType(subscriberUid, certType);
			logger.info(
					"{} :: getCertificateDataByCertificateType() :: response :: {}",
					CLASS,
					response
			);
			return AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.certificate.data", null, Locale.ENGLISH),
					"\"" + response + "\"").toString();
		} catch (Exception e) {
			logger.error(
					"{} :: getCertificateDataByCertificateType() :: error :: {}",
					CLASS,
					e.getMessage()
			);
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	@GetMapping(value = "get/service/certificate/data/organization/{orgId}/{certType}", produces = "application/json")
	public String getOrganizationCertficateDataByCertificateType(@PathVariable String orgId,
																 @PathVariable String certType) {
		try {
			logger.info(
					"{} :: getOrganizationCertficateDataByCertificateType() :: request :: orgId :: {} :: certType :: {}",
					CLASS,
					orgId,
					certType
			);
			RAServiceAsserts.notNullorEmpty(orgId, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(certType, ErrorCodes.E_INVALID_REQUEST);
			String response = raServiceIfaceImpl.getOrganizationCertificateDataByCertificateType(orgId, certType);
			logger.info(
					"{} :: getOrganizationCertficateDataByCertificateType() :: response :: {}",
					CLASS,
					response
			);
			return AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.certificate.data", null, Locale.ENGLISH),
					"\"" + response + "\"").toString();
		} catch (Exception e) {
			logger.error("{} :: getCertificateDataByCertificateType() :: error :: {}",
					CLASS,
					e.getMessage(),
					e);
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	@GetMapping(value = "get/service/certificate/data/agent/{subscriberUid}/{certType}", produces = "application/json")
	public ApiResponse getCertificateDataByCertificateTypeForAgent(@PathVariable String subscriberUid,
																	@PathVariable String certType) {
		try {
			logger.info(
					"{} :: getCertificateDataByCertificateTypeForAgent() :: request :: suid :: {} :: certType :: {}",
					CLASS,
					subscriberUid,
					certType
			);
			RAServiceAsserts.notNullorEmpty(subscriberUid, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(certType, ErrorCodes.E_INVALID_REQUEST);
			ApiResponse response = raServiceIfaceImpl.getCertificateDataByCertificateTypeForAgent(subscriberUid,
					certType);

			logger.info(
					"{} :: getCertificateDataByCertificateTypeForAgent() :: response :: {}",
					CLASS,
					response
			);
			return response;
		} catch (Exception e) {
			logger.error(
					"{} :: getCertificateDataByCertificateTypeForAgent() :: error :: {}",
					CLASS,
					e.getMessage()
			);
			return AppUtil.createApiResponse(false,
					messageSource.getMessage(API_ERROR, null, Locale.ENGLISH),
					null);
		}
	}

	@PostMapping(value = "get/service/certificate/data/agent/encrypt")
	public String getCertificateDataByCertificateTypeForAgentEncrypt(@RequestBody String request) throws PKICoreServiceException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Result result = DAESService.decryptSecureWireData(request);
			String decryptedString = new String(result.getResponse());
			AgentCert agentCert = mapper.readValue(decryptedString,AgentCert.class);
			String subscriberUid = agentCert.getId();
			String certType = agentCert.getCertType();
			logger.info(
					"{} :: getCertificateDataByCertificateTypeForAgent() :: request :: suid :: {} :: certType :: {}",
					CLASS,
					subscriberUid,
					certType
			);
			RAServiceAsserts.notNullorEmpty(subscriberUid, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(certType, ErrorCodes.E_INVALID_REQUEST);
			ApiResponse response = raServiceIfaceImpl.getCertificateDataByCertificateTypeForAgent(subscriberUid,
					certType);

			logger.info(
					"{} :: getCertificateDataByCertificateTypeForAgent() :: response :: {}",
					CLASS,
					response
			);
			String responseString = mapper.writeValueAsString(response);
			Result encrypt = DAESService.createSecureWireData(responseString);
			return new String(encrypt.getResponse());
		} catch (Exception e) {

			logger.error(
					"{} :: getCertificateDataByCertificateTypeForAgent() :: error :: {}",
					CLASS,
					e.getMessage()
			);
			ApiResponse response= AppUtil.createApiResponse(false,
					messageSource.getMessage(API_ERROR, null, Locale.ENGLISH),
					null);
			Result encrypt = DAESService.createSecureWireData(response.toString());
			return new String(encrypt.getResponse());
		}
	}

	@GetMapping(value = "get/service/certificate/data/organization/agent/{orgId}/{certType}", produces = "application/json")
	public ApiResponse getOrganizationCertficateDataByCertificateTypeForAgent(@PathVariable String orgId,
																			   @PathVariable String certType) {
		try {
			logger.info(
					"{} :: getOrganizationCertficateDataByCertificateTypeForAgent() :: request :: orgId :: {} :: certType :: {}",
					CLASS,
					orgId,
					certType
			);
			RAServiceAsserts.notNullorEmpty(orgId, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(certType, ErrorCodes.E_INVALID_REQUEST);
			ApiResponse response = raServiceIfaceImpl.getOrganizationCertificateDataByCertificateTypeForAgent(orgId,
					certType);
			logger.info(
					"{} :: getOrganizationCertficateDataByCertificateTypeForAgent() :: response :: {}",
					CLASS,
					response
			);
			return response;
		} catch (Exception e) {
			logger.error(
					"{} :: getOrganizationCertficateDataByCertificateTypeForAgent() :: error :: {}",
					CLASS,
					e.getMessage()
			);
			return AppUtil.createApiResponse(false,
					messageSource.getMessage(API_ERROR, null, Locale.ENGLISH),
					null);
		}
	}


	@PostMapping(value = "get/service/certificate/data/organization/agent/encrypt")
	public String getOrganizationCertficateDataByCertificateTypeForAgentEncrypt(@RequestBody String request) throws PKICoreServiceException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Result result = DAESService.decryptSecureWireData(request);
			String decryptedString = new String(result.getResponse());
			AgentCert agentCert = mapper.readValue(decryptedString,AgentCert.class);
			String orgId = agentCert.getId();
			String certType = agentCert.getCertType();
			logger.info(
					"{} :: getOrganizationCertficateDataByCertificateTypeForAgent() :: request :: orgId :: {} :: certType :: {}",
					CLASS,
					orgId,
					certType
			);
			RAServiceAsserts.notNullorEmpty(orgId, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(certType, ErrorCodes.E_INVALID_REQUEST);
			ApiResponse response = raServiceIfaceImpl.getOrganizationCertificateDataByCertificateTypeForAgent(orgId,
					certType);
			logger.info(
					"{} :: getOrganizationCertficateDataByCertificateTypeForAgent() :: response :: {}",
					CLASS,
					response
			);
			String responseString = mapper.writeValueAsString(response);
			Result encrypt = DAESService.createSecureWireData(responseString);
			return new String(encrypt.getResponse());
		} catch (Exception e) {

			logger.error(
					"{} :: getOrganizationCertficateDataByCertificateTypeForAgent() :: error :: {}",
					CLASS,
					e.getMessage()
			);
			ApiResponse response= AppUtil.createApiResponse(false,
					messageSource.getMessage(API_ERROR, null, Locale.ENGLISH),
					null);
			Result encrypt = DAESService.createSecureWireData(response.toString());
			return new String(encrypt.getResponse());
		}
	}

	/**
	 * Gets the certificate life cycle logs by subscriber unique id.
	 *
	 * @param subscriberUid the subscriber uid
	 * @return the certificate life cycle logs by subscriber unique id
	 */
	@GetMapping(value = "get/service/certificate/history/{subscriberUid}", produces = "application/json")
	public String getCertificateLifeCycleLogsBySubscriberUniqueId(@PathVariable String subscriberUid) {
		try {
			logger.info(
					"{} :: getCertificateLifeCycleLogsBySubscriberUniqueId() :: request :: suid :: {}",
					CLASS,
					subscriberUid
			);
			RAServiceAsserts.notNullorEmpty(subscriberUid, ErrorCodes.E_INVALID_REQUEST);
			String response = raServiceIfaceImpl.getCertificateLifeCycleLogsBySubscriberUniqueId(subscriberUid);
			logger.info(
					"{} :: getCertificateLifeCycleLogsBySubscriberUniqueId() :: response :: {}",
					CLASS,
					response
			);
			return AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.certificate.history", null, Locale.ENGLISH), response)
					.toString();
		} catch (Exception e) {

			logger.error(
					"{} :: getCertificateLifeCycleLogsBySubscriberUniqueId() :: error :: {}",
					CLASS,
					e.getMessage()
			);
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	/**
	 * Gets the subscriber details by search type.
	 *
	 * @param searchType  the search type
	 * @param searchValue the search value
	 * @return the subscriber details by search type
	 */

	@GetMapping(value = "get/service/subscriber/details/{searchType}/{searchValue}", produces = "application/json")
	public String getSubscriberDetailsBySearchType(@PathVariable int searchType, @PathVariable String searchValue) {
		try {

			logger.info("{} :: getSubscriberDetailsBySearchType() :: request :: searchType :: {} :: searchValue :: {}",
					CLASS, searchType, searchValue);

			RAServiceAsserts.notNullorEmpty(searchType, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(searchValue, ErrorCodes.E_INVALID_REQUEST);

			String raSubscriberData =
					subscriberServiceIfaceImpl.getSubscriberDetailsBySearchType(searchType, searchValue);

			logger.info(CLASS + " :: getSubscriberDetailsBySearchType().success");

			return raSubscriberData;   // ✅ only change needed

		} catch (Exception e) {

			logger.error("{} :: getSubscriberDetailsBySearchType() :: error occurred", CLASS, e);

			return AppUtil.createApiResponse(false,
					ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())),
					null).toString();
		}
	}
	/**
	 * Gets the subscriber count.
	 *
	 * @return the subscriber count
	 */
	@GetMapping(value = "get/service/subscriber/count", produces = "application/json")
	public String getSubscriberCount() {
		try {
			logger.info(CLASS + " :: getSubscriberCount().");
			String count = subscriberServiceIfaceImpl.getCountOfAllSubscribers();
			logger.info(CLASS + " :: getSubscriberCount().success");
			return AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.subscriber.count", null, Locale.ENGLISH), count).toString();
		} catch (Exception e) {

			logger.error("{} :: getSubscriberCount() :: error :: {}",
					CLASS,
					e.getMessage());
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	/**
	 * Gets the revoke reasons.
	 *
	 * @return the revoke reasons
	 */
	@GetMapping(value = "get/service/revoke-reasons", produces = "application/json")
	public String getRevokeReasons() {
		logger.info(CLASS + " :: getRevokeReasons().");
		String reasons = raServiceIfaceImpl.getRevokeReasons();
		logger.info(CLASS + " :: getRevokeReasons() :: success.");
		return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.revoke.reasons", null, Locale.ENGLISH),
				reasons).toString();
	}

	/**
	 * Gets the certificate details by subscriber unique id.
	 *
	 * @param subscriberUniqueId the subscriber unique id
	 * @return the certificate details by subscriber unique id
	 */
	@GetMapping(value = "get/service/certificate/details/by-subscriber-unique-id/{subscriberUniqueId}", produces = "application/json")
	public String getCertificateDetailsBySubscriberUniqueId(@PathVariable String subscriberUniqueId,HttpServletRequest httpServletRequest) {
		try {
			logger.info("{} :: getCertificateDetailsBySubscriberUniqueId() :: request :: subscriberUniqueId :: {}",
					CLASS,
					subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_INVALID_REQUEST);
			String response = raServiceIfaceImpl.getCertificateDetailsBySubscriberUniqueId(subscriberUniqueId);
			logger.info(CLASS + " :: getCertificateDetailsBySubscriberUniqueId() :: success.");
			return AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.certificate.details", null, Locale.ENGLISH), response)
					.toString();
		} catch (Exception e) {
			logger.error("{} :: getCertificateDetailsBySubscriberUniqueId() :: error :: {}",
					CLASS,
					e.getMessage());
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	/**
	 * Update subscriber status.
	 *
	 * @param updateSubscriberStatus the update subscriber status
	 * @return the string
	 */
	@PutMapping(value = "update/service/subscriber/status", produces = "application/json")
	public String updateSubscriberStatus(@RequestBody RARequestDTO updateSubscriberStatus) {
		try {
			logger.info("{} :: updateSubscriberStatus() :: request :: updateSubscriberStatus :: {}",
					CLASS,
					updateSubscriberStatus);
			RAServiceAsserts.notNullorEmpty(updateSubscriberStatus, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(updateSubscriberStatus.getSubscriberUniqueId(),
					ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(updateSubscriberStatus.getSubscriberStatus(), ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(updateSubscriberStatus.getDescription(), ErrorCodes.E_INVALID_REQUEST);
			String status = subscriberServiceIfaceImpl.updateSubscriberStatus(updateSubscriberStatus);
			logger.info(CLASS + " :: updateSubscriberStatus() :: Success.");
			return AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.subscriber.status.updated", null, Locale.ENGLISH),
					"\"" + status + "\"").toString();
		} catch (Exception e) {
			logger.error("{} :: updateSubscriberStatus() :: error :: {}",
					CLASS,
					e.getMessage());
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	/**
	 * Update device status and subscriber status.
	 *
	 * @param subscriberUniqueId the subscriber unique id
	 * @return the string
	 */
	@PutMapping(value = "update/service/subscriber/device-status-and-subscriber-satus/{subscriberUniqueId}")
	public String updateDeviceStatusAndSubscriberStatus(@PathVariable String subscriberUniqueId) {
		try {
			logger.info("{} :: updateDeviceStatusAndSubscriberStatus() :: request :: subscriberUniqueId :: {}",
					CLASS,
					subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_INVALID_REQUEST);
			String status = subscriberServiceIfaceImpl.updateDeviceStatusAndSubscriberStatus(subscriberUniqueId);
			logger.info(CLASS + " :: updateDeviceStatusAndSubscriberStatus() :: Success.");
			return AppUtil.createApiResponse(true, messageSource
					.getMessage("api.response.subscriber.status.and.device.status.updated", null, Locale.ENGLISH),
					"\"" + status + "\"").toString();
		} catch (Exception e) {
			logger.error("{} :: updateDeviceStatusAndSubscriberStatus() :: error :: {}",
					CLASS,
					e.getMessage());
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	/**
	 * Gets the subscriber details by search type.
	 *
	 * @param searchType  the search type
	 * @param searchValue the search value
	 * @return the subscriber details by search type
	 */
	@GetMapping(value = "get/service/subscriber/list/{searchType}/{searchValue}", produces = "application/json")
	public String getSubscriberListBySearchType(@PathVariable int searchType, @PathVariable String searchValue) {
		try {
			logger.info("{} :: getSubscriberDetailsBySearchType() :: request :: searchType :: {} :: searchValue :: {}",
					CLASS,
					searchType,
					searchValue);
			RAServiceAsserts.notNullorEmpty(searchType, ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(searchValue, ErrorCodes.E_INVALID_REQUEST);
			String raSubscriberData = subscriberServiceIfaceImpl.getSubscriberList(searchType, searchValue);
			logger.info(CLASS + " :: getSubscriberListBySearchType().success");
			return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.subscriber.list", null, Locale.ENGLISH),
					raSubscriberData).toString();
		} catch (Exception e) {
			logger.error("{} :: getSubscriberDetailsBySearchType() :: error :: {}",
					CLASS,
					e.getMessage());
			return AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())), null)
					.toString();
		}
	}

	@GetMapping("get-certificate-count")
	public ApiResponseCertCount getTotalCertificates() {
		try {
			return subscriberServiceIfaceImpl.getCountOfAllCertificates();
		} catch (Exception e) {
			logger.error("{} :: getCertificateCount() :: error :: {}",
					CLASS,
					e.getMessage());
			return new ApiResponseCertCount(false, e.getMessage(), null);
		}
	}

	@GetMapping(value = "get/service/subscriber-certificate/count")
	public ApiResponseCertCount getSubscriberAndCertCount() {
		try {
			return subscriberServiceIfaceImpl.getSubscriberAndCertCount();
		} catch (Exception e) {
			logger.error("{} :: getSubscriberAndCertificateCount() :: error :: {}",
					CLASS,
					e.getMessage());
			return new ApiResponseCertCount(false, e.getMessage(), null);
		}
	}

	@GetMapping({ "get/service/subscriber/name/{subscriberUniqueId}" })
	public String getSubscriberFullName(@PathVariable String subscriberUniqueId) {
		try {

			logger.info(
					"{} :: getSubscriberFullName() :: request :: subscriberUniqueId :: {}",
					CLASS,
					subscriberUniqueId
			);

			RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_INVALID_REQUEST);
			String response = subscriberServiceIfaceImpl.getSubscriberName(subscriberUniqueId);
			return (AppUtil.createApiResponse(true,
					messageSource.getMessage("api.response.subscriber.full.name", null, Locale.ENGLISH), response))
					.toString();
		} catch (Exception var3) {
			logger.error("{} :: getSubscriberFullName() :: error :: {}",
					CLASS,
					var3.getMessage());
			return (AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(var3.getMessage())),
					(String) null)).toString();
		}
	}

	@PostMapping({ "get/service/subscriber/name/encrypt" })
	public String getSubscriberFullNameEncrypt(@RequestBody String request) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Result result = DAESService.decryptSecureWireData(request);
			String subscriberUniqueId = new String(result.getResponse());
			logger.info("{} :: getSubscriberFullName() :: request :: subscriberUniqueId :: {}",
					CLASS, subscriberUniqueId);
			RAServiceAsserts.notNullorEmpty(subscriberUniqueId, ErrorCodes.E_INVALID_REQUEST);
			String response = subscriberServiceIfaceImpl.getSubscriberName(subscriberUniqueId);
			ApiResponse apiResponse = AppUtil.createApiResponse(true,messageSource.getMessage("api.response.subscriber.full.name", null, Locale.ENGLISH), response);
			String ApiResponsetring = mapper.writeValueAsString(apiResponse);
			Result encrypt = DAESService.createSecureWireData(ApiResponsetring);
			return new String(encrypt.getResponse());
		} catch (Exception var3) {
			logger.error("{} :: getSubscriberFullName() :: error", CLASS, var3);
			return (AppUtil.createApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(var3.getMessage())),
					(String) null)).toString();
		}
	}

	@PostMapping("/compare/image")
	public ApiResponse compareImage(@RequestBody CompareFaceRequest compareFaceRequest){
		try{
			RAServiceAsserts.notNullorEmpty(compareFaceRequest.getSuId(), ErrorCodes.E_INVALID_REQUEST);
			RAServiceAsserts.notNullorEmpty(compareFaceRequest.getCapturedImage(), ErrorCodes.E_INVALID_REQUEST);
			String suid = compareFaceRequest.getSuId();
			String capturedImage = compareFaceRequest.getCapturedImage();
			logger.info(CLASS + " :: compareImage() :: request :: suid() capturedImage()" ,suid , capturedImage);

			return subscriberServiceIfaceImpl.compareImage(suid,capturedImage);
		}
		catch (Exception e){
			logger.error("{} :: compareImage() :: error", CLASS, e);
			logger.error(UNEXPECTED_ERROR, e);
			return AppUtil.createApiResponse(false,e.getMessage(),null);
		}
	}

	@PostMapping("/get/image/features")
	public ApiResponse getFeatures(@RequestBody CompareFaceRequest compareFaceRequest){
		try{
			RAServiceAsserts.notNullorEmpty(compareFaceRequest.getCapturedImage(), ErrorCodes.E_INVALID_REQUEST);
			String capturedImage = compareFaceRequest.getCapturedImage();
			logger.info(CLASS + " :: getFeatures() :: request :: capturedImage()" , capturedImage);


			return subscriberServiceIfaceImpl.getFeatures(null,capturedImage);
		}
		catch (Exception e){
			logger.error("{} :: getFeatures() :: error", CLASS, e);
			logger.error(UNEXPECTED_ERROR, e);
			return AppUtil.createApiResponse(false,e.getMessage(),null);
		}
	}

	@PostMapping("/get/image/features/encrypt")
	public String getFeaturesEncrypt(@RequestBody String request){
		try{
			ObjectMapper mapper = new ObjectMapper();
			Result result = DAESService.decryptSecureWireData(request);
			String decryptedString = new String(result.getResponse());
			CompareFaceRequest compareFaceRequest = mapper.readValue(decryptedString,CompareFaceRequest.class);
			RAServiceAsserts.notNullorEmpty(compareFaceRequest.getCapturedImage(), ErrorCodes.E_INVALID_REQUEST);
			String capturedImage = compareFaceRequest.getCapturedImage();
			logger.info(CLASS + " :: getFeatures() :: request :: capturedImage()" , capturedImage);
			ApiResponse apiResponse = subscriberServiceIfaceImpl.getFeatures(null,capturedImage);
			String ApiResponsetring = mapper.writeValueAsString(apiResponse);
			Result encrypt = DAESService.createSecureWireData(ApiResponsetring);
			return new String(encrypt.getResponse());
		}
		catch (Exception e){

			logger.error("{} :: getFeatures() :: error :: {}",
					CLASS,
					e.getMessage(),
					e);
			logger.error(UNEXPECTED_ERROR, e);
			return AppUtil.createApiResponse(false,e.getMessage(),null).toString();
		}
	}

	@GetMapping(value = "get/subscriber/photo/{suid}", produces = "application/json")
	public ApiResponse getSubscriberPhoto(@PathVariable String suid) {
		try {

			logger.info("{} :: getSubscriberDetailsBySearchType() :: request :: searchValue :: {}",
					CLASS,
					suid);
			logger.debug("SUID value :: {}", suid);
			RAServiceAsserts.notNullorEmpty(suid, ErrorCodes.E_INVALID_REQUEST);
			return  subscriberServiceIfaceImpl.getSubscriberPhoto(suid);

		} catch (Exception e) {
			logger.error("{} :: getSubscriberDetailsBySearchType() :: error", CLASS, e);
			return AppUtil.createApiResponse(false,e.getMessage(),null);

		}
	}

	@PostMapping(value = "/post/get/qr/data")
	public Response getSignForQr(@RequestBody QrData qrData) {
		try {
			logger.info("{} :: getSignForQr :: request :: {}", CLASS, qrData);
			return subscriberServiceIfaceImpl.getSignForQr(qrData);
		}
		catch (Exception e) {
			logger.error(UNEXPECTED_ERROR, e);
			return new Response(false, ERROR, e.getMessage());
		}
	}

	@PostMapping(value = "/post/get/qr/data/encrypt")
	public String getSignForQrEncrypt(@RequestBody String request) throws JsonProcessingException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Result result = DAESService.decryptSecureWireData(request);
			String decryptedString = new String(result.getResponse());
			QrData qrData = mapper.readValue(decryptedString,QrData.class);
			logger.info("{} :: getSignForQr :: request :: {}", CLASS, qrData);
			Response response = subscriberServiceIfaceImpl.getSignForQr(qrData);
			String responseString = mapper.writeValueAsString(response);
			Result encrypt = DAESService.createSecureWireData(responseString);
			return new String(encrypt.getResponse());
		}
		catch(Exception e){
			logger.error(UNEXPECTED_ERROR, e);
			return new Response(false , ERROR, e.getMessage()).toString();
		}
	}

	@PostMapping({"/post/expire/subscriber-cert"})
	public ApiResponse expireSubscriberCert(@RequestBody ExpireSubscriberCertRequestDTO expireSubscriberCertRequestDTO) {
		try {
			logger.info("RASubscriberRestController :: expireSubscriberCert() :: request :: email :: {} :: mobile :: {}",
					expireSubscriberCertRequestDTO.getEmail(),
					expireSubscriberCertRequestDTO.getMobileNumber());

			return this.raServiceIface.expireSubscriberCert(expireSubscriberCertRequestDTO);
		} catch (Exception e) {
			logger.error(UNEXPECTED_ERROR, e);
			return AppUtil.createApiResponse(false, ERROR, e.getMessage());
		}
	}

}
