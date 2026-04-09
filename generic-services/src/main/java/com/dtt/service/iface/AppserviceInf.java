package com.dtt.service.iface;

import java.util.Map;

import com.dtt.common.util.ApiResponse;
import com.dtt.model.GenericAppconfigModel;
import com.dtt.requestdto.AppconfigReqDTO;

public interface AppserviceInf {
	
	ApiResponse appUpdateUrl(AppconfigReqDTO appconfigReqDTO);

	ApiResponse saveconfig(GenericAppconfigModel paramAppconfigModel);

	ApiResponse getAllConfig();
	
	Map<String, String> getAllEndpointsAsMap();

}
