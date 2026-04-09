package com.dtt.organization.service.impl;


import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.common.util.Utility;
import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.constant.Constant;
import com.dtt.organization.dto.BizAppCreateDto;
import com.dtt.organization.dto.EmailDto;
import com.dtt.organization.dto.SoftwareLicensesDTO;
import com.dtt.organization.model.*;
import com.dtt.organization.repository.*;
import com.dtt.organization.service.iface.LicensesIface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ug.daes.DAESService;
import ug.daes.Result;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.dtt.organization.constant.Constant.*;

@Service
public class LicensesImpl implements LicensesIface {

	@Value(value = "${apply.forgenerate.licenses}")
	private boolean generateLicensesAdmin;

	private final OrgSoftwareLicensesRepository softwareLicensesRepository;

	private final OrganizationDetailsForClientRepoIface organizationDetailsForClientRepoIface;
	private final OrgSoftwareLicenseApprovalRequestsRepo softwareLicenseApprovalRequestsRepo;
	private final OrganizationDetailsRepository organizationDetailsRepository;
	private final OrgSubscriberRepository subscriberRepository;
	private final OrgLicenseDeviceListRepo licenseDeviceListRepo;
	private final RestTemplate restTemplate;
	private final ExceptionHandlerUtil exceptionHandlerUtil;

	private final OrgSoftwareLicensesHistoryRepo softwareLicensesHistoryRepo;
	private final JavaMailSender mailSender;
	private final OrgSubscriberPreferencesRepo subscriberPreferencesRepo;

	public LicensesImpl(
			OrgSoftwareLicensesRepository softwareLicensesRepository,
			OrgSoftwareLicensesHistoryRepo softwareLicensesHistoryRepo,
			OrganizationDetailsForClientRepoIface organizationDetailsForClientRepoIface,
			OrgSoftwareLicenseApprovalRequestsRepo softwareLicenseApprovalRequestsRepo,
			OrganizationDetailsRepository organizationDetailsRepository,
			OrgSubscriberRepository subscriberRepository,
			OrgLicenseDeviceListRepo licenseDeviceListRepo,
			RestTemplate restTemplate,
			ExceptionHandlerUtil exceptionHandlerUtil,
			JavaMailSender mailSender,
			OrgSubscriberPreferencesRepo subscriberPreferencesRepo) {

		this.softwareLicensesRepository = softwareLicensesRepository;
		this. softwareLicensesHistoryRepo=softwareLicensesHistoryRepo;
		this.organizationDetailsForClientRepoIface = organizationDetailsForClientRepoIface;
		this.softwareLicenseApprovalRequestsRepo = softwareLicenseApprovalRequestsRepo;
		this.organizationDetailsRepository = organizationDetailsRepository;
		this.subscriberRepository = subscriberRepository;
		this.licenseDeviceListRepo = licenseDeviceListRepo;
		this.restTemplate = restTemplate;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
		this.mailSender = mailSender;
		this.subscriberPreferencesRepo = subscriberPreferencesRepo;
	}

	@Value(value = "${url.admin.emaillist}")
	private String url;

	@Value(value = "${max.adminEmails}")
	private int noOfAdminEmail;

	@Value(value = "${privatekey.for.license}")
	private String privatekey;

	@Value(value = "${send.email.url}")
	private String sendEmail;

	@Value(value = "${send.email.adminURL}")
	private String sendEmailAdmin;

	@Value("${save.client}")
	String saveClient;

	@Value("${application.uri}")
	String applicationUri;

	@Value("${logout.uri}")
	String logoutUri;

	@Value("${redirect.uri}")
	String redirectUri;

	@Value("${file.crt}")
	String crtFile;

	@Value("${icp.env}")
	boolean icpEnv;

	@Value("${spring.mail.username}")
	private String senderEmail;


	private static final String CLASS = LicensesImpl.class.getSimpleName();
	Logger logger = LoggerFactory.getLogger(LicensesImpl.class);

	private static final String COMMERCIAL ="COMMERCIAL";
	private static final String AUTHORIZATION_CODE = "authorization_code";
	private static final String  API_ERROR_LICENSE_ACTIVE ="api.error.license.active";
	private static final String  API_RESPONSE_LICENSE_GENERATED = "api.response.license.generated";
	private static final String ACTIVE = "ACTIVE";
	private static final String APPLIED ="APPLIED";





