package com.dtt.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.model.GenericApiEndpoint;
import com.dtt.model.GenericAppconfigModel;
import com.dtt.repo.ApiEndpointRepository;
import com.dtt.repo.AppConfigRepo;
import com.dtt.requestdto.AppconfigReqDTO;
import com.dtt.responsedto.AppconfigResDTO;
import com.dtt.service.iface.AppserviceInf;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class AppServiceImpl implements AppserviceInf {


	private final AppConfigRepo appconfigRepository;
	private final ApiEndpointRepository apiEndpointRepository;
	private  final ExceptionHandlerUtil exceptionHandlerUtil;

	Logger logger= LoggerFactory.getLogger(AppServiceImpl.class);


	public  AppServiceImpl(AppConfigRepo appconfigRepository,
						  ApiEndpointRepository apiEndpointRepository,
						  ExceptionHandlerUtil exceptionHandlerUtil) {

		this.appconfigRepository = appconfigRepository;
		this.apiEndpointRepository = apiEndpointRepository;
		this.exceptionHandlerUtil = exceptionHandlerUtil;  // ✔ correct assignment
	}


    @Override
	public ApiResponse appUpdateUrl(AppconfigReqDTO appconfigDTO) {
		try {
			if (appconfigDTO.getOsVersion() == null && appconfigDTO.getAppVersion() == null) {
				return exceptionHandlerUtil.createErrorResponse("api.response.invalid.input");
			}

			GenericAppconfigModel appConfigModel = appconfigRepository.getAppConfig(appconfigDTO.getOsVersion());
			if (appConfigModel == null) {
				return exceptionHandlerUtil.createErrorResponse("api.response.config.not.found");
			}

			// Check update conditions
			if (compareVersions(appconfigDTO.getAppVersion(), appConfigModel.getMinimumVersion()) < 0) {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.update.required",
						new AppconfigResDTO(appConfigModel.getLatestVersion(), false, true, appConfigModel.getUpdateLink())
				);
			} else if (compareVersions(appconfigDTO.getAppVersion(), appConfigModel.getLatestVersion()) < 0) {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.update.recommended",
						new AppconfigResDTO(appConfigModel.getLatestVersion(), true, false, appConfigModel.getUpdateLink())
				);
			} else {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.no.update.required",
						new AppconfigResDTO(appConfigModel.getLatestVersion(), false, false, null)
				);
			}

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			logger.error("Exception occurred:", ex);

			return exceptionHandlerUtil.handleException(ex);
		} catch (Exception e) {
			return exceptionHandlerUtil.handleException(e);
		}
	}

	// Method to compare version strings using Stream API
	public static int compareVersions(String version1, String version2) {
		String[] v1 = version1.split("\\.");
		String[] v2 = version2.split("\\.");

		int maxLength = Math.max(v1.length, v2.length);

		return IntStream.range(0, maxLength)
				.map(i -> Integer.compare(
						i < v1.length ? Integer.parseInt(v1[i]) : 0,
						i < v2.length ? Integer.parseInt(v2[i]) : 0
				))
				.filter(result -> result != 0)
				.findFirst()
				.orElse(0);
	}

	@Override
	public ApiResponse saveconfig(GenericAppconfigModel dto) {
		try {
			System.out.println(dto);
			if (dto == null) {
				return exceptionHandlerUtil.createErrorResponse("api.response.dto.null");
			}

			if (dto.getLatestVersion() == null || dto.getLatestVersion().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse("api.response.latest.version.empty");
			}
			if (dto.getMinimumVersion() == null || dto.getMinimumVersion().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse("api.response.min.version.empty");
			}
			if (dto.getOsVersion() == null || dto.getOsVersion().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse("api.response.os.version.empty");
			}

			GenericAppconfigModel modelToSave;

			if (dto.getId() > 0) {
				Optional<GenericAppconfigModel> existingRecord = appconfigRepository.findById(dto.getId());
				if (existingRecord.isPresent()) {
					GenericAppconfigModel existing = existingRecord.get();
					existing.setLatestVersion(dto.getLatestVersion());
					existing.setMinimumVersion(dto.getMinimumVersion());
					existing.setUpdateLink(dto.getUpdateLink());
					existing.setOsVersion(dto.getOsVersion());
					modelToSave = existing;

				}
				else{
					return exceptionHandlerUtil.createErrorResponse("api.error.id.not.found",null);
				}
			}

			else{
				modelToSave = dto;
			}
			appconfigRepository.save(modelToSave);

			return exceptionHandlerUtil.createSuccessResponse("api.response.config.saved.successfully", null);

		} catch (Exception ex) {
			return exceptionHandlerUtil.handleException(ex);
		}
	}

	@Override
	public ApiResponse getAllConfig() {
		try {
			List<GenericAppconfigModel> models = appconfigRepository.findAll();
			if (!models.isEmpty()) {
				return exceptionHandlerUtil.createSuccessResponse("api.response.config.list.found", models);
			}
			return exceptionHandlerUtil.createErrorResponse("api.response.no.records.found");

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return exceptionHandlerUtil.handleException(ex);
		} catch (Exception e) {
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public Map<String, String> getAllEndpointsAsMap() {
		List<GenericApiEndpoint> endpoints = apiEndpointRepository.findAll();
		Map<String, String> resultMap = new HashMap<>();

		for (GenericApiEndpoint endpoint : endpoints) {
			resultMap.put(endpoint.getKeyName(), endpoint.getValue());
		}
		return resultMap;
	}
}
