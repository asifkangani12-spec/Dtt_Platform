package ug.daes.OnBoardingTransactionHandler.service;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.template.GetPriceSlabOrg;
import ug.daes.OnBoardingTransactionHandler.dto.template.PricingSlabDefinitions;
import ug.daes.OnBoardingTransactionHandler.dto.template.RecordDto;
import ug.daes.OnBoardingTransactionHandler.dto.template.ServiceFeeReqDto;
import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PriceModelService {

    private final RestTemplate restTemplate;


	private static Logger logger = LoggerFactory.getLogger(PriceModelService.class);

	/** The Constant CLASS. */
	static final String CLASS = "PriceModelService";

	private final PropertiesUtil propertiesUtil;
	String baseUrl;

	private final TxHandlerPropertiesConfiguration config;

	@PostConstruct
	public void init() {
		this.baseUrl = config.getPricemodel();
	}

	private final ObjectMapper objectMapper;

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public PriceModelService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.propertiesUtil = propertiesUtil;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse getServices(HttpHeaders httpHeaders, Object o) {
		ResponseEntity<ApiResponse> res = null;
		try {
			RecordDto recordDto = new RecordDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			recordDto = objectMapper.readValue(s, RecordDto.class);

			String url = baseUrl + "/api/get-services?suid=" + recordDto.getSuid();
			RestTemplate rest = new RestTemplate();

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = rest.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);

			return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	
	public ApiResponse getRemCredits(HttpHeaders httpHeaders,Object o) {
		ResponseEntity<ApiResponse> res = null;
		try {
			RecordDto recordDto = new RecordDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			recordDto = objectMapper.readValue(s, RecordDto.class);

			logger.info(CLASS+" getRemCredits req with time 1 {},{}", recordDto.getSuid(), AppUtil.getDate());
			String url = baseUrl + "/api/get/remaining-credits?suid=" + recordDto.getSuid();
			RestTemplate rest = new RestTemplate();

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = rest.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);

			return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	public ApiResponse getPriceSlabByServiceAndStakeHolder(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			PricingSlabDefinitions pricingSlabDefinitions = new PricingSlabDefinitions();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			pricingSlabDefinitions = objectMapper.readValue(s, PricingSlabDefinitions.class);

			String url = baseUrl + "/api/get-price-slab?serviceId=" + pricingSlabDefinitions.getServiceId()
					+ "&stakeHolder=" + pricingSlabDefinitions.getStakeHolder();

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(pricingSlabDefinitions, headers);
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

	public ApiResponse getPriceSlabOrg(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			GetPriceSlabOrg getPriceSlabOrg = new GetPriceSlabOrg();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			getPriceSlabOrg = objectMapper.readValue(s, GetPriceSlabOrg.class);

			String url = baseUrl + "/api/get-price-slab-org?orgId=" + getPriceSlabOrg.getOrgId() + "&serviceId="
					+ getPriceSlabOrg.getServiceId();
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
			HttpEntity<Object> requestEntity = new HttpEntity<>(getPriceSlabOrg, headers);
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

	public ApiResponse getPayHistory(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			RecordDto recordDto = new RecordDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			recordDto = objectMapper.readValue(s, RecordDto.class);

			String url = baseUrl + "/api/get-payment-history?suid=" + recordDto.getSuid();

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(recordDto, headers);
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

	public ApiResponse getSubscriptionFee(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			RecordDto recordDto = new RecordDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			recordDto = objectMapper.readValue(s, RecordDto.class);

			String url = baseUrl + "/api/get/onboarding-subscription-fee?suid=" + recordDto.getSuid();

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(recordDto, headers);
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

	public ApiResponse getOrganizationRemainingCredits(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			GetPriceSlabOrg getPriceSlabOrg = new GetPriceSlabOrg();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			getPriceSlabOrg = objectMapper.readValue(s, GetPriceSlabOrg.class);

			String url = baseUrl + "/api/get-org-rem-credits?orgId=" + getPriceSlabOrg.getOrgId();

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(getPriceSlabOrg, headers);
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

	public ApiResponse getServiceFee(HttpHeaders httpHeaders,Object o) {
		ResponseEntity<ApiResponse> res = null;
    	try {
    		ServiceFeeReqDto serviceFeeReqDto = new ServiceFeeReqDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			serviceFeeReqDto = objectMapper.readValue(s, ServiceFeeReqDto.class);

			String url = baseUrl + "/api/get-service-fee";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(serviceFeeReqDto,headers);
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
