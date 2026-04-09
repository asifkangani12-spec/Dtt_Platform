package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.TemporaryTableDTO;
import ug.daes.OnBoardingTransactionHandler.dto.TitleDto;
import ug.daes.OnBoardingTransactionHandler.dto.UpdateTemporaryTableDto;
import ug.daes.OnBoardingTransactionHandler.dto.template.MainDto;
import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;


@Service
public class    ProposedOnboardingService {

    private final RestTemplate restTemplate;



    private static Logger logger = LoggerFactory.getLogger(ProposedOnboardingService.class);

    /** The Constant CLASS. */
    static final String CLASS = "ProposedOnboardingService";

    @Value("${url.onboarding}")
    private String onboardingUrl;



    private final TxHandlerPropertiesConfiguration config;

    TxHandlerPropertiesConfiguration propertiesConfiguration=new TxHandlerPropertiesConfiguration();

    PropertiesUtil util = new PropertiesUtil(propertiesConfiguration);

    String verificationChannelBaseUrl = util.getVerificationChannelBaseUrl();

    private String baseUrl;

    @PostConstruct
    public void init() {
        this.baseUrl = config.getOnboarding();
    }

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    private final ObjectMapper objectMapper;

    public ProposedOnboardingService(RestTemplate restTemplate, TxHandlerPropertiesConfiguration config, ExceptionHandlerUtil exceptionHandlerUtil, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.objectMapper = objectMapper;
    }


    public ApiResponse saveDataInTemporaryTable(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            TemporaryTableDTO temporaryTableDTO = new TemporaryTableDTO();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            temporaryTableDTO = objectMapper.readValue(s, TemporaryTableDTO.class);

            String url = baseUrl + "/api/save/temporary-data";
            System.out.println("URL for saving ::::::"+url);
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
            HttpEntity<Object> requestEntity = new HttpEntity<>(temporaryTableDTO,headers);

            res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    ApiResponse.class);
            return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
            e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
            e.printStackTrace();
			return ExceptionHandlerUtil.handleGenericException(e);
		}
    }

    public ApiResponse checkMobileAndEmail(HttpHeaders httpHeaders, Object o)  {
        ResponseEntity<ApiResponse> res = null;
        try {
            TemporaryTableDTO temporaryTableDTO ;
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            temporaryTableDTO = objectMapper.readValue(s, TemporaryTableDTO.class);
            String url = baseUrl + "/api/validate/mobile/or/email";

            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

            HttpEntity<Object> requestEntity = new HttpEntity<>(temporaryTableDTO,headers);

            res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    ApiResponse.class);

            logger.info("RestTemplate response: {}", res);
            return exceptionHandlerUtil.handleResponse(res);

        } catch (HttpClientErrorException e) {
            logger.error("{}",e.getMessage());
            return exceptionHandlerUtil.handleHttpException(e);
        }  catch (ResourceAccessException e) {
            logger.error("{}",e.getMessage());
            return exceptionHandlerUtil.handleResourceAccessException(e);
        } catch (Exception e) {
            logger.error("{}",e.getMessage());
            return ExceptionHandlerUtil.handleGenericException(e);
        }
    }

    public ApiResponse submitOnboardingData(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            TemporaryTableDTO temporaryTableDTO = new TemporaryTableDTO();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            temporaryTableDTO = objectMapper.readValue(s, TemporaryTableDTO.class);

            String url = baseUrl + "/api/submit/ob-data/"+temporaryTableDTO.getIdDocNumber();
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

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


    public ApiResponse updateRecordInTemporaryTable(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            UpdateTemporaryTableDto updateTemporaryTableDto = new UpdateTemporaryTableDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            updateTemporaryTableDto = objectMapper.readValue(s, UpdateTemporaryTableDto.class);

            String url = baseUrl + "/api/update/temporaryTable";
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
            HttpEntity<Object> requestEntity = new HttpEntity<>(updateTemporaryTableDto,headers);

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

    public ApiResponse deleteRecordInTemporaryTable(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            UpdateTemporaryTableDto updateTemporaryTableDto = new UpdateTemporaryTableDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            updateTemporaryTableDto = objectMapper.readValue(s, UpdateTemporaryTableDto.class);

            String url = baseUrl + "/api/delete-record/temporaryTable";
            HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);
            HttpEntity<Object> requestEntity = new HttpEntity<>(updateTemporaryTableDto,headers);

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

    public ApiResponse getDetailsFromVc(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {

        	MainDto mainDTO = new MainDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            mainDTO = objectMapper.readValue(s, MainDto.class);
            //https://uaeid-stg.digitaltrusttech.com/uaeid-VerificationService/VerificationService/api/verify
            String url = verificationChannelBaseUrl + "/api/verify";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(mainDTO,headers);

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

    public ApiResponse getPreferredTitles(){
        ResponseEntity<ApiResponse> res = null;
        try {

            logger.info(CLASS+"getPreferredTitles req with time 1 {}", AppUtil.getDate());
            String url = baseUrl + "/api/get/preferredTitle";

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


    public ApiResponse addUpdateTitle(HttpHeaders httpHeaders, Object o) throws Exception {
        ResponseEntity<ApiResponse> res = null;
        try {
            TitleDto titleDto = new TitleDto();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            titleDto = objectMapper.readValue(s, TitleDto.class);

            String url = baseUrl + "/api/addUpdate/title";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(titleDto,headers);

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
