package com.dtt.controller;


import com.dtt.common.util.ApiResponse;
import com.dtt.enums.IdentifierType;
import com.dtt.enums.ResponseType;
import com.dtt.service.iface.ProvisioningServiceIface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProvisioningServiceController {

    private final ProvisioningServiceIface provisioningServiceIface;

    public ProvisioningServiceController(ProvisioningServiceIface provisioningServiceIface) {
        this.provisioningServiceIface = provisioningServiceIface;
    }

    @GetMapping("/api/get/details/{identifierType}/{responseType}/{identifierValue}")
    public ApiResponse getProfileDetailsForProvisioning(

            @PathVariable IdentifierType identifierType,
            @PathVariable ResponseType responseType,
            @PathVariable String identifierValue
    ) {
        return provisioningServiceIface.getProfileDetailsForProvisioning(
                identifierType,
                responseType,
                identifierValue
        );
    }


    @GetMapping("/api/get/user/profile/by/suid/{suid}")
    public ApiResponse getUserProfileBySuid(@PathVariable String suid){
        return provisioningServiceIface.getUserProfileDetailsBySuid(suid);

    }

    @GetMapping("/api/get/uaeid/auth/profile/by/suid/{suid}")
    public ApiResponse getAuthProfileBySuid(@PathVariable String suid){
        return provisioningServiceIface.getUAEIDAuthenticatinProfileBySuid(suid);
    }

    @GetMapping("/api/get/uaeid/visa/credentials/by/passportNumber/{passportNumber}")
    public ApiResponse getVisaCredentialsByPassportNo(@PathVariable String passportNumber){
        return provisioningServiceIface.getVisaCredentialsByPassportNo(passportNumber);
    }



}
