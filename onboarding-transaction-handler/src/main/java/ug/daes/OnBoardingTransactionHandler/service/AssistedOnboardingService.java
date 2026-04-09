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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.ra.IssueCertDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.GetProfileDto;
import ug.daes.OnBoardingTransactionHandler.dto.template.SubscriberObRequestDTOAgents;
import ug.daes.OnBoardingTransactionHandler.dto.template.UpdateAgentFcmTokenDto;

import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

@Service
public class AssistedOnboardingService {

    private final RestTemplate restTemplate;

	private final PropertiesUtil propertiesUtil;

	String assistedOnboardingUrl;

	private final ObjectMapper objectMapper;

	private final TxHandlerPropertiesConfiguration config;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

	public AssistedOnboardingService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, ObjectMapper objectMapper, TxHandlerPropertiesConfiguration config, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.propertiesUtil = propertiesUtil;
		this.objectMapper = objectMapper;
		this.config = config;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	@PostConstruct
	public void init() {
		this.assistedOnboardingUrl = config.getAssistedOnboarding();
	}


    /**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse checkAgent(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			UpdateAgentFcmTokenDto updateAgentFcmTokenDto = new UpdateAgentFcmTokenDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			updateAgentFcmTokenDto = objectMapper.readValue(s, UpdateAgentFcmTokenDto.class);

			String url = assistedOnboardingUrl + "/api/check/agent/by/suid";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(updateAgentFcmTokenDto, headers);
			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);
		} catch (HttpClientErrorException e) {
			return exceptionHandlerUtil.handleException(e);
		} catch (HttpServerErrorException e) {
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
	public ApiResponse assistedOnboardSubscriber(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			SubscriberObRequestDTOAgents subscriberObRequestDTO = new SubscriberObRequestDTOAgents();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			subscriberObRequestDTO = objectMapper.readValue(s, SubscriberObRequestDTOAgents.class);

			String url = assistedOnboardingUrl + "/api/onboard/privileged/subscriber";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(subscriberObRequestDTO, headers);
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

	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse getOnboardPrivilegedSubscriber(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			GetProfileDto getProfileDto = new GetProfileDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			getProfileDto = objectMapper.readValue(s, GetProfileDto.class);

			String url = assistedOnboardingUrl + "/api/get/profile/by/id-doc-number";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(getProfileDto, headers);
			System.out.println(" url " + url);
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

	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse getPrivilegedSubscriberList(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(data);

			String url = assistedOnboardingUrl + "/api/get/OnboardedUser-By-Agent-suid/" + jsonArrayNode.get("suid").asText();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			System.out.println(" url " + url);
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

	/**
	 * @param_suid
	 * @return
	 * @throws Exception
	 */
	public ApiResponse generateFailedCertificateByAgent(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			IssueCertDTO issueCertDTO = new IssueCertDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			issueCertDTO = objectMapper.readValue(s, IssueCertDTO.class);

			String url = assistedOnboardingUrl + "/api/post/service/certificate/request";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(issueCertDTO, headers);

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

}
