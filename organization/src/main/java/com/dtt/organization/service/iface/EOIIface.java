package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.RegisterOrganizationDTO;
import com.dtt.organization.dto.TrustedStakeholderDto;
import com.dtt.organization.dto.TrustedStakeholderRequestDto;

public interface EOIIface {

	ApiResponse registerTrustedOrganizationEOIPortal(RegisterOrganizationDTO registerOrganizationDTO, String referenceId);

    ApiResponse sendEmailOTP(String referenceId);

    ApiResponse registerTrustedOrganizationEOI(RegisterOrganizationDTO registerOrganizationDTO);
    
    ApiResponse addStakeHoldersList(TrustedStakeholderRequestDto trustedStakeholderRequestDto);
    
    ApiResponse getAllStakeHolder(String referredBy, String stakeholderType);
    
    ApiResponse getStakeHolder(String referenceId);
    
    ApiResponse sendEmailToSpoc(String spocEmail);
    
    ApiResponse updateStakeHolder(TrustedStakeholderDto trustedStakeHolder);
    
    ApiResponse getStakeHolderList(String spocEmail);
}
