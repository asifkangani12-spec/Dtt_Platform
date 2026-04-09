package com.dtt.organization.restcontroller;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.RegisterOrganizationDTO;
import com.dtt.organization.dto.TrustedStakeholderDto;
import com.dtt.organization.dto.TrustedStakeholderRequestDto;
import com.dtt.organization.service.iface.EOIIface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class EOIController {
    public static final String CLASS = "EOIController";
    Logger logger = LoggerFactory.getLogger(EOIController.class);

    private final EOIIface eoiIface;

    // Constructor Injection
    public EOIController(EOIIface eoiIface) {
        this.eoiIface = eoiIface;
    }

    @PostMapping("/api/post/service/register/organization/eoi/{referenceId}")
    public ApiResponse registerOrganizationEOIPortal(@RequestBody RegisterOrganizationDTO registerOrganizationDTO, @PathVariable String referenceId) {
        logger.info(CLASS + "registerOrganization orgname {}",registerOrganizationDTO.getOrganizationName());
        logger.info(CLASS + "registerOrganization DTO request {}", registerOrganizationDTO);
        return eoiIface.registerTrustedOrganizationEOIPortal(registerOrganizationDTO,referenceId);

    }

   //Register the organization without reference id
	@PostMapping("/api/post/service/register/organization/eoi")
	public ApiResponse onboardOrganization(@RequestBody RegisterOrganizationDTO registerOrganizationDTO) {
		return eoiIface.registerTrustedOrganizationEOI(registerOrganizationDTO);
	}
	
	//API for add all stake holder from admin portal(import)
	@PostMapping("/api/post/addstakeholderlist")
	public ApiResponse addStakeHolders(@RequestBody TrustedStakeholderRequestDto trustedStakeholderRequestDto) {
	    return eoiIface.addStakeHoldersList(trustedStakeholderRequestDto);
    }
	
	//update record of stakeholder
	@PostMapping("/api/update/stakeholder")
    public ApiResponse updateStakeHolder(@RequestBody TrustedStakeholderDto trustedStakeHolder) {
    	return eoiIface.updateStakeHolder(trustedStakeHolder);
    }
	

	@GetMapping("/api/get/stakeholder/{referenceId}")
	public ApiResponse fetchStakeHolder(@PathVariable String referenceId) {
	    return eoiIface.getStakeHolder(referenceId);
	}
	
	//get All stakeholder list for eoi
	@GetMapping(value = "/api/get/allstakeholder")
    public ApiResponse getAllStakeHolder(@RequestParam String referredBy, @RequestParam String stakeholderType) {
    	return eoiIface.getAllStakeHolder(referredBy,stakeholderType);
    }
    
    //get stakeholder list for eoi
    @GetMapping("/api/get-stakeholders-list/{spocEmail}")
    public ApiResponse getStakeHoldersListEOI(@PathVariable String spocEmail) {
    	return eoiIface.getStakeHolderList(spocEmail);
    }
    
    //send email otp to spoc
    @PostMapping("/api/post/sendemailotp/{referenceId}")
    public ApiResponse sendEmailOTP(@PathVariable String referenceId) {
        return eoiIface.sendEmailOTP(referenceId);
    }
    
    //send invitation link to spoc
    @PostMapping("/api/initationlink/spoc/{email}")
    public ApiResponse invitationLinkToSpoc(@PathVariable("email") String spocemail) {
        return eoiIface.sendEmailToSpoc(spocemail);
    }
}
