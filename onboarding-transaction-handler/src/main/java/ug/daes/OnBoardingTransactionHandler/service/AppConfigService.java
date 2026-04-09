package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.template.AppConfigDTO;
import ug.daes.OnBoardingTransactionHandler.dto.template.AppConfigReqDTO;

import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

@Service
public class AppConfigService
{
    private final RestTemplate restTemplate;

    private final PropertiesUtil propertiesUtil;

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;
    static final String CLASS = "AppConfigService";

    private final TxHandlerPropertiesConfiguration config;
    private  final ExceptionHandlerUtil exceptionHandlerUtil;

    public AppConfigService(RestTemplate restTemplate,
                            PropertiesUtil propertiesUtil,
                            MessageSource messageSource,
                            ObjectMapper objectMapper,
                            TxHandlerPropertiesConfiguration config,
                            ExceptionHandlerUtil exceptionHandlerUtil) {

        this.restTemplate = restTemplate;
        this.propertiesUtil = propertiesUtil;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
        this.config = config;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    private String baseUrl;
    private String appconfigUrl;

    @PostConstruct
    public void init() {
        this.baseUrl = config.getOnboarding();       // if needed
        this.appconfigUrl = config.getAppConfig(); // <-- create getter if not exists
    }



    public ApiResponse checkUpdate(HttpHeaders httpHeaders, final Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            AppConfigReqDTO appConfigReqDTO = new AppConfigReqDTO();
            final String s = this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            appConfigReqDTO = (AppConfigReqDTO)this.objectMapper.readValue(s, (Class)AppConfigReqDTO.class);
            final String url = this.appconfigUrl + "/api/check/update";
            System.out.println("appconfigUrl :: " + url);
            HttpHeaders headers = HeaderUtil.createHeadersForGET(httpHeaders);
            final HttpEntity<Object> requestEntity = new HttpEntity(appConfigReqDTO, headers);
            res = restTemplate.exchange(url, HttpMethod.POST, (HttpEntity)requestEntity, (Class)ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

        }  catch (Exception e) {
            return exceptionHandlerUtil.handleException(e);
        }
    }

    public ApiResponse addAppConfig(HttpHeaders httpHeaders,final Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            AppConfigDTO appConfigDTO = new AppConfigDTO();
            final String s = this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            appConfigDTO = (AppConfigDTO)this.objectMapper.readValue(s, (Class)AppConfigDTO.class);
            final String url = this.appconfigUrl + "/api/save";
            System.out.println("url :: " + url);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<Object> requestEntity = new HttpEntity(appConfigDTO, headers);
            res = restTemplate.exchange(url, HttpMethod.POST, (HttpEntity)requestEntity, (Class)ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

        }  catch (Exception e) {
            return exceptionHandlerUtil.handleException(e);
        }
    }

    public ApiResponse getAppConfigList() throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {

            final String url = this.appconfigUrl + "/api/get/AppConfig";
            System.out.println("url :: " + url);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<Object> requestEntity = new HttpEntity(headers);
            res = restTemplate.exchange(url, HttpMethod.GET, (HttpEntity)requestEntity, (Class)ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

        } catch (Exception e) {
            return exceptionHandlerUtil.handleException(e);
        }
    }

    public ApiResponse appConfigurationList() throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {

            final String url = this.appconfigUrl + "/api/get/AppConfigurationList";
            System.out.println("url :: " + url);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("AssistedOnboarding","0");
            final HttpEntity<Object> requestEntity = new HttpEntity(headers);
            res = restTemplate.exchange(url, HttpMethod.GET, (HttpEntity)requestEntity, (Class)ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

        }  catch (Exception e) {
            return exceptionHandlerUtil.handleException(e);
        }
    }

    public ApiResponse appConfigurationListForAssistedOnboarding() throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            final String url = this.appconfigUrl + "/api/get/AppConfigurationList";

            System.out.println("url :: " + url);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("AssistedOnboarding","1");
            final HttpEntity<Object> requestEntity = new HttpEntity(headers);
            res = restTemplate.exchange(url, HttpMethod.GET, (HttpEntity)requestEntity, (Class)ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

        }  catch (Exception e) {
            return exceptionHandlerUtil.handleException(e);
        }
    }

}