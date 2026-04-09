package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.*;

import java.util.List;

public interface OrgGatewayIface {
    ApiResponse getBusinessUsers(String orgId);

    ApiResponse getBusinessUserById(Integer id);

    ApiResponse updateBusinessUser(OrgUser orgUser);

    ApiResponse getEsealLogo(String orgUid);

    ApiResponse updateEsealLogo(UpdateEsealDto updateEsealDto);

    ApiResponse updateSignatureTemplatesById(SignatureTemplateUpdateDto signatureTemplateUpdateDto);

    ApiResponse getSignatureTemplateById(String orgUid);

    ApiResponse addMultipleBusinessUsers(List<OrgUser> orgUsersList);


    ApiResponse updateOrganisationEGSpoc(EnterpriseGatewayOrganisationUpdateDto gatewayOrganisationUpdateDto);
    ApiResponse updateOrganisationEGAgent(EnterpriseGatewayOrganisationUpdateDto gatewayOrganisationUpdateDto);
    
    ApiResponse getOrganizationCertificateDetailsByOrgUid(String orgUid);


    ApiResponse deleteBusinessUser(String orgId, String email);

    ApiResponse updateEmailDomain(UpdateEmailDomainDto updateEmailDomainDto);

    ApiResponse getEmailDomain(String ouid);
}
