package com.dtt.organization.restcontroller;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.*;
import com.dtt.organization.service.iface.OrgGatewayIface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class OrganizationGatewayController {

    public static final String CLASS = "OrganizationGatewayController";
    Logger logger = LoggerFactory.getLogger(OrganizationGatewayController.class);


    private final OrgGatewayIface orgGatewayIface;

    public OrganizationGatewayController(OrgGatewayIface orgGatewayIface) {
        this.orgGatewayIface = orgGatewayIface;
    }

    //1.get list of business users by orgId
    @GetMapping("/api/get/business/users/{orgId}")
    public ApiResponse getBusinessUsers(@PathVariable String orgId)
    {
     return orgGatewayIface.getBusinessUsers(orgId);
    }

    //4.get business users details by primary key id
    @GetMapping("/api/get/business/user/by/{id}")
    public ApiResponse getBusinessUserById(@PathVariable Integer id)
    {
        return orgGatewayIface.getBusinessUserById(id);
    }

    //5.update business user
    @PostMapping("/api/update/business/user")
    public ApiResponse updateBusinessUser(@RequestBody OrgUser orgUser){
        return orgGatewayIface.updateBusinessUser(orgUser);
    }


    @GetMapping("/api/get/eseal-logo/{orgUid}")
    public ApiResponse getEsealLogo(@PathVariable("orgUid") String orgUid){
        return orgGatewayIface.getEsealLogo(orgUid);
    }


    @PostMapping("/api/post/update/eseal-logo")
    public ApiResponse updateEsealLogo(@RequestBody UpdateEsealDto updateEsealDto){
        return orgGatewayIface.updateEsealLogo(updateEsealDto);
    }

    @PostMapping("/api/post/update/siganture-templates/by/id")
    public ApiResponse updateSignatureTemplatesById(@RequestBody SignatureTemplateUpdateDto signatureTemplateUpdateDto){
        return orgGatewayIface.updateSignatureTemplatesById(signatureTemplateUpdateDto);
    }

    @GetMapping("/api/get/signature-templates/by/id/{orgUid}")
    public ApiResponse getSignatureTemplatesById(@PathVariable("orgUid") String orgUid){
        return orgGatewayIface.getSignatureTemplateById(orgUid);
    }

    @PostMapping("/api/post/add-multiple-business-users")
    public ApiResponse addMultipleBusinessUsers(@RequestBody List<OrgUser> orgUsersList){

        return orgGatewayIface.addMultipleBusinessUsers(orgUsersList);
    }


    @PostMapping("/api/update/organisation/spocEmail")
    public ApiResponse updateOrganisationEGSpoc(@RequestBody EnterpriseGatewayOrganisationUpdateDto gatewayOrganisationUpdateDto){
        logger.info(CLASS+"updateOrganisation controller {}",gatewayOrganisationUpdateDto.getOrganizationUid());
        return orgGatewayIface.updateOrganisationEGSpoc(gatewayOrganisationUpdateDto);
    }
    @PostMapping("/api/update/organisation/agentUrl")
    public ApiResponse updateOrganisationEGAgent(@RequestBody EnterpriseGatewayOrganisationUpdateDto gatewayOrganisationUpdateDto){
        logger.info(CLASS+"updateOrganisation controller {}",gatewayOrganisationUpdateDto.getOrganizationUid());
        return orgGatewayIface.updateOrganisationEGAgent(gatewayOrganisationUpdateDto);
    }
    
    @GetMapping("/api/get/organizationCertificateDetails/{orgUid}")
    public ApiResponse getOrganizationCertificateDetailsByOrgUid(@PathVariable("orgUid") String orgUid) {
    	return orgGatewayIface.getOrganizationCertificateDetailsByOrgUid(orgUid);
    }


    @PostMapping("/api/delete/organisation/business-user/{orgId}/{email}")
    public ApiResponse deleteBusinessUser(@PathVariable String orgId, @PathVariable String email){
        return orgGatewayIface.deleteBusinessUser(orgId,email);
    }

    @PostMapping("/api/update/organisation/email-domain/by/ouid")
    public ApiResponse updateEmailDomain(@RequestBody UpdateEmailDomainDto updateEmailDomainDto) {
        return orgGatewayIface.updateEmailDomain(updateEmailDomainDto);
    }

    @GetMapping("/api/get/email-domain/by/ouid/{ouid}")
    public ApiResponse getEmailDomain(@PathVariable String ouid){
        return orgGatewayIface.getEmailDomain(ouid);
    }


}
