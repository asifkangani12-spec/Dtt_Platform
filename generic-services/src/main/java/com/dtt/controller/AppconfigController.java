package com.dtt.controller;

import java.util.Map;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dtt.model.GenericAppconfigModel;
import com.dtt.requestdto.AppconfigReqDTO;
import com.dtt.service.impl.AppServiceImpl;

@RestController
public class AppconfigController {

	private final AppServiceImpl configservice;

    public AppconfigController(AppServiceImpl configservice) {
        this.configservice = configservice;
    }

    @GetMapping("/api/service/status")
	public String hello() {
		return "AppConfig Service is Up and Running ..";
	}

	@PostMapping("/api/check/update")
	public ApiResponse updateconfig(@RequestBody AppconfigReqDTO dto) {
		return configservice.appUpdateUrl(dto);
	}

	@PostMapping("/api/save")
	public ApiResponse addconfig(@RequestBody GenericAppconfigModel dto) {
		return configservice.saveconfig(dto);
	}

	@GetMapping("/api/get/AppConfig")
	public ApiResponse getAllConfig() {
		return configservice.getAllConfig();
	}
	

    @GetMapping({"/api/endpoints/getall"})
    public ApiResponse getAllEndpoints() {
        Map<String, String> endpointsMap = configservice.getAllEndpointsAsMap();
        return AppUtil.createApiResponse(true, "Successfully received all endpoints", endpointsMap);
    }
}

