package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.BenificiariesDto;

import java.util.List;


public interface BeneficiaryIface {


    ApiResponse verifyByEgpForVendor(String vendorId, String orgid);

    ApiResponse addBeneficiary(BenificiariesDto benificiariesDto);

    ApiResponse findPrivilegeByStatus();

    ApiResponse getAllBeneficiaries();

    ApiResponse getAllBeneficiariesBySponsor(String sponsorId);

    ApiResponse getBeneficiaryById(int id);

    ApiResponse updateBeneficiary(BenificiariesDto benificiariesDto);

    ApiResponse dlink(int id);

    ApiResponse verifyOnBoardingSponsor(String suid);

    ApiResponse linkSponsor(String beneficiaryDigitalId, int id);
    
    ApiResponse linkAllSponsor(BenificiariesDto benificiariesDto);

    ApiResponse getAllSponsersBySuid(String suid);

    ApiResponse changeStatusForSSP(int id);

    ApiResponse addMultipleBeneficiaries(List<BenificiariesDto> multipleBenificiariesDto);

    ApiResponse getVendorsByVendorId(String vendorId);


}



