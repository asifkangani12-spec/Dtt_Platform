package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.template.OrganizationDto;

import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrganizationService {

    private final RestTemplate restTemplate;


	private static Logger logger = LoggerFactory.getLogger(OrganizationService.class);

	/** The Constant CLASS. */
	static final String CLASS = "OrganizationService";

	private final PropertiesUtil propertiesUtil;
	private final ObjectMapper objectMapper;
	String organizationBaseUrl = PropertiesUtil.organizationBaseUrl;

	private final TxHandlerPropertiesConfiguration config;

	@PostConstruct
	public void init() {
		this.organizationBaseUrl = config.getOrganization();
	}

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public OrganizationService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, ObjectMapper objectMapper, TxHandlerPropertiesConfiguration config, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.propertiesUtil = propertiesUtil;
		this.objectMapper = objectMapper;
		this.config = config;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse getCertificateDetails(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			OrganizationDto organizationDto = new OrganizationDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			organizationDto = objectMapper.readValue(s, OrganizationDto.class);
			String orgUid = organizationDto.getOrgUid();
			String url = organizationBaseUrl + "/api/get/certificate/details?orgUid=" + orgUid;
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

	public ApiResponse getSponsorListBySuidForUserSubscription(HttpHeaders httpHeaders, Object o) throws Exception {

		String data;
		ResponseEntity<ApiResponse> res = null;
		try {

			System.out.println(" getSponsors ");
			ObjectMapper objectMapper = new ObjectMapper();
			data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(data);
			String url = organizationBaseUrl + "/api/verify/onboarding-sponsorship?suid="
					+ jsonArrayNode.get("suid").asText();
			System.out.println("url " + url);
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

	public ApiResponse linkSponsorByBeneficiarySuid(HttpHeaders httpHeaders, Object o) throws Exception {

		String data;
		ResponseEntity<ApiResponse> res = null;
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(data);

			String url = organizationBaseUrl + "/api/link-sponsor?id=" + jsonArrayNode.get("id")
					+ "&beneficiaryDigitalId=" + jsonArrayNode.get("beneficiaryDigitalId").asText();
			System.out.println("url " + url);

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

	public ApiResponse getSponsorsBySuid(HttpHeaders httpHeaders, Object o) throws Exception {

		String data;
		ResponseEntity<ApiResponse> res = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			JsonNode jsonArrayNode = objectMapper.readTree(data);

			String url = organizationBaseUrl + "/api/get/all/sponsors?&suid="
					+ jsonArrayNode.get("beneficiaryDigitalId").asText();
			System.out.println("url " + url);

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

	public ApiResponse getAllCertificateDetails(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			OrganizationDto organizationDto = new OrganizationDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			organizationDto = objectMapper.readValue(s, OrganizationDto.class);

			String url = organizationBaseUrl + "/api/get/all/organizations/cert?orgId=" + organizationDto.getOrgUid();
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

}
