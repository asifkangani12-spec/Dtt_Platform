package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
import ug.daes.OnBoardingTransactionHandler.dto.template.SubscriberDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.TemplateApproveDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.TemplateDTO;

import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;




/**
 * @author Raxit Dubey
 *
 */
@Service
public class TemplatesService {

    private final RestTemplate restTemplate;


	/** The Constant CLASS. */
	static final String CLASS = "TemplatesService";

	private final PropertiesUtil propertiesUtil;

	String baseUrl;

	private final TxHandlerPropertiesConfiguration config;


	@PostConstruct
	public void init() {
		this.baseUrl = config.getOnboarding();
	}



	private final ObjectMapper objectMapper;

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public TemplatesService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.propertiesUtil = propertiesUtil;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse addTemplate(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			TemplateDTO templateDTO = new TemplateDTO();

			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			templateDTO = objectMapper.readValue(s, TemplateDTO.class);

			String url = baseUrl + "/api/auth/save/template";
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(templateDTO, headers);
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

	public ApiResponse addTemplateApproval(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			TemplateApproveDTO templateDTO = new TemplateApproveDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			templateDTO = objectMapper.readValue(s, TemplateApproveDTO.class);
			String url = baseUrl + "/api/post/approve-template";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(templateDTO, headers);
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

	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse getActiveTemplate(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			SubscriberDTO subscriberDTO = new SubscriberDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			subscriberDTO = objectMapper.readValue(s, SubscriberDTO.class);

			String url = baseUrl + "/api/post/activte-template";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			System.out.println("hedareddd"+httpHeaders);




			HttpEntity<Object> requestEntity = new HttpEntity<>(subscriberDTO, headers);
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

	/**
	 * @return
	 * @throws Exception
	 */
	public ApiResponse getTemplates() throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/auth/get/templates";
			System.out.println("url ==>> " + url);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ApiResponse getTemplateByID(HttpHeaders httpHeaders,Object id) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/auth/get/template-by-id?id=" + id;

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ApiResponse deleteTemplateByID(HttpHeaders httpHeaders,Object id) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/delete/template-by-id?id=" + id;

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ApiResponse publishTemplateByID(HttpHeaders httpHeaders,Object id) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/update/template-status?id=" + id + "&status=PUBLISHED";
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ApiResponse unPublishTemplateByID(HttpHeaders httpHeaders,Object id) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/update/template-status?id=" + id + "&status=UNPUBLISHED";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	public ApiResponse isTemplateExist(Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			TemplateDTO templateDTO = new TemplateDTO();

			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			templateDTO = objectMapper.readValue(s, TemplateDTO.class);

			String url = baseUrl + "/api/get/template-exist?templateName=" + templateDTO.getTemplateName()
					+ "&methodName=" + templateDTO.getTemplateMethod();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(templateDTO, headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	public ApiResponse getSteps() throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/auth/get/onboarding-step";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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

	public ApiResponse getMethods() throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/auth/get/methods";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
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
}
