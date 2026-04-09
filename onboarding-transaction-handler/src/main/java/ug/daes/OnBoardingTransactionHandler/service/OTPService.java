/**
 *
 */
package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;


import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.template.GetSubscriberObDataDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.MobileOTPDto;
import ug.daes.OnBoardingTransactionHandler.dto.template.RegisterDeviceDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.SubscriberObRequestDTO;


import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Raxit Dubey
 *
 */
@Service
public class OTPService {

    private final RestTemplate restTemplate;


	private static Logger logger = LoggerFactory.getLogger(OTPService.class);

	/** The Constant CLASS. */
	static final String CLASS = "OTPService";

	private final TxHandlerPropertiesConfiguration config;

	private String baseUrl;

	@PostConstruct
	public void init() {
		this.baseUrl = config.getOnboarding();
	}

	private final ObjectMapper objectMapper;

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public OTPService(RestTemplate restTemplate, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse deviceRegistration(HttpHeaders httpHeaders, Object o) throws Exception{
		ResponseEntity<ApiResponse> res = null;
		try {
			RegisterDeviceDTO registerDeviceDTO = new RegisterDeviceDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);

			registerDeviceDTO = objectMapper.readValue(s, RegisterDeviceDTO.class);
			String url = baseUrl + "/api/post/register/device";
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(registerDeviceDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}


	public ApiResponse sendMobileOTP(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			MobileOTPDto mobileDTO = new MobileOTPDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			mobileDTO = objectMapper.readValue(s, MobileOTPDto.class);

			String url = baseUrl + "/api/post/register-subscriber";
			System.out.println("baseUrl: "+baseUrl);
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(mobileDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse saveSubscriberDetails(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			MobileOTPDto mobileDTO = new MobileOTPDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			mobileDTO = objectMapper.readValue(s, MobileOTPDto.class);
			String url = baseUrl + "/api/post/save-subscriber-details";
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(mobileDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse verifyNewDevice(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			MobileOTPDto mobileDTO = new MobileOTPDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			mobileDTO = objectMapper.readValue(s, MobileOTPDto.class);

			String url = baseUrl + "/api/post/verify-new-device";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(mobileDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}


	public ApiResponse activateNewDevice(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			MobileOTPDto mobileDTO = new MobileOTPDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			mobileDTO = objectMapper.readValue(s, MobileOTPDto.class);

			String url = baseUrl + "/api/post/activate-new-device";
			System.out.println(url);

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(mobileDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse getSubscriberImage(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			MobileOTPDto mobileDTO = new MobileOTPDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			mobileDTO = objectMapper.readValue(s, MobileOTPDto.class);

			String url = baseUrl + "/api/post/subscriber-image";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(mobileDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse saveSubscriberOnboarding(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			SubscriberObRequestDTO obRequestDTO = new SubscriberObRequestDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			obRequestDTO = objectMapper.readValue(s, SubscriberObRequestDTO.class);

			String url = baseUrl + "/api/post/add/subscriber-ob-data";
			System.out.println(" ob url "+url);

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(obRequestDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	//Re-OnBoarding
	public ApiResponse saveSubscriberReOnboarding(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			SubscriberObRequestDTO obRequestDTO = new SubscriberObRequestDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			obRequestDTO = objectMapper.readValue(s, SubscriberObRequestDTO.class);

			String url = baseUrl + "/api/post/reonboard/subscriber-ob-data";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(obRequestDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse getSubscriberOnboarding(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			GetSubscriberObDataDTO obRequestDTO = new GetSubscriberObDataDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			obRequestDTO = objectMapper.readValue(s, GetSubscriberObDataDTO.class);

			String url = baseUrl + "/api/post/fetch/subscriber-ob-data";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(obRequestDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);

			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse resetPin(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			GetSubscriberObDataDTO obRequestDTO = new GetSubscriberObDataDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			obRequestDTO = objectMapper.readValue(s, GetSubscriberObDataDTO.class);

			String url = baseUrl + "/api/post/fetch/reset-pin";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(obRequestDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}


	}


	public ApiResponse getDeviceStatus(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			logger.info(CLASS+" saveSubscriberDetails req with time 1 {},{}");
			String url = baseUrl + "/api/get/devicestatus";
			System.out.println(url);
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
					ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException |HttpServerErrorException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse updateSubscriberFcmToken(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;

		String errCode;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			errCode = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(errCode);
			String url = this.baseUrl + "/api/post/update/fcmtoken?suid=" + jsonArrayNode.get("suid").asText() + "&fcmToken=" + jsonArrayNode.get("fcmToken").asText();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity(headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse getSubscriberProfiles(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		String errCode;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			errCode = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(errCode);
			String url = this.baseUrl + "/api/get/subscriber/profile/"+jsonArrayNode.get("searchType").asText()+"/"+jsonArrayNode.get("searchValue").asText();
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse getVerificationChannelResponse(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		String errCode;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			errCode = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(errCode);
			String url = this.baseUrl + "/api/get/verification-channel-response/"+jsonArrayNode.get("suid").asText();
			System.out.println(url);
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}
}
