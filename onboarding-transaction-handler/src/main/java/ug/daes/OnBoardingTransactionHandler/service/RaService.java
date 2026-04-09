package ug.daes.OnBoardingTransactionHandler.service;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.ra.*;

import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raxit Dubey
 *
 */
@Service
public class RaService {

    private final RestTemplate restTemplate;


	private static Logger logger = LoggerFactory.getLogger(RaService.class);

	/** The Constant CLASS. */
	static final String CLASS = "RaService";

	private final PropertiesUtil propertiesUtil;

	String baseRaSigningUrl;

	String baseRaAuthorityUrl;

	private final TxHandlerPropertiesConfiguration config;

	private String baseUrl;

	@PostConstruct
	public void init() {
		this.baseRaSigningUrl = config.getSigning();
		this.baseRaAuthorityUrl = config.getRaauthority();
	}

	private final ObjectMapper objectMapper;

	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public RaService(RestTemplate restTemplate, PropertiesUtil propertiesUtil, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.restTemplate = restTemplate;
		this.propertiesUtil = propertiesUtil;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	/**
//	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ApiResponse getCertificateDetailsBySubscriberUniqueId(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			RevokeCertDTO revokeCertDTO = new RevokeCertDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			revokeCertDTO = objectMapper.readValue(s, RevokeCertDTO.class);

			String url = baseRaAuthorityUrl + "/api/get/service/certificate/details/by-subscriber-unique-id/"
					+ revokeCertDTO.getSubscriberUniqueId();
			System.out.println("url ==> " + url);

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

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

	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse revokeCertificate(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			RevokeCertDTO revokeCertDTO = new RevokeCertDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			revokeCertDTO = objectMapper.readValue(s, RevokeCertDTO.class);

			String url = baseRaAuthorityUrl + "/api/post/service/certificate/revoke";
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(revokeCertDTO, headers);

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

	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse setPin(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			SignatureServiceRequestDTO signatureServiceRequestDTO = new SignatureServiceRequestDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			signatureServiceRequestDTO = objectMapper.readValue(s, SignatureServiceRequestDTO.class);

			String url = baseRaAuthorityUrl + "/api/post/service/certificate/set-pin";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(signatureServiceRequestDTO, headers);

			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
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


	/**
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public ApiResponse setPins(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			SetPinModelDto setPinModelDto = new SetPinModelDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			setPinModelDto = objectMapper.readValue(s, SetPinModelDto.class);

			String url = baseRaAuthorityUrl + "/api/post/certificate/set-pins";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(setPinModelDto, headers);

			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);

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


	public ApiResponse setPinFromSMA(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			SetPinSMADTO setPinSMA = new SetPinSMADTO();
			SetPinFromSMA setPinFromSMA = new SetPinFromSMA();

			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			setPinSMA = objectMapper.readValue(s, SetPinSMADTO.class);

			setPinFromSMA.setSigningPin(setPinSMA.getSignPin());
			setPinFromSMA.setAuthPin(setPinSMA.getAuthPin());
			setPinFromSMA.setSubscriberUniqueId(setPinSMA.getSuid());
			setPinFromSMA.setSignHash(setPinSMA.isSignHash());
			setPinFromSMA.setSignType(setPinSMA.getSignType());
			setPinFromSMA.setUserSelfie(setPinSMA.getUserSelfie());

			String url = baseRaSigningUrl + "/api/digital/signature/post/setPinFromSMA";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(setPinFromSMA, headers);

			res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
			return exceptionHandlerUtil.handleResponse(res);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleHttpException(e);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			return exceptionHandlerUtil.handleResourceAccessException(e);
		} catch (Exception e) {
			return ExceptionHandlerUtil.handleGenericException(e);
		}
	}

	
	public ApiResponse cancelSetPinFromSMA(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			SetPinSMADTO setPinSMA = new SetPinSMADTO();
			SetPinFromSMA setPinFromSMA = new SetPinFromSMA();

			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			setPinSMA = objectMapper.readValue(s, SetPinSMADTO.class);

			setPinFromSMA.setSubscriberUniqueId(setPinSMA.getSuid());
			setPinFromSMA.setSignHash(setPinSMA.isSignHash());

			String url = baseRaSigningUrl + "/api/digital/signature/post/cancelSetPinFromSMA";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(setPinFromSMA, headers);

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

	public ApiResponse verifyCertPin(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			VerifyCertPinDTO verifyCertPinDTO = new VerifyCertPinDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			verifyCertPinDTO = objectMapper.readValue(s, VerifyCertPinDTO.class);

			String url = baseRaAuthorityUrl + "/api/post/service/certificate/verify-certificate-pin";

			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(verifyCertPinDTO, headers);

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

	public ApiResponse genrateFailedCertificate(HttpHeaders httpHeaders,Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			IssueCertDTO issueCertDTO = new IssueCertDTO();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			issueCertDTO = objectMapper.readValue(s, IssueCertDTO.class);

			String url = baseRaAuthorityUrl + "/api/post/service/certificate/request";
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(issueCertDTO, headers);

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
