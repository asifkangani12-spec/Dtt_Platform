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
import ug.daes.OnBoardingTransactionHandler.dto.template.*;
import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

@Service
public class UpdateSubscriberService {

    private final RestTemplate restTemplate;



	/** The Constant CLASS. */
	static final String CLASS = "UpdateSubscriberService";

    private final PropertiesUtil propertiesUtil;
    private final ObjectMapper objectMapper;
    String baseUrl;
    private final TxHandlerPropertiesConfiguration config;
    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public UpdateSubscriberService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, ObjectMapper objectMapper, TxHandlerPropertiesConfiguration config, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.restTemplate = restTemplate;
        this.propertiesUtil = propertiesUtil;
        this.objectMapper = objectMapper;
        this.config = config;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    @PostConstruct
    public void init() {
        this.baseUrl = config.getOnboarding();
    }

    String organizationBaseUrl = PropertiesUtil.organizationBaseUrl;

    public ApiResponse updateSubscriberDetails(HttpHeaders httpHeaders,Object o) throws Exception {
    	ResponseEntity<ApiResponse> res = null;
        try {
            UpdateDto updateDto = new UpdateDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            updateDto = objectMapper.readValue(s, UpdateDto.class);

            String url = baseUrl + "/api/post/updateSubscriberDetails";

            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

            HttpEntity<Object> requestEntity = new HttpEntity<>(updateDto, headers);
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

    public ApiResponse getOtp(HttpHeaders httpHeaders,Object o) throws Exception {
    	ResponseEntity<ApiResponse> res = null;
        try {
            UpdateOtpDto otpDto = new UpdateOtpDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            otpDto = objectMapper.readValue(s, UpdateOtpDto.class);

            String url = baseUrl + "/api/post/updateSubscriberOtp";
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

            HttpEntity<Object> requestEntity = new HttpEntity<>(otpDto, headers);
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

    public ApiResponse linkEmail(HttpHeaders httpHeaders,Object o) throws Exception {
    	ResponseEntity<ApiResponse> res = null;
        try {
            OrgUser userDto = new OrgUser();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            userDto = objectMapper.readValue(s, OrgUser.class);

            String url = organizationBaseUrl + "/api/post/linkEmail";
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

            HttpEntity<Object> requestEntity = new HttpEntity<>(userDto, headers);
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

    public ApiResponse sendEmailLinkOtp(HttpHeaders httpHeaders,Object o) throws Exception {
    	ResponseEntity<ApiResponse> res = null;
        try {
            EmailReqDto emailDTO = new EmailReqDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            emailDTO = objectMapper.readValue(s, EmailReqDto.class);

            String url = organizationBaseUrl + "/api/post/otp";
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

            HttpEntity<Object> requestEntity = new HttpEntity<>(emailDTO, headers);
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

    public ApiResponse getOrgList(HttpHeaders httpHeaders,Object o) throws Exception {
    	ResponseEntity<ApiResponse> res = null;
        try {
            OrgListDto orgListDto = new OrgListDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            orgListDto = objectMapper.readValue(s, OrgListDto.class);

            String url = organizationBaseUrl + "/api/get/org/list/by-suid"+"?suid="+orgListDto.getSuid();
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



}
