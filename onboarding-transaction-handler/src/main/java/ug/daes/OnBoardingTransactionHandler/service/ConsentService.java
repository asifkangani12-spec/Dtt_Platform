/**
 *
 */
package ug.daes.OnBoardingTransactionHandler.service;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.consent.Consent;
import ug.daes.OnBoardingTransactionHandler.dto.template.NiraApiLogDto;
import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raxit Dubey
 *
 */
@Service("ConsentService")
public class ConsentService {

    private final RestTemplate restTemplate;


    private static Logger logger = LoggerFactory.getLogger(ConsentService.class);

    /** The Constant CLASS. */
    static final String CLASS = "ConsentService";

    private final TxHandlerPropertiesConfiguration config;

    private String baseUrl;

    @PostConstruct
    public void init() {
        this.baseUrl = config.getOnboarding();
    }

    private final ObjectMapper objectMapper;

    private static final String ACCEPT_LANGUAGE ="Accept-Language";

   private  final ExceptionHandlerUtil exceptionHandlerUtil;

    public ConsentService(RestTemplate restTemplate, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.objectMapper = objectMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ApiResponse addConsent(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            Consent consent = new Consent();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            consent = objectMapper.readValue(s, Consent.class);
            String url = baseUrl + "/api/add/consent";
            HttpHeaders headers = HeaderUtil.createHeadersForGET(httpHeaders);
            HttpEntity<Object> requestEntity = new HttpEntity<>(consent, headers);
            res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}


    /**
     * @return
     * @throws Exception
     */
    public ApiResponse getActiveConsent(HttpHeaders headers, Object body) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            String url = baseUrl + "/api/activte/consent";

            HttpHeaders headers1 =  HeaderUtil.createHeadersForGET(headers);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers1);


            RestTemplate rest = new RestTemplate();
            System.out.println("Heasers"+headers);
            res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
            System.out.println("Reponse::"+res);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse getConsentList() throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {

            String url = baseUrl + "/api/get/list/consent";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

            res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse getConsentById(HttpHeaders httpHeaders, Object id) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            String url = baseUrl + "/api/get/consent/id?id=" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

            res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse updateConsentActive(HttpHeaders httpHeaders, Object id) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            String url = baseUrl + "/api/update/consent/status?consentId=" + id + "&status=ACTIVE";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

            res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse updateConsentInActive(HttpHeaders httpHeaders, Object id) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            String url = baseUrl + "/api/update/consent/status?consentId=" + id + "&status=INACTIVE";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

            res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse saveNiraApiLog(HttpHeaders httpHeaders, Object id) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            NiraApiLogDto niraApiLogDto = new NiraApiLogDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(id);
            niraApiLogDto = objectMapper.readValue(s, NiraApiLogDto.class);
            String url = baseUrl + "/api/save/nira/logs";

            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
            HttpEntity<Object> requestEntity = new HttpEntity<>(niraApiLogDto, headers);

            res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse signConsentData(HttpHeaders httpHeaders , Object id) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {

            String url = baseUrl + "/sign-data/for/consent";

            HttpHeaders headers = HeaderUtil.createHeadersForSignConsentData(httpHeaders);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

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
