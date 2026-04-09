package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.SoftwareLicensesDTO;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface LicensesIface {
	
	ApiResponse applyForGenerateLicenses(SoftwareLicensesDTO softwareLicensesDTO, HttpHeaders httpHeaders);

	ApiResponse downloadLicense(String ouid, String type);

	ApiResponse getLicenseByOuid(String ouid);

	ApiResponse getLicenseByOuidVG(String ouid);
	
	ApiResponse getListForGenerateLicense();
	
	ApiResponse sendEmailToAdmin(SoftwareLicensesDTO softwareLicensesDTO);
	
	ApiResponse getAdminEmailList();
	
	ApiResponse addDeviceIdOfLicense(String applicationName, List<String> deviceID);
	
	ApiResponse updateDeviceIdOfLicense(String applicationName, String olddeviceID, String newdeviceID);
	
	ApiResponse getDeviceID(String clientId);
	
	ApiResponse getDeviceIdDetails(String applicationName);
	
	ApiResponse deleteRecordByDeviceID(String deviceId, String applicationName);
}
