package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.mosip.AuthRequestDTO;
import ug.daes.OnBoardingTransactionHandler.dto.mosip.GetMosipEmailOtpDto;
import ug.daes.OnBoardingTransactionHandler.dto.mosip.GetProfileByOTP;


import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

@Service
public class MosipService {

    private final RestTemplate restTemplate;

    private final PropertiesUtil propertiesUtil;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    String mosipUrl;

    private final TxHandlerPropertiesConfiguration config;

    private final ObjectMapper objectMapper;

    public MosipService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, ExceptionHandlerUtil exceptionHandlerUtil, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.propertiesUtil = propertiesUtil;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.config = config;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        this.mosipUrl = config.getMosipBaseUrl();
    }

    public ApiResponse getMosipEmailOtp (HttpHeaders httpHeaders, Object o) {
        ResponseEntity<ApiResponse> res = null;
        try {

            GetMosipEmailOtpDto getMosipEmailOtpDto = new GetMosipEmailOtpDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            getMosipEmailOtpDto = objectMapper.readValue(s, GetMosipEmailOtpDto.class);
            String url = mosipUrl + "/api/get/otp";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(getMosipEmailOtpDto,headers);
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


    public ApiResponse getProfileByOTP (HttpHeaders httpHeaders, Object o) {
        ResponseEntity<ApiResponse> res = null;
        try {

            GetProfileByOTP getProfileByOTP = new GetProfileByOTP();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            getProfileByOTP = objectMapper.readValue(s, GetProfileByOTP.class);
            String url = mosipUrl + "/api/get/profile";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(getProfileByOTP,headers);
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

    public ApiResponse getProfileByFingerPrint (HttpHeaders httpHeaders, Object o) {
        ResponseEntity<ApiResponse> res = null;
        try {

            AuthRequestDTO authRequestDTO = new AuthRequestDTO();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            authRequestDTO = objectMapper.readValue(s, AuthRequestDTO.class);
            String url = mosipUrl + "api/get/profile/fingerprint";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(authRequestDTO,headers);
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
