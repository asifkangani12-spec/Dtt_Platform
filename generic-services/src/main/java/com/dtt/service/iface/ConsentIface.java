package com.dtt.service.iface;

import com.dtt.common.util.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import com.dtt.requestdto.ConsentDTO;

public interface ConsentIface {
	
	public ApiResponse addFiles(ConsentDTO consentDTO, MultipartFile termsAndConditions, MultipartFile dataPrivacy);


}
