package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.policy.PolicyDTO;

@Service
public class PolicyService {

    private final RestTemplate restTemplate;


    private static Logger logger = LoggerFactory.getLogger(PolicyService.class);

    /** The Constant CLASS. */
    static final String CLASS = "PolicyService";

    String baseUrl;

    private final ObjectMapper objectMapper;

	private final TxHandlerPropertiesConfiguration config;

	@PostConstruct
	public void init() {
		this.baseUrl = config.getOnboarding();
	}

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public PolicyService(RestTemplate restTemplate, ObjectMapper objectMapper, TxHandlerPropertiesConfiguration config, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.config = config;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse verifyServiceForDevice(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			PolicyDTO policyDTO = new PolicyDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);

			policyDTO = objectMapper.readValue(s, PolicyDTO.class);

			String url = baseUrl + "/api/get/verify-policy";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			policyDTO.setDeviceUid(httpHeaders.getFirst("deviceId"));
			policyDTO.setSuid(httpHeaders.getFirst("suid"));
			HttpEntity<Object> requestEntity = new HttpEntity<>(policyDTO, headers);
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