	@Override
	public ApiResponse applyForGenerateLicenses(SoftwareLicensesDTO softwareLicensesDTO, HttpHeaders httpHeaders) {
		try {
			if (softwareLicensesDTO.getOuid() != null) {
				String validUptoDate = null;
				String issuedOn = null;
				String licenseBase64 = null;


				OrgSoftwareLicenses softwareLicenses;
				String softwareName;

				OrgSoftwareLicenseApprovalRequests softwareLicenseApprovalRequests;

				softwareLicenses = softwareLicensesRepository.findByOuidAndLicenseType(softwareLicensesDTO.getOuid(),
						COMMERCIAL);

				if(generateLicensesAdmin){
					 softwareName = "ENTERPRISE_GATEWAY";
				}
				else{
					String[] result = softwareLicensesDTO.getApplicationType().split("_");
					String lastRecord = result[result.length - 1];
					String[] applicationName = softwareLicensesDTO.getApplicationType().split("_" + lastRecord);
					 softwareName = applicationName[0];
				}

				softwareLicenseApprovalRequests = softwareLicenseApprovalRequestsRepo.getSoftwareDetails(
						softwareLicensesDTO.getOuid(),COMMERCIAL ,softwareName);

				OrgOrganizationDetails organizationDetails = organizationDetailsRepository
						.findByOrganizationUid(softwareLicensesDTO.getOuid());

					LocalDate currentDate = LocalDate.now();
					LocalDate dateAfter30Days = currentDate.plusDays(365);
					validUptoDate = dateAfter30Days.toString();
					issuedOn = currentDate.toString();


				if (generateLicensesAdmin) {

					if (softwareLicenses == null) {

                    BizAppCreateDto bizAppCreateDto = new BizAppCreateDto();


                    String adminApplicationName = "ENTERPRISE_GATEWAY_"+organizationDetails.getOrganizationId();



                    String arabicAppName = convertToArabicName(adminApplicationName);

                    bizAppCreateDto.setApplicationName(adminApplicationName);
                    bizAppCreateDto.setappilicationNameArabic(arabicAppName);
                    bizAppCreateDto.setApplicationType("Regular Web Application");
                    bizAppCreateDto.setApplicationUri(applicationUri + organizationDetails.getOrganizationId());
                    bizAppCreateDto.setGrantTypes(AUTHORIZATION_CODE);
                    bizAppCreateDto.setLogoutUri(logoutUri + organizationDetails.getOrganizationId());
                    bizAppCreateDto.setOrganizationId(organizationDetails.getOrganizationUid());
                    bizAppCreateDto.setRedirectUri(redirectUri + organizationDetails.getOrganizationId());
                    bizAppCreateDto.setScopes("openid urn:idp:digitalid:profile");
                    bizAppCreateDto.setBase64Cert(crtFile);


                    List<String> grantList = Arrays.asList(AUTHORIZATION_CODE, "authorization_code_with_pkce");
                    bizAppCreateDto.setGrantTypesList(grantList);

                    List<String> scopesList = Arrays.asList("openid", "urn:idp:digitalid:profile");
                    bizAppCreateDto.setScopesList(scopesList);

                    bizAppCreateDto.setAuthSchemaId("0");

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Object> reqEntity = new HttpEntity<>(bizAppCreateDto, headers);

                    ResponseEntity<ApiResponse> response = restTemplate.exchange(
                            saveClient,
                            HttpMethod.POST,
                            reqEntity,
                            ApiResponse.class
                    );

                    ApiResponse res = response.getBody();

					if (res == null) {
						return AppUtil.createApiResponse(false, "Empty response from service", null);
					}

                    if (!res.isSuccess()) {
                        return AppUtil.createApiResponse(false, res.getMessage(), null);
                    }


					licenseBase64 = generateLicenses(softwareLicensesDTO,
							softwareLicensesDTO.getLicenseType());

						OrgSoftwareLicenses softwareLicensesmodel = new OrgSoftwareLicenses();
						OrgSoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel = new OrgSoftwareLicenseApprovalRequests();
						softwareLicensesmodel.setAppid(softwareName);
						softwareLicensesmodel.setCreatedDateTime(AppUtil.getDate());
						softwareLicensesmodel.setIssuedOn(issuedOn);
						softwareLicensesmodel.setUpdatedDateTime(AppUtil.getDate());
						softwareLicensesmodel.setValidUpTo(validUptoDate);
						softwareLicensesmodel.setOuid(softwareLicensesDTO.getOuid());
						softwareLicensesmodel.setLicenseType(COMMERCIAL);
						softwareLicensesmodel.setLicenceStatus(ACTIVE);
						softwareLicensesmodel.setLicenseInfo(licenseBase64);
						softwareLicensesmodel.setApplicationName(adminApplicationName);
						softwareLicensesmodel.setOrganizationName(organizationDetails.getOrganizationName());

						softwareLicenseApprovalRequestsmodel.setApprovalStatus(ACTIVE);
						softwareLicenseApprovalRequestsmodel.setLicenseType(COMMERCIAL);
						softwareLicenseApprovalRequestsmodel.setUpdatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequestsmodel.setOuid(softwareLicensesDTO.getOuid());
						softwareLicenseApprovalRequestsmodel.setCreatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequestsmodel.setAppid(softwareName);

						softwareLicensesRepository.save(softwareLicensesmodel);
						softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);
						if(icpEnv){
							sendEmailICPLicenseGenerated(organizationDetails.getSpocUgpassEmail(),organizationDetails.getOrganizationName());
						}
						else {
							sendEmail(softwareLicensesDTO);
						}
						return exceptionHandlerUtil.createSuccessResponse( API_RESPONSE_LICENSE_GENERATED,
								softwareLicensesmodel);

					} else {
						OrgSoftwareLicensesHistory softwareLicensesHistory = new OrgSoftwareLicensesHistory();

						String todayDate = LocalDate.now().toString();
						String year = todayDate.substring(0, 4);
						String month = todayDate.substring(5, 7);
						String date = todayDate.substring(8, 10);
						String validDate = softwareLicenses.getValidUpTo();
						String yearValid = validDate.substring(0, 4);
						String monthValid = validDate.substring(5, 7);
						String dateValid = validDate.substring(8, 10);

						if (Integer.parseInt(yearValid) > Integer.parseInt(year) ||
										(Integer.parseInt(yearValid) == Integer.parseInt(year) &&
												Integer.parseInt(monthValid) > Integer.parseInt(month)) ||
										(Integer.parseInt(yearValid) == Integer.parseInt(year) &&
												Integer.parseInt(monthValid) == Integer.parseInt(month) &&
												Integer.parseInt(dateValid) > Integer.parseInt(date))
						) {
							return exceptionHandlerUtil.createSuccessResponse(
									API_ERROR_LICENSE_ACTIVE, null);
						}

						softwareLicensesHistory.setAppid(softwareLicenses.getAppid());
						softwareLicensesHistory.setOuid(softwareLicenses.getOuid());
						softwareLicensesHistory.setLicenseType(softwareLicenses.getLicenseType());
						softwareLicensesHistory.setCreatedDateTime(softwareLicenses.getCreatedDateTime());
						softwareLicensesHistory.setIssuedOn(softwareLicenses.getIssuedOn());
						softwareLicensesHistory.setUpdatedDateTime(softwareLicenses.getUpdatedDateTime());
						softwareLicensesHistory.setValidUpto(softwareLicenses.getValidUpTo());

						softwareLicenses.setAppid(softwareName);
						softwareLicenses.setCreatedDateTime(AppUtil.getDate());
						softwareLicenses.setIssuedOn(issuedOn);
						softwareLicenses.setUpdatedDateTime(AppUtil.getDate());
						softwareLicenses.setValidUpTo(validUptoDate);
						softwareLicenses.setOuid(softwareLicensesDTO.getOuid());
						softwareLicenses.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenses.setLicenceStatus(ACTIVE);
						softwareLicenses.setApplicationName(softwareLicensesDTO.getApplicationType());
						softwareLicenses.setOrganizationName(organizationDetails.getOrganizationName());

						softwareLicenses.setLicenseInfo(licenseBase64);

						softwareLicenseApprovalRequests.setApprovalStatus(ACTIVE);
						softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenseApprovalRequests.setUpdatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequests.setOuid(softwareLicensesDTO.getOuid());
						softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenseApprovalRequests.setCreatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequests.setAppid(softwareName);

						softwareLicensesHistoryRepo.save(softwareLicensesHistory);
						softwareLicensesRepository.save(softwareLicenses);

						softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequests);
						if(icpEnv){
							sendEmailICPLicenseGenerated(organizationDetails.getSpocUgpassEmail(),organizationDetails.getOrganizationName());
						}
						else {
							sendEmail(softwareLicensesDTO);
						}
						return exceptionHandlerUtil.createSuccessResponse( API_RESPONSE_LICENSE_GENERATED, softwareLicenses);
					}

				} else {

					String admin = httpHeaders.getFirst("admin");
					if (admin == null) {

						if (softwareLicenses == null) {
							OrgSoftwareLicenses softwareLicensesmodel = new OrgSoftwareLicenses();

							OrgSoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel = new OrgSoftwareLicenseApprovalRequests();
							softwareLicensesmodel.setAppid(softwareName);
							softwareLicensesmodel.setCreatedDateTime(AppUtil.getDate());
							softwareLicensesmodel.setUpdatedDateTime(AppUtil.getDate());
							softwareLicensesmodel.setOuid(softwareLicensesDTO.getOuid());
							softwareLicensesmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicensesmodel.setLicenceStatus(APPLIED);
							softwareLicensesmodel.setApplicationName(softwareLicensesDTO.getApplicationType());
							softwareLicensesmodel.setOrganizationName(organizationDetails.getOrganizationName());

							softwareLicenseApprovalRequestsmodel.setApprovalStatus(APPLIED);
							softwareLicenseApprovalRequestsmodel.setUpdatedDateTime(AppUtil.getDate());
							softwareLicenseApprovalRequestsmodel.setOuid(softwareLicensesDTO.getOuid());
							softwareLicenseApprovalRequestsmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenseApprovalRequestsmodel.setCreatedDateTime(AppUtil.getDate());
							softwareLicenseApprovalRequestsmodel.setAppid(softwareName);

							softwareLicensesRepository.save(softwareLicensesmodel);
							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);

							sendEmailToAdmin(softwareLicensesDTO);
							return exceptionHandlerUtil.createSuccessResponse(
									"api.response.license.request.submitted", null);


						} else {

							if (softwareLicenses.getLicenceStatus().equals(APPLIED)) {
								return exceptionHandlerUtil.createErrorResponse("api.error.license.already.applied");
							}

							OrgSoftwareLicensesHistory softwareLicensesHistory = new OrgSoftwareLicensesHistory();
							String todayDate = LocalDate.now().toString();
							String year = todayDate.substring(0, 4);
							String month = todayDate.substring(5, 7);
							String date = todayDate.substring(8, 10);
							String validDate = softwareLicenses.getValidUpTo();
							String yearValid = validDate.substring(0, 4);
							String monthValid = validDate.substring(5, 7);
							String dateValid = validDate.substring(8, 10);

							if (Integer.parseInt(yearValid) > Integer.parseInt(year)) {
								return exceptionHandlerUtil.createErrorResponse(API_ERROR_LICENSE_ACTIVE
								);
							} else if (Integer.parseInt(yearValid) == Integer.parseInt(year)
									&& Integer.parseInt(monthValid) > Integer.parseInt(month)) {
								return exceptionHandlerUtil.createErrorResponse(
										API_ERROR_LICENSE_ACTIVE);
							} else if (Integer.parseInt(yearValid) == Integer.parseInt(year)
									&& Integer.parseInt(monthValid) == Integer.parseInt(month)
									&& Integer.parseInt(dateValid) > Integer.parseInt(date)) {
								return exceptionHandlerUtil.createErrorResponse(
										"api.error.license.already.active");
							}

							// history table
							softwareLicensesHistory.setAppid(softwareName);
							softwareLicensesHistory.setOuid(softwareLicenses.getOuid());


							softwareLicensesHistory.setLicenseType(softwareLicenses.getLicenseType());
							softwareLicensesHistory.setCreatedDateTime(softwareLicenses.getCreatedDateTime());
							softwareLicensesHistory.setIssuedOn(softwareLicenses.getIssuedOn());
							softwareLicensesHistory.setUpdatedDateTime(softwareLicenses.getUpdatedDateTime());
							softwareLicensesHistory.setValidUpto(softwareLicenses.getValidUpTo());

							// main table
							softwareLicenses.setAppid(softwareName);
							softwareLicenses.setCreatedDateTime(AppUtil.getDate());
							softwareLicenses.setUpdatedDateTime(AppUtil.getDate());
							softwareLicenses.setOuid(softwareLicensesDTO.getOuid());
							softwareLicenses.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenses.setLicenceStatus(APPLIED);
							softwareLicenses.setApplicationName(softwareLicensesDTO.getApplicationType());
							softwareLicenses.setOrganizationName(organizationDetails.getOrganizationName());
							softwareLicenseApprovalRequests.setApprovalStatus(APPLIED);
							softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenseApprovalRequests.setUpdatedDateTime(AppUtil.getDate());
							softwareLicenseApprovalRequests.setCreatedDateTime(AppUtil.getDate());
							softwareLicensesHistoryRepo.save(softwareLicensesHistory);
							softwareLicensesRepository.save(softwareLicenses);

							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequests);
							sendEmailToAdmin(softwareLicensesDTO);
							return exceptionHandlerUtil.createSuccessResponse(
									"api.response.license.request.submitted", null);

						}
					}
					else
						logger.info("response::::{}",softwareLicenses);

