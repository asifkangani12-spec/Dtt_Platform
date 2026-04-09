package com.dtt.service.impl;


import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.enums.IdentifierType;
import com.dtt.enums.ResponseType;
import com.dtt.model.GenericSubscriber;
import com.dtt.model.GenericSubscriberOnboardingData;
import com.dtt.repo.GenericSubscriberOnboardingDataRepo;
import com.dtt.repo.GenericSubscriberRepo;
import com.dtt.responsedto.*;
import com.dtt.service.iface.ProvisioningServiceIface;
import com.dtt.utils.CountryInfo;
import com.dtt.utils.CountryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProvisioningServiceImpl implements ProvisioningServiceIface {

    public static final String SOMETHING_WENT_WRONG = "api.error.something.went.wrong";
    public static final String API_RESPONSE_FETCHED = "api.response.fetched";
    public static final String SUBSCRIBER_OB_DATA_NOT_FOUND = "api.error.subscriber.ob.data.cannot.be.found";
    private static final String API_ERROR_SUBSCRIBER_NOT_FOUND = "api.error.subscriber.not.found";

    private static final String ACTIVE_PASSPORT = "ActivePassport";
    private static final String FULL_NAME_AR = "FullNameAr";
    private static final String FULL_NAME_EN = "FullNameEn";
    private static final String GENDER_EN = "GenderEn";
    private static final String OCCUPATION_EN = "OccupationEn";
    private static final String ISSUE_DATE = "IssueDate";
    private static final String DOCUMENT_NO = "DocumentNo";
    private static final String PERSON_FACE = "PersonFace";
    private static final String DOCUMENT_TYPE = "DocumentType";
    private static final String DOCUMENT_NATIONALITY_ABBR = "DocumentNationalityAbbr";
    private static final String ACTIVE_VISA = "ActiveVisa";
    private static final String DATE_OF_BIRTH = "DateOfBirth";
    private static final String CURRENT_NATIONALITY = "CurrentNationality";
    private static final String RESULT = "Result";
    private static final String EXPIRY_DATE = "ExpiryDate";
    private static final String DOCUMENT_NATIONALITY = "DocumentNationality";
    public static final String PERSONAL_INFO = "PersonalInfo";
    public static final String EMIRATES_ID_NUMBER = "EmiratesIdNumber";
    public static final String CUSTOMER_DETAILS = "customerDetails";
    public static final String RESIDENCE_INFO="ResidenceInfo";
    public static final String PERSON_CLASS ="PersonClass";
    public static final String FAMILY_NAME_EN ="FamilyNameEn";
    public static final String FIRST_NAME_EN = "FirstNameEn";




    private final GenericSubscriberRepo subscriberRepoIface;


    private final GenericSubscriberOnboardingDataRepo subscriberOnboardingDataRepoIface;

    private final CountryService countryService;

    private final ObjectMapper objectMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;
    private static final String IMMIGRATION_FILE = "ImmigrationFile";

    private static final Logger logger = LoggerFactory.getLogger(ProvisioningServiceImpl.class);

    public ProvisioningServiceImpl(GenericSubscriberRepo subscriberRepoIface, GenericSubscriberOnboardingDataRepo subscriberOnboardingDataRepoIface,
                                   ObjectMapper objectMapper,
                                   ExceptionHandlerUtil exceptionHandlerUtil,
                                   CountryService countryService) {
        this.subscriberRepoIface = subscriberRepoIface;
        this.subscriberOnboardingDataRepoIface = subscriberOnboardingDataRepoIface;
        this.objectMapper = objectMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.countryService = countryService;
    }


    @Override
    public ApiResponse getProfileDetailsForProvisioning(IdentifierType identifierType, ResponseType responseType, String identifierValue) {

        try {
            logger.info("Fetching profile | identifierType={} | identifierValue={} | responseType={}", identifierType, identifierValue,responseType);
            Optional<GenericSubscriberOnboardingData> profileOpt = fetchProfile(identifierValue, identifierType);


            if (profileOpt.isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse("api.error.profile.not.found");
            }



            GenericSubscriberOnboardingData profile = profileOpt.get();


            Object responseDto = buildResponse(profile, responseType);
            return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_FETCHED ,responseDto);

        }catch (Exception e){
            return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);
        }
    }




    private Optional<GenericSubscriberOnboardingData> fetchProfile(
            String identifierValue,
            IdentifierType identifierType) {

        try {

            return switch (identifierType) {

                case passportNumber -> {
                    GenericSubscriber subscriber = subscriberRepoIface.findByPassportNumber(identifierValue);

                    if (subscriber == null || subscriber.getSubscriberUid() == null) {
                        logger.info("No subscriber found for passportNumber={}", identifierValue);
                        yield Optional.empty();
                    }

                    yield subscriberOnboardingDataRepoIface
                            .findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(
                                    subscriber.getSubscriberUid()
                            );
                }

                case IDNumber -> {
                    GenericSubscriber subscriber = subscriberRepoIface.findByNationalIdNumber(identifierValue);

                    if (subscriber == null || subscriber.getSubscriberUid() == null) {
                        logger.info("No subscriber found for Emirates ID={}", identifierValue);
                        yield Optional.empty();
                    }

                    yield subscriberOnboardingDataRepoIface
                            .findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(
                                    subscriber.getSubscriberUid()
                            );
                }

                case SUID -> subscriberOnboardingDataRepoIface
                        .findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(
                                identifierValue
                        );
            };
        } catch (Exception ex) {
            logger.info("Error fetching profile. identifierType={}, identifierValue={}",
                    identifierType, identifierValue, ex);
            return Optional.empty();
        }
    }







    private Object buildResponse(
            GenericSubscriberOnboardingData profile,
            ResponseType responseType)
            {

        try {
            JsonNode root = objectMapper.readTree(profile.getNiraResponse());

            JsonNode dataNode = root
                    .path(CUSTOMER_DETAILS)
                    .path(RESULT)
                    .path("Data");

            JsonNode personalInfo = dataNode.path(PERSONAL_INFO);
            JsonNode passport = dataNode.path(ACTIVE_PASSPORT);
            JsonNode residence = dataNode.path(RESIDENCE_INFO);
            JsonNode documents = dataNode.path("Documents");

            JsonNode immigrationFile = dataNode.path(IMMIGRATION_FILE);
            JsonNode activeVisas = dataNode.path(ACTIVE_VISA);
            return switch (responseType) {


                case EMIRATES_ID -> {

                    DigitalEmiratesIdDTO dto = new DigitalEmiratesIdDTO();

                    dto.setFullNameAr(personalInfo.path(FULL_NAME_AR).asText("null"));
                    dto.setDateOfBirth(personalInfo.path(DATE_OF_BIRTH).asText("null"));
                    dto.setCurrentNationality(personalInfo.path(CURRENT_NATIONALITY).asText("null"));
                    dto.setGenderEn(personalInfo.path(GENDER_EN).asText("null"));

                    dto.setFullNameEn(personalInfo.path(FULL_NAME_EN).asText("null"));
                    dto.setFirstNameEn(personalInfo.path("").asText("null"));
                    dto.setSecondNameEn(personalInfo.path("SecondNameEn").asText("null"));
                    dto.setFamilyNameEn(personalInfo.path(FAMILY_NAME_EN).asText("null"));

                    dto.setOccupationAr(personalInfo.path("OccupationAr").asText("null"));
                    dto.setOccupationEn(personalInfo.path(OCCUPATION_EN).asText("null"));

                    dto.setEmiratesIdNumber(residence.path(EMIRATES_ID_NUMBER).asText("null"));
                    dto.setIssueDate(residence.path(ISSUE_DATE).asText("null"));
                    dto.setExpiryDate(residence.path(EXPIRY_DATE).asText("null"));

                    dto.setSponsorNameAr(residence.path("SponsorNameAr").asText("null"));
                    dto.setSponsorNameEn(residence.path("SponsorNameEn").asText("null"));

                    dto.setDocumentNo(residence.path(DOCUMENT_NO).asText("null"));

                    dto.setPersonFace(documents.path(PERSON_FACE).asText("null"));
                    dto.setIssuePlace(immigrationFile.path("IssuePlace").asText("null"));
                    dto.setDigitalSignature(documents.path("DigitalSignature").asText("null"));
                    dto.setDigitalEID(documents.path("DigitalEID").asText("null"));



                    yield dto;
                }


                case PASSPORT -> {
                    DigitalPassportDTO dto = new DigitalPassportDTO();

                    dto.setFullNameAr(personalInfo.path(FULL_NAME_AR).asText("null"));
                    dto.setDocumentType(passport.path(DOCUMENT_TYPE).asText("null"));
                    dto.setDocumentNo(passport.path(DOCUMENT_NO).asText("null"));

                    dto.setDocumentNationalityAbbr(
                            passport.path(DOCUMENT_NATIONALITY_ABBR).asText("null"));
                    dto.setDocumentNationality(
                            passport.path(DOCUMENT_NATIONALITY).asText("null"));

                    dto.setPassportIssueDate(passport.path(ISSUE_DATE).asText("null"));
                    dto.setPassportExpiryDate(passport.path(EXPIRY_DATE).asText("null"));

                    dto.setFirstNameEn(personalInfo.path(FIRST_NAME_EN).asText("null"));
                    dto.setSecondNameEn(personalInfo.path("SecondNameEn").asText("null"));

                    dto.setDateOfBirth(personalInfo.path(DATE_OF_BIRTH).asText("null"));
                    dto.setGenderEn(personalInfo.path(GENDER_EN).asText("null"));

                    dto.setPlaceOfBirthEn(personalInfo.path("PlaceOfBirthEn").asText("null"));
                    dto.setPassportIssuePlace(
                            passport.path("DocumentIssueCountry").asText("null"));

                    dto.setPersonFace(documents.path(PERSON_FACE).asText("null"));
                    dto.setDigitalSignature(documents.path("DigitalSignature").asText("null"));
                    dto.setPassportIssuePlace(activeVisas.path("PassportIssuePlace").asText("null"));
                    yield dto;
                }

                /* ---------------- UAEID AUTH ---------------- */
                case UAEID_AUTH -> {
                    UAEIDAuthenticationDTO dto = new UAEIDAuthenticationDTO();

                    dto.setFullNameEn(personalInfo.path(FULL_NAME_EN).asText("null"));
                    dto.setDateOfBirth(personalInfo.path(DATE_OF_BIRTH).asText("null"));
                    dto.setCurrentNationality(personalInfo.path(CURRENT_NATIONALITY).asText("null"));
                    dto.setGenderEn(personalInfo.path(GENDER_EN).asText("null"));
                    dto.setOccupationEn(personalInfo.path(OCCUPATION_EN).asText("null"));

                    dto.setEmiratesIdNumber(residence.path(EMIRATES_ID_NUMBER).asText("null"));
                    dto.setIssueDate(residence.path(ISSUE_DATE).asText("null"));
                    dto.setExpiryDate(residence.path(EXPIRY_DATE).asText("null"));

                    dto.setDocumentType(passport.path(DOCUMENT_TYPE).asText("null"));
                    dto.setDocumentNo(passport.path(DOCUMENT_NO).asText("null"));
                    dto.setDocumentNationalityAbbr(
                            passport.path(DOCUMENT_NATIONALITY_ABBR).asText("null"));
                    dto.setDocumentNationality(
                            passport.path(DOCUMENT_NATIONALITY).asText("null"));

                    dto.setPassportType(passport.path(DOCUMENT_TYPE).asText("null"));

                    dto.setPassportIssueDate(passport.path(ISSUE_DATE).asText("null"));
                    dto.setPassportExpiryDate(passport.path(EXPIRY_DATE).asText("null"));

                    dto.setSuid(profile.getSubscriberUid());
                    dto.setLoa(profile.getLevelOfAssurance());


                    yield dto;
                }
            };

        } catch (Exception e) {
            return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);
        }
    }


    @Override
    public ApiResponse getUserProfileDetailsBySuid(String suid) {
        try{


            GenericSubscriber subscriber = subscriberRepoIface.findBySubscriberUid(suid);
            if(subscriber == null){
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
            }
            Optional<GenericSubscriberOnboardingData> subscriberOnboardingData = subscriberOnboardingDataRepoIface.findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(subscriber.getSubscriberUid());

            if(subscriberOnboardingData.isEmpty()){
                return exceptionHandlerUtil.createErrorResponse(SUBSCRIBER_OB_DATA_NOT_FOUND);
            }

            UAEIDAuthenticationProfileDTO uaeidAuthenticationProfileDTO = new UAEIDAuthenticationProfileDTO();

            JsonNode root = objectMapper.readTree(subscriberOnboardingData.get().getNiraResponse());

            JsonNode dataNode = root
                    .path(CUSTOMER_DETAILS)
                    .path(RESULT)
                    .path("Data");

            JsonNode personalInfo = dataNode.path(PERSONAL_INFO);
            JsonNode residence = dataNode.path(RESIDENCE_INFO);
            String countryCode = personalInfo.path(CURRENT_NATIONALITY).asText(null);

            Optional<CountryInfo> country = (countryCode == null)
                    ? Optional.empty()
                    : countryService.getCountryInfo(countryCode);


            uaeidAuthenticationProfileDTO.setIdn(residence.path(EMIRATES_ID_NUMBER).asText(null));
            uaeidAuthenticationProfileDTO.setFullnameEN(personalInfo.path(FULL_NAME_EN).asText(null));
            uaeidAuthenticationProfileDTO.setFullnameAR(personalInfo.path(FULL_NAME_AR).asText(null));
            uaeidAuthenticationProfileDTO.setFirstnameEN(personalInfo.path(FIRST_NAME_EN).asText(null));
            uaeidAuthenticationProfileDTO.setFirstnameAR(personalInfo.path("FirstNameAr").asText(null));
            uaeidAuthenticationProfileDTO.setLastnameEN(personalInfo.path(FAMILY_NAME_EN).asText(null));
            uaeidAuthenticationProfileDTO.setLastnameAR(personalInfo.path("FamilyNameAr").asText(null));
            uaeidAuthenticationProfileDTO.setNationalityEN(country.map(CountryInfo::nameEN).orElse(countryCode));
            uaeidAuthenticationProfileDTO.setNationalityAR(country.map(CountryInfo::nameAR).orElse(countryCode));
            uaeidAuthenticationProfileDTO.setGender(personalInfo.path(GENDER_EN).asText(null));
            uaeidAuthenticationProfileDTO.setIdType(personalInfo.path(PERSON_CLASS).asText(null));
            uaeidAuthenticationProfileDTO.setTitleEN(personalInfo.path("TitleEn").asText(null));
            uaeidAuthenticationProfileDTO.setTitleAR(personalInfo.path("TitleAr").asText(null));
            uaeidAuthenticationProfileDTO.setDateOfBirth(personalInfo.path(DATE_OF_BIRTH).asText(null));
            uaeidAuthenticationProfileDTO.setProfileType(personalInfo.path(PERSON_CLASS).asText(null));
            uaeidAuthenticationProfileDTO.setLoa(subscriberOnboardingData.get().getLevelOfAssurance());
            uaeidAuthenticationProfileDTO.setSuid(suid);
            uaeidAuthenticationProfileDTO.setUnifiedId(dataNode.path("uidNumber").asText(null));
            uaeidAuthenticationProfileDTO.setPassportNumber(subscriber.getIdDocNumber());
            return exceptionHandlerUtil.createSuccessResponse("api.response.UAEID.authentication.profile.fetched.Successfully",uaeidAuthenticationProfileDTO);

        }catch (Exception e){
            logger.error("Exception occurred in getUserProfileDetailsBySuid :", e);
            return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);
        }
    }


    @Override
    public ApiResponse getUAEIDAuthenticatinProfileBySuid(String suid) {
        try{


            GenericSubscriber subscriber = subscriberRepoIface.findBySubscriberUid(suid);
            if(subscriber == null){
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
            }
            Optional<GenericSubscriberOnboardingData> subscriberOnboardingData = subscriberOnboardingDataRepoIface.findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(subscriber.getSubscriberUid());

            if(subscriberOnboardingData.isEmpty()){
                return exceptionHandlerUtil.createErrorResponse(SUBSCRIBER_OB_DATA_NOT_FOUND);
            }

            UAEIDAuthenticationProfileDTO uaeidAuthenticationProfileDTO = new UAEIDAuthenticationProfileDTO();

            JsonNode root = objectMapper.readTree(subscriberOnboardingData.get().getNiraResponse());

            JsonNode dataNode = root
                    .path(CUSTOMER_DETAILS)
                    .path(RESULT)
                    .path("Data");

            JsonNode personalInfo = dataNode.path(PERSONAL_INFO);
            JsonNode residence = dataNode.path(RESIDENCE_INFO);
            String countryCode = personalInfo.path(CURRENT_NATIONALITY).asText("null");

            Optional<CountryInfo> country = (countryCode == null || countryCode.equals("null"))
                    ? Optional.empty()
                    : countryService.getCountryInfo(countryCode);


            uaeidAuthenticationProfileDTO.setIdn(residence.path(EMIRATES_ID_NUMBER).asText("null"));
            uaeidAuthenticationProfileDTO.setFullnameEN(personalInfo.path(FULL_NAME_EN).asText("null"));
            uaeidAuthenticationProfileDTO.setFullnameAR(personalInfo.path(FULL_NAME_AR).asText("null"));
            uaeidAuthenticationProfileDTO.setFirstnameEN(personalInfo.path(FIRST_NAME_EN).asText("null"));
            uaeidAuthenticationProfileDTO.setFirstnameAR(personalInfo.path("FirstNameAr").asText("null"));
            uaeidAuthenticationProfileDTO.setLastnameEN(personalInfo.path(FAMILY_NAME_EN).asText("null"));
            uaeidAuthenticationProfileDTO.setLastnameAR(personalInfo.path("FamilyNameAr").asText("null"));
            uaeidAuthenticationProfileDTO.setNationalityEN(country.map(CountryInfo::nameEN).orElse(countryCode));
            uaeidAuthenticationProfileDTO.setNationalityAR(country.map(CountryInfo::nameAR).orElse(countryCode));
            uaeidAuthenticationProfileDTO.setGender(personalInfo.path(GENDER_EN).asText("null"));
            uaeidAuthenticationProfileDTO.setIdType(personalInfo.path(PERSON_CLASS).asText("null"));
            uaeidAuthenticationProfileDTO.setTitleEN(personalInfo.path("TitleEn").asText("null"));
            uaeidAuthenticationProfileDTO.setTitleAR(personalInfo.path("TitleAr").asText("null"));
            uaeidAuthenticationProfileDTO.setDateOfBirth(personalInfo.path(DATE_OF_BIRTH).asText("null"));
            uaeidAuthenticationProfileDTO.setProfileType(personalInfo.path(PERSON_CLASS).asText("null"));
            uaeidAuthenticationProfileDTO.setLoa(subscriberOnboardingData.get().getLevelOfAssurance());
            uaeidAuthenticationProfileDTO.setSuid(suid);
            uaeidAuthenticationProfileDTO.setUnifiedId(dataNode.path("uidNumber").asText("null"));
            uaeidAuthenticationProfileDTO.setPassportNumber(subscriber.getIdDocNumber());
            return exceptionHandlerUtil.createSuccessResponse("api.response.UAEID.authentication.profile.fetched.Successfully",uaeidAuthenticationProfileDTO);

        }catch (Exception e){
            logger.error("Exception occurred:", e);
            return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);
        }
    }



    @Override
    public ApiResponse getVisaCredentialsByPassportNo(String passportNumber) {
        logger.info("START getVisaCredentialsByPassportNo: {}", passportNumber);

        try {
            // Validate Input
            if (passportNumber == null || passportNumber.isBlank()) {
                logger.warn(" Passport NULL/BLANK");
                return exceptionHandlerUtil.createErrorResponse( "api.error.passportNo.cant.be.null");
            }
            logger.debug(" Passport OK");

            // Fetch Subscriber
            GenericSubscriber subscriber = subscriberRepoIface.findByPassportNumber(passportNumber);
            logger.info(" Subscriber: {}", subscriber != null ? subscriber.getSubscriberUid() : "NULL");
            if (subscriber == null) {
                logger.warn("Subscriber NOT FOUND");
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
            }
            logger.debug("Subscriber found");

            // Fetch Latest Onboarding Data
            Optional<GenericSubscriberOnboardingData> subscriberOnboardingData =
                    subscriberOnboardingDataRepoIface
                            .findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(
                                    subscriber.getSubscriberUid());

            logger.info("Onboarding data: {}", subscriberOnboardingData.isPresent());
            if (subscriberOnboardingData.isEmpty()) {
                logger.warn(" No onboarding data");
                return exceptionHandlerUtil.createErrorResponse(SUBSCRIBER_OB_DATA_NOT_FOUND);
            }
            logger.debug("Onboarding data found");

            // Parse NIRA Response JSON
            String niraResponse = subscriberOnboardingData.get().getNiraResponse();
            logger.info("NIRA length: {}", niraResponse != null ? niraResponse.length() : 0);

            logger.debug(" Parsing JSON...");
            JsonNode root = objectMapper.readTree(niraResponse);
            logger.debug(" JSON parsed");

            JsonNode dataNode = root.path(CUSTOMER_DETAILS).path(RESULT).path("Data");
            logger.info("DataNode exists: {}", !dataNode.isMissingNode());

            if (dataNode.isMissingNode() || dataNode.isEmpty()) {
                logger.error(" DataNode missing/empty");
                return exceptionHandlerUtil.createErrorResponse( "api.error.invalid.nira.response.structure");
            }
            logger.debug("DataNode OK");

            // Create and populate flat DTO directly
            logger.debug(" Creating VisaResponseDTO...");
            VisaResponseDTO visaResponseDTO = new VisaResponseDTO();
            String uidNumber = subscriber.getSubscriberUid();
            visaResponseDTO.setUidNumber(uidNumber);
            logger.debug("UID set: {}", uidNumber);

            // ImmigrationFile.FileNumber
            visaResponseDTO.setFileNumber(dataNode.path(IMMIGRATION_FILE).path("FileNumber").asText("null"));
            logger.debug("FileNumber OK");

            // ActivePassport
            visaResponseDTO.setDocumentNo(dataNode.path(ACTIVE_PASSPORT).path(DOCUMENT_NO).asText("null"));
            visaResponseDTO.setDocumentType(dataNode.path(ACTIVE_PASSPORT).path(DOCUMENT_NO).asText("null"));
            logger.debug("ActivePassport OK");

            // PersonalInfo
            visaResponseDTO.setFullNameEn(dataNode.path(PERSONAL_INFO).path(FULL_NAME_EN).asText("null"));
            visaResponseDTO.setFullNameAr(dataNode.path(PERSONAL_INFO).path(FULL_NAME_AR).asText("null"));
            visaResponseDTO.setPlaceOfBirthEn(dataNode.path(PERSONAL_INFO).path("PlaceOfBirthEn").asText("null"));
            visaResponseDTO.setPlaceOfBirthAr(dataNode.path(PERSONAL_INFO).path("PlaceOfBirthAr").asText("null"));
            visaResponseDTO.setOccupationEn(dataNode.path(PERSONAL_INFO).path(OCCUPATION_EN).asText("null"));
            visaResponseDTO.setOccupationAr(dataNode.path(PERSONAL_INFO).path("OccupationAr").asText("null"));
            visaResponseDTO.setCurrentNationality(dataNode.path(PERSONAL_INFO).path(CURRENT_NATIONALITY).asText("null"));
            logger.debug("PersonalInfo OK");

            // ActiveVisa
            visaResponseDTO.setVisaType(dataNode.path(ACTIVE_VISA).path("VisaType").asText("null"));
            visaResponseDTO.setIssueDate(dataNode.path(ACTIVE_VISA).path(ISSUE_DATE).asText("null"));
            visaResponseDTO.setExpiryDate(dataNode.path(ACTIVE_VISA).path(EXPIRY_DATE).asText("null"));
            logger.debug("ActiveVisa OK");

            // Documents.PersonFace
            visaResponseDTO.setPersonFace(dataNode.path("Documents").path(PERSON_FACE).asText("null"));
            logger.debug("Documents OK");

            logger.info("SUCCESS for passport: {}", passportNumber);

            return exceptionHandlerUtil.createSuccessResponse( API_RESPONSE_FETCHED,visaResponseDTO);

        } catch (JsonProcessingException e) {
            logger.error("JSON PARSE FAILED for {}: {}", passportNumber, e.getMessage(), e);
            return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);
        } catch (Exception e) {
            logger.error("CRASH at passport {}: {}", passportNumber, e.getMessage(), e);
            logger.error("Exception occurred:", e);
            return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);
        }
    }
}
