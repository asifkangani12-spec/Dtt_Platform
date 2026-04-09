package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.template.BenificiariesDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.PaymentRequestDTO;
import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;


	private static Logger logger = LoggerFactory.getLogger(PaymentService.class);

	/** The Constant CLASS. */
	static final String CLASS = "PaymentService";

	String baseUrl;

	private final TxHandlerPropertiesConfiguration config;

	@PostConstruct
	public void init() {
		this.baseUrl = config.getPayment();
	}

	private final ObjectMapper objectMapper;

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public PaymentService(RestTemplate restTemplate, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse payment(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			paymentRequestDTO = objectMapper.readValue(s, PaymentRequestDTO.class);

			String url = baseUrl + "/api/post/payment";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(paymentRequestDTO, headers);
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

	public ApiResponse paymentSlabPrice(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);

			paymentRequestDTO = objectMapper.readValue(s, PaymentRequestDTO.class);

			String url = baseUrl + "/api/post/payment/slab";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(paymentRequestDTO, headers);
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

	public ApiResponse getAggregatorFee() throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			String url = baseUrl + "/api/get/aggregator-fee";

			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

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


	public ApiResponse activateUserSubscriptionBySponsorId(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			BenificiariesDTO paymentRequestDTO = new BenificiariesDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);

			paymentRequestDTO = objectMapper.readValue(s, BenificiariesDTO.class);

			String url = baseUrl + "/api/post/sponsor/payment";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(paymentRequestDTO, headers);
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

}
