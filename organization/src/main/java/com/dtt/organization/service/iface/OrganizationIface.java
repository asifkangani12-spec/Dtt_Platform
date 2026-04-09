package com.dtt.organization.service.iface;



import com.dtt.common.exception.OrgnizationServiceException;
import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.*;
import com.dtt.organization.request.entity.SignatureVerificationContext1;

import java.util.List;

public interface OrganizationIface {

	ApiResponse registerOrganization(RegisterOrganizationDTO registerOrganizationDTO);
	
	ApiResponse updateOrganization(RegisterOrganizationDTO updateOrganizationDTO);
	
	ApiResponse getOrganizationDetailsById(String organizationUid);
	
	ApiResponse getOrgDetailsByOrganizationName(String organizationName);
	
	ApiResponse getOrganizationBySerachType(String organizationName);
	
	ApiResponse isOrganizationAlreadyExixts(String organizationName);
	
	ApiResponse addOrganizationEmails(EmailListDto emailList);
	
	ApiResponse getSubascriberEmailBySearchType(String searchType);
	
	
	ApiResponse getOrganizationListAndUid(String suid);
		
	ApiResponse getOrganizationPrepetryStatus(String emailId);
	
	ApiResponse getOrganizationPrepetryStatusByOrgId(String orgId);
	
	ApiResponse getAgentUrlByOrg(String orgId);
	
	ApiResponse getOrgListAndUid();

	ApiResponse getOrganizationListForSearch();
	
	ApiResponse getSigntoryList(String organizationUid);
	
	ApiResponse getSigntoryListByOrgId(String organizationUid);

	ApiResponse verifySignedDocumnet(SignatureVerificationContext1 signatureVerificationContext1);
	
	ApiResponse getAllTemplates();
	ApiResponse getAllTemplatesWithoutImages();
	ApiResponse getTemplateImage(int id);
	
	SignatureTemplateDto getTemplatesByTempId(GetTemplateDto getTemplateDto) throws OrgnizationServiceException;
	
	ApiResponse getUserTemplateDetails(GetTemplateDto getTemplateDto);
	ApiResponse getOrganizationListBySuid(String suid);

	ApiResponse linkEmail(OrgUser orgUser);

	ApiResponse sendOtp(EmailReqDto otpDto);

	ApiResponse deactive(DeactivateDto deactivateDto);

	ApiResponse orgStatus(String orgUid);
	
	ApiResponse getCertificateDetails(String orgUid);
	
	ApiResponse getCertificateStatusList(OrganizationIdDto orgUidList);

	ApiResponse checkValidCertificateSerialNumber(String certificateSerialNumber);


	ApiResponse sendEmail(String spocEmail, String orgName);


	ApiResponse toggleManageByAdmin(ToggleManageByAdmin toggleManageByAdmin);


	ApiResponse checkTemplates(List<CheckTemplateDto> checkTemplateDtoList);


	ApiResponse getOrganizationStatus();

	ApiResponse getOrgListByOuid();

	ApiResponse getAllCategories();

	ApiResponse updateCategoryLabel(Integer id, String labelName);
}