					OrgOrganizationDetailsForClient organizationDetailsForClient =
							organizationDetailsForClientRepoIface
									.getClientId(softwareLicensesDTO.getApplicationType(), softwareLicensesDTO.getOuid());

					logger.info("response from repo to get the client id {}" ,organizationDetailsForClient);


					if (organizationDetailsForClient != null) {

						softwareLicensesDTO.setClientId(organizationDetailsForClient.getClientId());
						logger.info("Using existing ClientId: {}",softwareLicensesDTO.getClientId());

					}
					else {


						BizAppCreateDto bizAppCreateDto = new BizAppCreateDto();

						String str = softwareLicenses.getApplicationName();
						String[] parts = str.split("_");
						int num = Integer.parseInt(parts[2]);


						String arabicAppName = convertToArabicName(softwareLicenses.getApplicationName());

						bizAppCreateDto.setApplicationName(softwareLicenses.getApplicationName());
						bizAppCreateDto.setappilicationNameArabic(arabicAppName);
						bizAppCreateDto.setApplicationType("Regular Web Application");
						bizAppCreateDto.setApplicationUri(applicationUri + num);
						bizAppCreateDto.setGrantTypes(AUTHORIZATION_CODE);
						bizAppCreateDto.setLogoutUri(logoutUri + num);
						bizAppCreateDto.setOrganizationId(softwareLicenses.getOuid());
						bizAppCreateDto.setRedirectUri(redirectUri + num);
						bizAppCreateDto.setScopes("openid urn:idp:digitalid:profile");
						bizAppCreateDto.setBase64Cert(crtFile);


						List<String> grantList = Arrays.asList(AUTHORIZATION_CODE, "authorization_code_with_pkce");
						bizAppCreateDto.setGrantTypesList(grantList);

						List<String> scopesList = Arrays.asList("openid", "urn:idp:digitalid:profile");
						bizAppCreateDto.setScopesList(scopesList);

						bizAppCreateDto.setAuthSchemaId("0");

						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);

