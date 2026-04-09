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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;
import ug.daes.OnBoardingTransactionHandler.dto.template.RecordDto;

import ug.daes.OnBoardingTransactionHandler.util.HeaderUtil;
import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raxit Dubey
 *
 */
@Service
public class DeleteService {
	
	private static Logger logger = LoggerFactory.getLogger(DeleteService.class);

	/** The Constant CLASS. */
	static final String CLASS = "DeleteService";

	private final PropertiesUtil propertiesUtil;

	String baseUrl;

	private final TxHandlerPropertiesConfiguration config;

	@PostConstruct
	public void init() {
		this.baseUrl = config.getOnboarding();
	}

	private final ObjectMapper objectMapper;

    private  final ExceptionHandlerUtil exceptionHandlerUtil;

	public DeleteService(PropertiesUtil propertiesUtil, TxHandlerPropertiesConfiguration config, ObjectMapper objectMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.propertiesUtil = propertiesUtil;
		this.config = config;
		this.objectMapper = objectMapper;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public ApiResponse deleteRecordBySuid(HttpHeaders httpHeaders, Object o) throws Exception {
		ResponseEntity<ApiResponse> res = null;
		try {
			RecordDto recordDto = new RecordDto();
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			recordDto = objectMapper.readValue(s, RecordDto.class);
			
			String url = baseUrl + "/api/get/delete-record?suid=" + recordDto.getSuid();
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = HeaderUtil.createHeaders(httpHeaders);

			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = rest.exchange(url, HttpMethod.DELETE, requestEntity, ApiResponse.class);
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

}
