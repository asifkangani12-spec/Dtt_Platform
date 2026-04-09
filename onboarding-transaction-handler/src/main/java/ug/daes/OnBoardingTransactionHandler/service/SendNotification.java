/**
 *
 */
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
import ug.daes.OnBoardingTransactionHandler.dto.sendNotification.PushNotificationRequest;

import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Raxit Dubey
 *
 */
@Service
public class SendNotification {

    private final RestTemplate restTemplate;


	private static Logger logger = LoggerFactory.getLogger(SendNotification.class);

	/** The Constant CLASS. */
	static final String CLASS = "SendNotification";

	private final PropertiesUtil propertiesUtil;

	String baseUrl;

	String agentsBaseUrl;

	private final TxHandlerPropertiesConfiguration config;

	@PostConstruct
	public void init() {
		this.baseUrl = config.getNotification();
		this.agentsBaseUrl = config.getAgentsNotification();
	}

	private final ObjectMapper objectMapper;

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public SendNotification(RestTemplate restTemplate, PropertiesUtil propertiesUtil, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.propertiesUtil = propertiesUtil;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse setSendNotification(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			PushNotificationRequest notificationRequest = new PushNotificationRequest();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			notificationRequest = objectMapper.readValue(s, PushNotificationRequest.class);

			System.out.println("OnBoardingTransactionHandler in SendNotification service req :: " +notificationRequest);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(notificationRequest, headers);
			res = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity,
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

	public ApiResponse setAgentsSendNotification(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			PushNotificationRequest notificationRequest = new PushNotificationRequest();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			notificationRequest = objectMapper.readValue(s, PushNotificationRequest.class);
			System.out.println("OnBoardingTransactionHandler in SendNotification service req :: " +notificationRequest);

			System.out.println("baseUrl notification "+agentsBaseUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(notificationRequest, headers);
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
