package ug.daes.OnBoardingTransactionHandler.controller;


import com.dtt.common.util.ApiResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import ug.daes.OnBoardingTransactionHandler.dto.DataFramePostRequest;
import ug.daes.OnBoardingTransactionHandler.service.AppConfigService;
import ug.daes.OnBoardingTransactionHandler.service.DataFrameService;



@RestController
@CrossOrigin
public class DataFrameController{

    private final DataFrameService dataFrameService;

    @Lazy
    private final AppConfigService appConfigService;

    public DataFrameController(DataFrameService dataFrameService, AppConfigService appConfigService) {
        this.dataFrameService = dataFrameService;
        this.appConfigService = appConfigService;
    }

    @PostMapping("/api/post/onboarding/dataframe")
    public ApiResponse postResponse(@RequestHeader HttpHeaders httpHeaders, @RequestBody DataFramePostRequest dataFrame) throws Exception{
        return dataFrameService.ResponseServ(httpHeaders,dataFrame);
    }
    
    @GetMapping("/api/get/onboarding/dataframe")
    public ApiResponse getResponse(@RequestHeader HttpHeaders httpHeaders,@RequestParam String methodname) throws Exception{
    	DataFramePostRequest dataFrame = new DataFramePostRequest();
    	dataFrame.setServiceMethod(methodname);
    	
        return dataFrameService.ResponseServ(httpHeaders,dataFrame);
    }
    
    @GetMapping("/api/get/onboarding/dataframe-by-id")
    public ApiResponse getResponseByID(@RequestHeader HttpHeaders httpHeaders,@RequestParam int id ,@RequestParam String methodname) throws Exception{
    	DataFramePostRequest dataFrame = new DataFramePostRequest();
    	dataFrame.setServiceMethod(methodname);
        return dataFrameService.ResponseServByID(httpHeaders,dataFrame,id);
    }

    @GetMapping("/api/get/obth-status")
    public String getStatus() throws Exception{
        return "OBTH running";
    }

}
