package com.dtt.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.enums.IdentifierType;
import com.dtt.enums.ResponseType;

public interface ProvisioningServiceIface {

   public ApiResponse getProfileDetailsForProvisioning(IdentifierType identifierType, ResponseType responseType, String identifierValue);

   public ApiResponse getUserProfileDetailsBySuid(String suid);

   public ApiResponse getUAEIDAuthenticatinProfileBySuid(String suid);


   ApiResponse getVisaCredentialsByPassportNo(String passportNumber);
}