						HttpEntity<Object> reqEntity = new HttpEntity<>(bizAppCreateDto, headers);

						ResponseEntity<ApiResponses> response = restTemplate.exchange(
								saveClient,
								HttpMethod.POST,
								reqEntity,
								ApiResponses.class
						);

						ApiResponses res = response.getBody();

						logger.info("Response from URL:{} {}",saveClient,response);

						if (res == null) {
							return AppUtil.createApiResponse(false, "Empty response from service", null);
						}

						if (!res.isSuccess()) {
							return AppUtil.createApiResponse(false, res.getMessage(), null);
						}
					}

					licenseBase64 = generateLicenses(softwareLicensesDTO,
							softwareLicensesDTO.getLicenseType());

					softwareLicenses.setIssuedOn(issuedOn);
					softwareLicenses.setUpdatedDateTime(LocalDate.now().toString());
					softwareLicenses.setValidUpTo(validUptoDate);
					softwareLicenses.setLicenceStatus(ACTIVE);

					softwareLicenses.setLicenseInfo(licenseBase64);

					softwareLicenseApprovalRequests.setApprovalStatus(ACTIVE);
					softwareLicenseApprovalRequests.setUpdatedDateTime(AppUtil.getDate());
					softwareLicenseApprovalRequests.setOuid(softwareLicensesDTO.getOuid());
					softwareLicenseApprovalRequests.setCreatedDateTime(AppUtil.getDate());

					softwareLicensesRepository.save(softwareLicenses);
					softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequests);

					organizationDetails.setManageByAdmin(false);
					organizationDetailsRepository.save(organizationDetails);

					logger.info("Calling email service");
					if(icpEnv){
						sendEmailICPLicenseGenerated(organizationDetails.getSpocUgpassEmail(),organizationDetails.getOrganizationName());
					}
					else {
						sendEmail(softwareLicensesDTO);
					}

					return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_LICENSE_GENERATED, softwareLicenses);

				}

			} else {
				return exceptionHandlerUtil.createErrorResponse( "api.error.organization.id.cant.be.null.empty");
			}
		} catch (Exception e) {
			logger.info("{}",e.getMessage());
			return exceptionHandlerUtil.createErrorResponse( "api.error.something.went.wrong.please.try.after.sometime");
		}

	}




	private String convertToArabicName(String appName) {
		String[] parts = appName.split("_");
		if (parts.length < 3) return "اسم غير صالح";

		String prefixAr = "بوابة المؤسسة";
		String number = parts[2];

		return prefixAr + " " + number;
	}

	@Override
	public ApiResponse downloadLicense(String ouid, String type) {
		try {

			if (ouid == null || ouid.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.organization.id.required");
			}

			if (type == null || type.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.license.type.required");
			}

			OrgSoftwareLicenses softwareLicenses =
					softwareLicensesRepository.findByOuidAndLicenseType(ouid, type);

			if (softwareLicenses == null) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.license.not.found");
			}

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.license.download.success",
					softwareLicenses.getLicenseInfo());

		} catch (Exception e) {
			logger.error("{} - {} : Exception occurred during DOWNLOAD LICENSE: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}



	@Override
	public ApiResponse getLicenseByOuid(String ouid) {
		try {

			if (ouid == null || ouid.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.organization.id.required");
			}

			List<OrgSoftwareLicenses> licenses =
					softwareLicensesRepository.findByOuid(ouid);

			if (licenses == null || licenses.isEmpty()) {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.license.not.available",
						new ArrayList<>());
			}

			LocalDate today = LocalDate.now();

			for (OrgSoftwareLicenses s : licenses) {

				if (!APPLIED.equalsIgnoreCase(s.getLicenceStatus())
						&& s.getValidUpTo() != null) {

					LocalDate expiryDate = LocalDate.parse(s.getValidUpTo());

					if (expiryDate.isBefore(today)) {
						s.setLicenceStatus(EXPIRED);
						softwareLicensesRepository.save(s);
					}

					s.setValidUpTo(
							expiryDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
				}
			}

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.licenses.fetched",
					licenses);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in getLicenseByOuid: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}



	@Override
	public ApiResponse getLicenseByOuidVG(String ouid) {
		try {
			if (ouid == null || ouid.isEmpty()) {
				return AppUtil.createApiResponse(false, "Organisation Id cannot be null", null);
			}

			OrgSoftwareLicenses s = softwareLicensesRepository.findByOuidVG(ouid);
			OrgSoftwareLicenses softwareLicenses = new OrgSoftwareLicenses();

			SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy");

			Date currentDate = dbFormat.parse(LocalDate.now().toString());
			if (!APPLIED.equalsIgnoreCase(s.getLicenceStatus()) && s.getValidUpTo() != null) {
				Date expiredDate = dbFormat.parse(s.getValidUpTo());

				if (expiredDate.before(currentDate)) {
					String[] result = s.getApplicationName().split("_");
					String lastRecord = result[result.length - 1];
					String[] applicationName = s.getApplicationName().split("_" + lastRecord);
					String softwareName = applicationName[0];
					softwareLicenses.setLicenceStatus(EXPIRED);
					OrgSoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel =
							softwareLicenseApprovalRequestsRepo.getSoftwareDetails(ouid, s.getLicenseType(), softwareName);
					if (softwareLicenseApprovalRequestsmodel != null) {
						softwareLicenseApprovalRequestsmodel.setApprovalStatus(EXPIRED);
						softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);
					}
					softwareLicensesRepository.save(s);
				}
				softwareLicenses.setValidUpTo(apiFormat.format(expiredDate));
			}

			softwareLicenses.setLicenceStatus(s.getLicenceStatus());

            return AppUtil.createApiResponse(true, "Licenses Fetched Successfully", softwareLicenses);

		} catch (Exception e) {
			logger.info("{}",e.getMessage());
			return AppUtil.createApiResponse(false, "An Exception Occurred", null);
		}
	}
	private String generateLicenses(SoftwareLicensesDTO softwareLicensesDTO,
									String type) {
		try {

			OrgOrganizationDetailsForClient organizationDetailsForClient ;
			organizationDetailsForClient = organizationDetailsForClientRepoIface
					.getClientId(softwareLicensesDTO.getApplicationType(), softwareLicensesDTO.getOuid());

			logger.info("Fetching Client ID From View");
			if(organizationDetailsForClient != null) {
				softwareLicensesDTO.setClientId(organizationDetailsForClient.getClientId());

			}

			String s = softwareLicensesDTO.licenseInfoNew(softwareLicensesDTO.getOuid(), type, "macaddress",
					softwareLicensesDTO.getClientId());
			Result res = DAESService.createSecureWireData(s);
			return new String(res.getResponse());


		} catch (Exception e) {
			logger.info("{}",e.getMessage());
			return e.getMessage();
		}
	}



	@Override
	public ApiResponse getListForGenerateLicense() {
		try {

			List<OrgSoftwareLicenses> list =
					softwareLicensesRepository.getListForGenerateLicenses();

			if (list == null || list.isEmpty()) {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.no.records",
						null);
			}

			return exceptionHandlerUtil.createSuccessResponse(
					API_RESPONSE_FETCHES_SUCCESSFULLY,
					list);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in getListForGenerateLicense: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public ApiResponse sendEmail(SoftwareLicensesDTO softwareLicensesDTO) {
		try {
			logger.info("Inside Email method");

			OrgOrganizationDetails organizationDetails =
					organizationDetailsRepository
							.findByOrganizationUid(softwareLicensesDTO.getOuid());

			if (organizationDetails == null) {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.organization.not.found",
						null);
			}



			List<String> listOfEmail = new ArrayList<>();
			listOfEmail.add(organizationDetails.getSpocUgpassEmail());

			String emailBody = "Dear SPOC "
					+ ",<br>Your software license request for the organization \""
					+ organizationDetails.getOrganizationName()
					+ "\" has been approved. Please proceed to download it.";

			EmailDto emailDto = new EmailDto();
			emailDto.setEmailBody(emailBody);
			emailDto.setRecipients(listOfEmail);
			emailDto.setSubject("Software License is generated successfully");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Object> requestEntity = new HttpEntity<>(emailDto, headers);

			ResponseEntity<ApiResponses> res =
					restTemplate.exchange(sendEmail,
							HttpMethod.POST,
							requestEntity,
							ApiResponses.class);


			if (res.getStatusCode().value() == 200) {// Extracting to a local variable guarantees null-safety for the linter
				var responseBody = res.getBody();

				return exceptionHandlerUtil.createSuccessResponse(
						API_RESPONSE_EMAIL_SENT,
						responseBody != null ? responseBody.getResult() : null);
			} else if (res.getStatusCode().value() == 400) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.response.bad.request");
			} else if (res.getStatusCode().value() == 500) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.response.internal.server.error");
			}

			return exceptionHandlerUtil.createSuccessResponse(
					API_RESPONSE_EMAIL_SENT,
					null);

		} catch (Exception e) {

			logger.error("{} - {} : Exception in sendEmail: {}",
					CLASS, Utility.getMethodName(), e.getMessage());

			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse sendEmailToAdmin(SoftwareLicensesDTO softwareLicensesDTO) {
		try {

			String[] result = softwareLicensesDTO.getApplicationType().split("_");
			String lastRecord = result[result.length - 1];
			String[] applicationName =
					softwareLicensesDTO.getApplicationType().split("_" + lastRecord);
			String softwareName = applicationName[0];

			OrgOrganizationDetails organizationDetails =
					organizationDetailsRepository
							.findByOrganizationUid(softwareLicensesDTO.getOuid());

			ApiResponse response = getAdminEmailList();

			if (!response.isSuccess()) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.response.admin.email.fetch.failed");
			}

			EmailDto emailDto = getEmailDto(response, softwareName, organizationDetails);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Object> requestEntity = new HttpEntity<>(emailDto, headers);

			ResponseEntity<ApiResponse> res =
					restTemplate.exchange(sendEmailAdmin,
							HttpMethod.POST,
							requestEntity,
							ApiResponse.class);

			// Extracting this prevents the linter from warning about consecutive getBody() calls
			var responseBody = res.getBody();

// Replaced deprecated getStatusCodeValue() with getStatusCode().value()
			if (res.getStatusCode().value() == 200) {
				return exceptionHandlerUtil.createSuccessResponse(
						API_RESPONSE_EMAIL_SENT,
						responseBody != null ? responseBody.getResult() : null);

			} else if (res.getStatusCode().value() == 400) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.response.bad.request");

			} else if (res.getStatusCode().value() == 500) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.response.internal.server.error");
			}

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.email.sent.success",
					null);

		} catch (Exception e) {

			logger.error("{} - {} : Exception in sendEmailToAdmin: {}",
					CLASS, Utility.getMethodName(), e.getMessage());

			return exceptionHandlerUtil.handleException(e);
		}
	}

	private EmailDto getEmailDto(ApiResponse response, String softwareName, OrgOrganizationDetails organizationDetails) {
		List<String> listOfEmail = (List<String>) response.getResult();

		EmailDto emailDto = new EmailDto();

		if (listOfEmail.size() >= noOfAdminEmail) {
			emailDto.setRecipients(
					listOfEmail.subList(0,
							Math.min(noOfAdminEmail, listOfEmail.size())));
		} else {
			emailDto.setRecipients(listOfEmail);
		}

		String emailBody = "Dear Admin,<br>Greetings!<br><br>SPOC have applied for license of the software \""
				+ softwareName.replace("_", " ")
				+ "\" for the organization \""
				+ organizationDetails.getOrganizationName()
				+ "\". Kindly do the needful.<br>";

		emailDto.setEmailBody(emailBody);
		emailDto.setSubject("Software License Request");
		return emailDto;
	}

	@Override
	public ApiResponse getAdminEmailList() {
		try {

			String adminEmailUrl = url;

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

			ResponseEntity<String> response =
					restTemplate.exchange(adminEmailUrl, HttpMethod.GET,
							requestEntity, String.class);

			int status = response.getStatusCode().value();

			if (status != 200 && status != 201) {
				logger.error("{} - {} : Failed to fetch admin emails. Status: {}",
						CLASS, Utility.getMethodName(), status);

				return exceptionHandlerUtil.createErrorResponse(
						"api.error.admin.email.fetch.failed");
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(response.getBody());
			JsonNode resourceNode = jsonNode.get("resource");

			List<String> emailList = convertJsonNodeToList(resourceNode);

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.records.fetched",
					emailList);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in getAdminEmailList: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	private static List<String> convertJsonNodeToList(JsonNode jsonNode) {
		List<String> stringList = new ArrayList<>();

		// Check if JsonNode is an array
		if (jsonNode.isArray()) {
			for (JsonNode element : jsonNode) {
				stringList.add(element.asText());
			}
		}

		return stringList;
	}

	@Override
	public ApiResponse addDeviceIdOfLicense(String applicationName,
											 List<String> deviceIDs) {
		try {

			if (applicationName == null || applicationName.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(
						API_ERROR_APPLICATION_NAME_REQUIRED);
			}

			OrgOrganizationDetailsForClient details =
					organizationDetailsForClientRepoIface
							.getOrganizationClientDetails(applicationName);

			if (details == null) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.organization.not.found");
			}

			deviceIDs.forEach(deviceId -> {
				OrgLicenseDeviceList model = new OrgLicenseDeviceList();
				model.setOrganizationName(details.getOrgName());
				model.setApplicationName(details.getApplicationName());
				model.setClientId(details.getClientId());
				model.setDeviceId(deviceId);
				model.setCreatedDate(AppUtil.getDate());
				model.setUpdatedDate(AppUtil.getDate());
				licenseDeviceListRepo.save(model);
			});

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.device.saved",
					null);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in addDeviceIdOfLicense: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse getDeviceID(String clientId) {
		try {

			if (!StringUtils.hasText(clientId)) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.client.id.required");
			}

			List<String> deviceList =
					licenseDeviceListRepo.getLicenseDeviceDetailsList(clientId);

			if (deviceList == null || deviceList.isEmpty()) {
				return exceptionHandlerUtil.createSuccessResponse(
						"api.response.no.records",
						new ArrayList<>());
			}

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.records.fetched",
					deviceList);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in getDeviceID: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse getDeviceIdDetails(String applicationName) {
		try {
			if (applicationName == null || applicationName.isEmpty()) {
				return AppUtil.createApiResponse(false, "Application name should not be null or empty", null);
			} else {
				List<OrgLicenseDeviceList> licenseDeviceList = licenseDeviceListRepo.getLicenseDeviceList(applicationName);
				if (!licenseDeviceList.isEmpty()) {
					return AppUtil.createApiResponse(true, "Data fetched successfully", licenseDeviceList);

				} else {
					return AppUtil.createApiResponse(true, "No record found", licenseDeviceList);
				}

			}
		} catch (Exception e) {
			logger.info("{}",e.getMessage());
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

	@Override
	public ApiResponse updateDeviceIdOfLicense(String applicationName,
												String oldDeviceId,
												String newDeviceId) {
		try {

			if (!StringUtils.hasText(applicationName)) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.application.name.required");
			}

			if (!StringUtils.hasText(oldDeviceId)) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.old.device.id.required");
			}

			if (!StringUtils.hasText(newDeviceId)) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.new.device.id.required");
			}

			OrgLicenseDeviceList model =
					licenseDeviceListRepo.getLicenseDevice(oldDeviceId,
							applicationName);

			if (model == null) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.record.not.found");
			}

			model.setDeviceId(newDeviceId);
			model.setUpdatedDate(AppUtil.getDate());

			licenseDeviceListRepo.save(model);

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.device.updated",
					null);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in updateDeviceIdOfLicense: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse deleteRecordByDeviceID(String deviceId,
											   String applicationName) {
		try {

			if (!StringUtils.hasText(deviceId)) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.device.id.required");
			}

			if (!StringUtils.hasText(applicationName)) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.application.name.required");
			}

			int deleted =
					licenseDeviceListRepo.deleteRecordByDeviceId(
							deviceId, applicationName);

			if (deleted == 0) {
				return exceptionHandlerUtil.createErrorResponse(
						"api.error.record.not.found");
			}

			return exceptionHandlerUtil.createSuccessResponse(
					"api.response.device.deleted",
					null);

		} catch (Exception e) {
			logger.error("{} - {} : Exception in deleteRecordByDeviceID: {}",
					CLASS, Utility.getMethodName(), e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}



    public ApiResponse sendEmailICPLicenseGenerated(String spocEmail, String orgName) {
        String methodName = Utility.getMethodName();
        logger.info("{} - {}: Sending email to SPOC: {}", CLASS, methodName, spocEmail);
        try {
            if (!StringUtils.hasText(spocEmail)) {
                return exceptionHandlerUtil.createErrorResponse(
                        Constant.API_ERROR_SPOC_EMAIL_CANT_BE_NULL_OR_EMPTY);
            }
            if (!StringUtils.hasText(orgName)) {
                return exceptionHandlerUtil.createErrorResponse(
                        Constant.API_ERROR_ORG_NAME_CANT_BE_NULL_OR_EMPTY);
            }
            String subscriberUid = subscriberRepository.findByemailId(spocEmail);
            OrgSubscriberPreferences preferences = null;
            if (subscriberUid != null) {
                preferences = subscriberPreferencesRepo.getBySubUid(subscriberUid);
            }
            String language = "en";
            if (preferences != null && StringUtils.hasText(preferences.getLanguagePreferred())) {
                language = preferences.getLanguagePreferred();
            }
            String emailBody;
            String subjectl;
			if ("ar".equalsIgnoreCase(language)) {

				subjectl = "تم إنشاء الترخيص البرمجي بنجاح";

				// Arabic Email
				emailBody = String.format("""
        <html>
        <body dir="rtl" style="font-family: Arial; text-align:right">

        <p>
        عزيزي نقطة الاتصال الرئيسية (SPOC)،
        <br><br>

        يسعدنا إبلاغكم بأن المؤسسة "<b>%s</b>" قد تم إنشاؤها بنجاح،
        وقد تم تعيينكم كنقطة الاتصال الرئيسية للمؤسسة.
        <br><br>

        لأي استفسارات إضافية يرجى التواصل مع المسؤول.
        </p>

        <br><br>
        - نظام الهوية الرقمية الإماراتية
        <br>
        <img src='cid:image1' width='150' height='51'/>
        <br>

        </body>
        </html>
        """, orgName);

			} else {

				subjectl = "Software License is generated successfully";

				emailBody = String.format("""
            <html>
            <body style="font-family: Arial">

            <p>
            Dear SPOC,<br><br>

            Your software license request for the organization "<b>%s</b>" has been approved.
            Please proceed to download it.
            <br><br>

            We are pleased to inform you that the organization "<b>%s</b>" has been successfully created
            and you have been designated as the SPOC (Single Point of Contact).
            <br><br>

            For any further inquiries please reach out to the administrator.
            </p>

            <br><br>
            - UAEID System
            <br>
            <img src="cid:image1" width="150" height="51"/>
            <br>
         

            </body>
            </html>
            """, orgName, orgName.toUpperCase());
			}

            sendMailICP(spocEmail, subjectl, emailBody);
            logger.info("{} - {}: Email sent successfully to {}", CLASS, methodName, spocEmail);
            return AppUtil.createApiResponse(true, "Email sent successfully", null);
        } catch (Exception e) {
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private void sendMailICP(String receiver, String subject, String body) {
        try{

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(receiver);
            helper.setFrom(senderEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            ClassPathResource image = new ClassPathResource("UAEID.png");

            helper.addInline("image1", image);

            mailSender.send(mimeMessage);
        }catch (Exception e){
            logger.info("{}",e.getMessage());
        }
    }

}
