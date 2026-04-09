package ug.daes.onboarding.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.common.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ug.daes.DAESService;
import ug.daes.PKICoreServiceException;
import ug.daes.Result;

import ug.daes.onboarding.config.OnboardingSentryClientExceptions;

import ug.daes.onboarding.dto.*;
import ug.daes.onboarding.enums.LogMessageType;
import ug.daes.onboarding.enums.ServiceNames;
import ug.daes.onboarding.enums.TransactionType;
import ug.daes.onboarding.model.*;
import ug.daes.onboarding.repository.*;
import ug.daes.onboarding.service.iface.ProposedFlowIface;
import ug.daes.onboarding.service.iface.SubscriberServiceIface;
import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


import static ug.daes.onboarding.util.VersionComparatorThread.subscriber;

@Service
public class ProposedFlowImpl implements ProposedFlowIface {

    private static Logger logger = LoggerFactory.getLogger(ProposedFlowImpl.class);
    private static final String COMPLETED = "COMPLETED";
    private static final String API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL = "api.error.temporary.table.dto.cannot.be.null";
    private static final String DATA_ALREADY_EXISTS = "{}{} - Data already exists in temporary table for idDocNumber: {}";
    private static final String API_RESPOSNE_TEMP_TABLE_RECORD_DELETED_SUCCESSFULLY = "api.response.temporary.table.record.deleted.successfully";
    private static final String API_ERROR_NO_RECORD_FOUND_FOR_ID_DOC = "api.error.no.record.found.for.given.document.id.number";
    private static final String API_RESPOSNE_DETAILS_FOUND = "api.response.details.found";
    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception";
    private static final String EXCEPTION = "{}{} - Exception: {}";

    private static final String API_ERROR_ID_DOC_CANNOT_BE_NULL = "api.error.id.doc.number.cannot.be.null";
    private static final String API_TEMP_TABLE_RECORD_IS_NOT_DELETED_BY_USING_DEVICE_ID = "api.error.temporary.table.record.is.not.deleted.by.using.device.id";

    private static final String CLASS = "ProposedFlowImpl";


    private final OnboardingSentryClientExceptions sentryClientExceptions;
    private final RestTemplate restTemplate;
    private final SubscriberRepoIface subscriberRepoIface;
    private final SubscriberDeviceRepoIface subscriberDeviceRepoIface;
    private final SubscriberOnboardingDataRepoIface onboardingDataRepoIface;
    private final TemporaryTableRepo temporaryTableRepo;
    private final OnboardingStepDetailsRepoIface onboardingStepsRepoIface;
    private final OnbKafkaSender mqSender;
    private final SubscriberOnboardingDataRepoIface subscriberOnboardingDataRepoIface;
    private final PhotoFeaturesRepo photoFeaturesRepo;
    private final SubscriberPreferencesRepo subscriberPreferencesRepo;

    private final SubscriberServiceIface subscriberServiceIface;
    private final ExceptionHandlerUtil exceptionHandlerUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProposedFlowImpl(
            OnboardingSentryClientExceptions sentryClientExceptions,
            RestTemplate restTemplate,
            SubscriberRepoIface subscriberRepoIface,
            SubscriberDeviceRepoIface subscriberDeviceRepoIface,
            SubscriberOnboardingDataRepoIface onboardingDataRepoIface,
            TemporaryTableRepo temporaryTableRepo,
            OnboardingStepDetailsRepoIface onboardingStepsRepoIface,
            OnbKafkaSender mqSender,
            SubscriberOnboardingDataRepoIface subscriberOnboardingDataRepoIface,
            PhotoFeaturesRepo photoFeaturesRepo,
            SubscriberPreferencesRepo subscriberPreferencesRepo,
            @Lazy SubscriberServiceIface subscriberServiceIface,
            ExceptionHandlerUtil exceptionHandlerUtil) {

        this.sentryClientExceptions = sentryClientExceptions;
        this.restTemplate = restTemplate;
        this.subscriberRepoIface = subscriberRepoIface;
        this.subscriberDeviceRepoIface = subscriberDeviceRepoIface;
        this.onboardingDataRepoIface = onboardingDataRepoIface;
        this.temporaryTableRepo = temporaryTableRepo;
        this.onboardingStepsRepoIface = onboardingStepsRepoIface;
        this.mqSender = mqSender;
        this.subscriberOnboardingDataRepoIface = subscriberOnboardingDataRepoIface;
        this.photoFeaturesRepo = photoFeaturesRepo;
        this.subscriberPreferencesRepo = subscriberPreferencesRepo;

        this.subscriberServiceIface = subscriberServiceIface;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    @Value("${is.onboarding.fee}")
    boolean isOnboardingFee;

    @Value("${verify.photo}")
    private Boolean verifyPhoto;

    @Value("${age.validation.enabled}")
    private boolean ageValidationEnabled;

    @Value("${age.validation.min-age}")
    private int minAge;

    @Value("${id.document.verification.enabled}")
    private boolean idDocumentVerificationEnabled;
    @Value("${signed.required.by.user}")
    private boolean signRequired;

    @Value(value = "${ind.api.sms}")
    private String indApiSMS;

    @Value("${au.log.url}")
    private String auditLogUrl;

    @Value("${extract.features}")
    private String exractFeatures;

    @Value("${find.details}")
    private String findDetails;

    @Override
    public ApiResponse saveDataTemporyTable(TemporaryTableDTO temporaryTableDTO) {
        try {
            if (Objects.isNull(temporaryTableDTO)) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
            }
            if (temporaryTableDTO.getIdDocNumber() == null || temporaryTableDTO.getIdDocNumber().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_ID_DOC_CANNOT_BE_NULL);
            }
            if (temporaryTableDTO.getStep() == 1) {
                logger.info("{}{} - Processing saveDataTemporyTable for step: {}", CLASS, Utility.getMethodName(),
                        temporaryTableDTO.getStep());
                return flag1method(temporaryTableDTO);

            } else if (temporaryTableDTO.getStep() == 3) {
                logger.info("{}{} - saveDataTemporaryTable step: {}", CLASS, Utility.getMethodName(),
                        temporaryTableDTO.getStep());
                return flag3method(temporaryTableDTO);

            } else if (temporaryTableDTO.getStep() == 4) {
                logger.info("{}{} - saveDataTemporaryTable steps: {}", CLASS, Utility.getMethodName(),
                        temporaryTableDTO.getStep());
                return flag4method(temporaryTableDTO);

            } else {
                return exceptionHandlerUtil.createErrorResponse("api.error.step.not.found");
            }

        } catch (Exception e) {
            logger.error("{}{} - Exception in saveDataTemporyTable: {}", CLASS, Utility.getMethodName(),
                    e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);
            e.getCause();
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

    public ApiResponse flag1method(TemporaryTableDTO temporaryTableDTO) {
        try {

            ApiResponse validation = validateFlag1Input(temporaryTableDTO);
            if (validation != null) {
                return validation;
            }

            OnbSubscriber subscriber = checkExistingSubscriber(temporaryTableDTO);
            if (subscriber != null) {
                return buildExistingSubscriberResponse(temporaryTableDTO, subscriber);
            }

            ApiResponse deviceCheck = validateDevice(temporaryTableDTO);
            if (deviceCheck != null) {
                return deviceCheck;
            }

            ApiResponse tempTableCheck = handleTemporaryTable(temporaryTableDTO);
            if (tempTableCheck != null) {
                return tempTableCheck;
            }

            return saveStep1Data(temporaryTableDTO);

        } catch (Exception e) {
            logger.error(EXCEPTION, CLASS, Utility.getMethodName(), e.getMessage());
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private ApiResponse validateFlag1Input(TemporaryTableDTO dto) {

        SubscriberObDetails sub = dto.getSubscriberObDataDTO();

        // Validate deviceId
        ApiResponse error = validateField(dto.getDeviceId(),
                "api.error.deviceid.cant.be.null.or.empty");
        if (error != null) return error;

        // Subscriber object validation
        if (sub == null) {
            logger.error("{}{} - Subscriber Ob data cannot be null for idDocNumber: {}",
                    CLASS, Utility.getMethodName(), dto.getIdDocNumber());
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
        }

        // Validate subscriber fields using helper
        if ((error = validateField(sub.getDocumentCode(), "api.error.doc.cant.be.null.or.empty")) != null) return error;
        if ((error = validateField(sub.getDocumentType(), "api.error.doctype.cant.be.null.or.empty")) != null)
            return error;
        if ((error = validateField(sub.getNationality(), "api.error.nationality.cant.be.null.or.empty")) != null)
            return error;
        if ((error = validateField(sub.getSubscriberType(), "api.error.subscriber.type.cant.be.null.or.empty")) != null)
            return error;
        if ((error = validateField(sub.getDateOfBirth(), "api.error.date.of.birth.cant.be.null.or.empty")) != null)
            return error;
        if ((error = validateField(sub.getDateOfExpiry(), "api.error.date.of.expiry.cant.be.null.or.empty")) != null)
            return error;
        if ((error = validateField(sub.getGeoLocation(), "api.error.geolocation.cant.be.null.or.empty")) != null)
            return error;

        return null;
    }

    private ApiResponse validateField(String value, String errorKey) {
        if (value == null || value.isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse(errorKey);
        }
        return null;
    }

    private OnbSubscriber checkExistingSubscriber(TemporaryTableDTO temporaryTableDTO) {

        List<OnbSubscriberOnboardingData> records =
                subscriberOnboardingDataRepoIface
                        .findSubscriberByDocIdLatestRecord(temporaryTableDTO.getIdDocNumber());

        OnbSubscriberOnboardingData onboardingData =
                records.isEmpty() ? null : records.getFirst();

        OnbSubscriber subscriber;

        if (onboardingData != null) {
            subscriber = subscriberRepoIface.findBysubscriberUid(onboardingData.getSubscriberUid());
        } else {
            subscriber = subscriberRepoIface.findbyDocumentNumber(temporaryTableDTO.getIdDocNumber());
        }

        return subscriber;
    }

    private ApiResponse buildExistingSubscriberResponse(
            TemporaryTableDTO temporaryTableDTO,
            OnbSubscriber subscriber) {

        try {

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

            OnbSubscriberOnboardingData subscriberOnboardingData =
                    onboardingDataRepoIface
                            .findLatestSubscriber(subscriber.getSubscriberUid())
                            .stream().findFirst().orElse(null);

            if (subscriberOnboardingData == null) {
                logger.error("{}{} - Subscriber onboarding data cannot be null for idDocNumber: {}",
                        CLASS, Utility.getMethodName(), temporaryTableDTO.getIdDocNumber());

                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.temporaryTableDTO.cannot.be.null");
            }

            temporaryResponseDto.setSelfieImage(subscriberOnboardingData.getSelfie());
            temporaryResponseDto.setSubscriber(subscriber);
            temporaryResponseDto.setExistingSubscriber(true);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.it.seems.your.already.have.an.ugpass.account.kindly.log.in.to.access.your.account",
                    temporaryResponseDto);

        } catch (Exception e) {

            logger.error(UNEXPECTED_EXCEPTION, e);
            sentryClientExceptions.captureExceptions(e);

            return exceptionHandlerUtil.handleException(e);
        }
    }

    private ApiResponse validateDevice(TemporaryTableDTO temporaryTableDTO) {

        List<OnbSubscriberDevice> devices =
                subscriberDeviceRepoIface
                        .findDeviceDetailsById(temporaryTableDTO.getDeviceId());

        OnbSubscriberDevice subscriberDevice =
                devices.isEmpty() ? null : devices.get(0);

        if (subscriberDevice != null
                && subscriberDevice.getDeviceStatus().equals("ACTIVE")) {

            logger.info("{}{} - Onboarded subscriber device details: {}",
                    CLASS, Utility.getMethodName(), subscriberDevice);

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setExistingSubscriberDevice(true);

            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.device.is.already.registered.with.onboarded.user");
        }

        return null;
    }

    private ApiResponse handleTemporaryTable(TemporaryTableDTO temporaryTableDTO) throws JsonProcessingException {

        OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(temporaryTableDTO.getIdDocNumber());

        OnbTemporaryTable temporaryTableDevice = temporaryTableRepo.getByDevice(temporaryTableDTO.getDeviceId());

        List<OnboardingStepDetails> onboardingStepDetailslist = onboardingStepsRepoIface.getAllSteps();


        if (temporaryTable != null) {

            if (temporaryTableDTO.getIdDocNumber().equals(temporaryTable.getIdDocNumber())
                    && temporaryTableDTO.getDeviceId().equals(temporaryTable.getDeviceId())) {

                logger.info(DATA_ALREADY_EXISTS, CLASS, Utility.getMethodName(),
                        temporaryTable.getIdDocNumber());

                TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

                temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());
                temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

                if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                        && !temporaryTable.getOptionalData1().equals("0")) {

                    temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());

                } else {

                    temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
                }

                SubscriberObDetails subscriberObDetails = objectMapper.readValue(
                        temporaryTable.getStep1Data(), SubscriberObDetails.class);

                temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
                temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());
                temporaryResponseDto.setStep2Status(temporaryTable.getStep2Status());
                temporaryResponseDto.setMobileNumber(temporaryTable.getStep3Data());
                temporaryResponseDto.setStep3Status(temporaryTable.getStep3Status());
                temporaryResponseDto.setEmailId(temporaryTable.getStep4Data());
                temporaryResponseDto.setStep4Status(temporaryTable.getStep4Status());
                temporaryResponseDto.setStep5Details(temporaryTable.getStep5Data());
                temporaryResponseDto.setStep5Status(temporaryTable.getStep5Status());
                temporaryResponseDto.setStepCompleted(temporaryTable.getStepCompleted());
                temporaryResponseDto.setNextStep(temporaryTable.getNextStep());
                temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);
                temporaryResponseDto.setSelfieImage(temporaryTable.getSelfie());
                temporaryResponseDto.setDataInTemporaryTable(true);

                return exceptionHandlerUtil.createSuccessResponse(API_RESPOSNE_DETAILS_FOUND,
                        temporaryResponseDto);

            } else if (temporaryTableDevice == null
                    && temporaryTableDTO.getIdDocNumber().equals(temporaryTable.getIdDocNumber())
                    && !temporaryTableDTO.getDeviceId().equals(temporaryTable.getDeviceId())) {

                TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

                temporaryResponseDto.setNewDevice(true);

                logger.info(
                        "{}{} - Data already exists in temporary table, coming with new device for idDocNumber: {}",
                        CLASS, Utility.getMethodName(), temporaryTable.getIdDocNumber());

                return exceptionHandlerUtil.createSuccessResponse(
                        "api.response.do.you.want.to.continue.on.this.new.device",
                        temporaryResponseDto);
            }
        }

        if (temporaryTableDevice != null
                && !temporaryTableDevice.getIdDocNumber().equals(temporaryTableDTO.getIdDocNumber())) {

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

            temporaryResponseDto.setUsedDevice(true);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.this.device.is.already.registered.with.different.details.use.the.same.document.to.proceed.or.delete.the.existing.data.and.try.again",
                    temporaryResponseDto);
        }

        return null;
    }

    private ApiResponse saveStep1Data(TemporaryTableDTO temporaryTableDTO) throws JsonProcessingException, PKICoreServiceException {

        String documentDetailsJson = objectMapper.writeValueAsString(
                temporaryTableDTO.getSubscriberObDataDTO());

        String deviceDetailsJson = objectMapper.writeValueAsString(
                temporaryTableDTO.getSubscriberDeviceInfoDto());

        JsonNode jsonNode1 = objectMapper.readTree(documentDetailsJson);

        ApiResponse validationResponse = validateSubscriberDetails(jsonNode1);
        if (validationResponse != null) {
            return validationResponse;
        }

        OnbTemporaryTable temporaryTable1 = new OnbTemporaryTable();
        TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

        prepareTemporaryTable(
                temporaryTableDTO,
                documentDetailsJson,
                deviceDetailsJson,
                temporaryTable1,
                temporaryResponseDto
        );

        processNextStep(
                temporaryTableDTO,
                objectMapper,
                temporaryTable1,
                temporaryResponseDto
        );

        ApiResponse niraResponse = processNiraResponse(
                temporaryTableDTO,
                objectMapper,
                temporaryTable1
        );

        if (niraResponse != null) {
            return niraResponse;
        }

        temporaryTableRepo.save(temporaryTable1);

        return exceptionHandlerUtil.createSuccessResponse(
                "api.response.details.of.step1.saved.successfully.in.temporary.table",
                temporaryResponseDto);
    }

    private ApiResponse validateSubscriberDetails(JsonNode jsonNode1) {

        String subscriberType = jsonNode1.get("subscriberType").asText();

        if (subscriberType.equals("null") || subscriberType.isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.type");
        }

        if (ageValidationEnabled) {

            LocalDate dob = AppUtil.parseToLocalDate(
                    jsonNode1.get("dateOfBirth").asText());

            int age = AppUtil.calculateAge(dob);

            if (age < minAge) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.you.cannot.onboard.Minimum.allowed.age.is.16.as.per.current.policy");
            }
        }

        if (idDocumentVerificationEnabled) {

            String doe = jsonNode1.get("dateOfExpiry").asText();

            if (doe == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.id.document.expiry.date.cannot.be.null");
            }

            LocalDate dateOfExpiry = AppUtil.parseToLocalDate(doe);
            LocalDate today = LocalDate.now();

            logger.info("Parsed Date of Expiry::: {} ", dateOfExpiry);

            if (dateOfExpiry.isBefore(today)) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.id.document.expiry.is.expired");
            }
        }

        return null;
    }

    private void prepareTemporaryTable(
            TemporaryTableDTO temporaryTableDTO,
            String documentDetailsJson,
            String deviceDetailsJson,
            OnbTemporaryTable temporaryTable1,
            TemporaryResponseDto temporaryResponseDto) {

        temporaryResponseDto.setSubscriberObDetails(
                temporaryTableDTO.getSubscriberObDataDTO());

        temporaryTable1.setDeviceInfo(deviceDetailsJson);

        temporaryResponseDto.setSubscriberDeviceInfoDto(
                temporaryTableDTO.getSubscriberDeviceInfoDto());

        temporaryTable1.setStep1Status(COMPLETED);
        temporaryResponseDto.setStep1Status(COMPLETED);

        temporaryTable1.setIdDocNumber(temporaryTableDTO.getIdDocNumber());
        temporaryResponseDto.setIdDocNumber(temporaryTable1.getIdDocNumber());

        temporaryTable1.setStep1Data(documentDetailsJson);

        temporaryTable1.setOptionalData1(temporaryTableDTO.getOptionalData1());
        temporaryResponseDto.setOptionalData1(temporaryTable1.getOptionalData1());

        temporaryTable1.setDeviceId(temporaryTableDTO.getDeviceId());
        temporaryResponseDto.setDeviceId(temporaryTable1.getDeviceId());

        temporaryTable1.setStepCompleted(temporaryTableDTO.getStep());
        temporaryResponseDto.setStepCompleted(temporaryTable1.getStepCompleted());

        temporaryTable1.setCreatedOn(AppUtil.getDate());
        temporaryTable1.setUpdatedOn(AppUtil.getDate());
    }

    private void processNextStep(
            TemporaryTableDTO temporaryTableDTO,
            ObjectMapper objectMapper,
            OnbTemporaryTable temporaryTable1,
            TemporaryResponseDto temporaryResponseDto) {
        try {

            ApiResponse res = nextStepDetails(temporaryTableDTO.getStep());

            String responseJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(res.getResult());

            OnboardingStepDetails responseDto =
                    objectMapper.readValue(responseJson, OnboardingStepDetails.class);

            if (!res.isSuccess()) {

                temporaryTable1.setNextStep(temporaryTableDTO.getStep());
                temporaryResponseDto.setNextStep(temporaryTable1.getNextStep());
            }

            temporaryTable1.setNextStep(responseDto.getStepId());

            temporaryResponseDto.setNextStep(temporaryTable1.getNextStep());

            temporaryResponseDto.setOnboardingStepDetails(
                    onboardingStepsRepoIface.getAllSteps());



        } catch (Exception e) {
            logger.error(e.getMessage());

        }

    }


    private ApiResponse processNiraResponse(
            TemporaryTableDTO temporaryTableDTO,
            ObjectMapper objectMapper,
            OnbTemporaryTable temporaryTable1) throws JsonProcessingException, PKICoreServiceException {

        if (isOnboardingFee) {

            if (temporaryTableDTO.getNiraResponse() != null &&
                    !temporaryTableDTO.getNiraResponse().trim().isEmpty()) {

                ObjectMapper ob = new ObjectMapper();
                String s = ob.writeValueAsString(temporaryTableDTO.getNiraResponse());

                Result r = DAESService.createSecureWireData(s);

                temporaryTable1.setNiraResponse(new String(r.getResponse()));
            }

        } else {

            if (temporaryTableDTO.getNiraResponse() == null ||
                    temporaryTableDTO.getNiraResponse().trim().isEmpty()) {

                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                        "api.error.nira.response.empty", null);
            }

            String json = temporaryTableDTO.getNiraResponse();

            JsonNode root = objectMapper.readTree(json);

            JsonNode dataNode = root.path("customerDetails").path("Result").path("Data");

            String passportNumber = dataNode.path("ActivePassport").path("DocumentNo").textValue();
            String emiratesIdNumber = dataNode.path("ResidenceInfo").path("EmiratesIdNumber").textValue();
            String emiratesIdDocumentNumber = dataNode.path("ResidenceInfo").path("DocumentNo").textValue();

            List<String> reasons = subscriberRepoIface.findDuplicateReason(
                    passportNumber,
                    emiratesIdNumber,
                    emiratesIdDocumentNumber
            );

            if (!reasons.isEmpty()) {

                switch (reasons.getFirst()) {

                    case "PASSPORT":
                        return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                                "api.error.passport.already.used", null);

                    case "NATIONAL_ID":
                        return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                                "api.error.emirates.id.already.used", null);

                    case "NATIONAL_ID_CARD":
                        return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                                "api.error.emirates.id.card.already.used", null);

                    default:
                        return null;
                }
            }

            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonNode = mapper.readTree(json);

            String normalizedJson = mapper.writeValueAsString(jsonNode);

            temporaryTable1.setNiraResponse(normalizedJson);
        }

        return null;
    }



	@SuppressWarnings("null")
    public ApiResponse flag2method(TemporaryTableDTO temporaryTableDTO, MultipartFile livelinessVideo, String selfie) {
        try {

            ApiResponse validation = validateFlag2Input(temporaryTableDTO, selfie);
            if (validation != null) {
                return validation;
            }

            OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(temporaryTableDTO.getIdDocNumber());
            List<OnboardingStepDetails> onboardingStepDetailslist = onboardingStepsRepoIface.getAllSteps();

            ApiResponse tableCheck = validateTemporaryTable(temporaryTable, onboardingStepDetailslist);
            if (tableCheck != null) {
                return tableCheck;
            }

            ApiResponse existingCheck = checkExistingStep2Data(temporaryTableDTO, temporaryTable, onboardingStepDetailslist);
            if (existingCheck != null) {
                return existingCheck;
            }

            return saveStep2Data(temporaryTableDTO, temporaryTable, onboardingStepDetailslist, selfie);

        } catch (Exception e) {
            logger.error("{}{} - Exception occurred in flag2method: {}", CLASS, Utility.getMethodName(), e.getMessage(), e);
            logger.error(UNEXPECTED_EXCEPTION, e);
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
    private ApiResponse validateFlag2Input(TemporaryTableDTO temporaryTableDTO, String selfie) {

        if (Objects.isNull(temporaryTableDTO)) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
        }

        if (selfie == null || selfie.isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse("api.error.selfie.cannot.be.null");
        }

        return null;
    }
    private ApiResponse validateTemporaryTable(OnbTemporaryTable temporaryTable,
                                               List<OnboardingStepDetails> onboardingStepDetailslist) {

        if (onboardingStepDetailslist == null) {
            return exceptionHandlerUtil.createErrorResponse("api.error.onboarding.steps.cannot.be.null.or.empty");
        }

        if (Objects.isNull(temporaryTable)) {
            return exceptionHandlerUtil.createErrorResponse("api.error.document.details.not.found");
        }

        return null;
    }
    private ApiResponse checkExistingStep2Data(TemporaryTableDTO temporaryTableDTO,
                                               OnbTemporaryTable temporaryTable,
                                               List<OnboardingStepDetails> onboardingStepDetailslist) throws JsonProcessingException {

        if (temporaryTable.getStepCompleted() == 2 || temporaryTable.getSelfie() != null) {

            logger.info(DATA_ALREADY_EXISTS, CLASS,
                    Utility.getMethodName(), temporaryTableDTO.getIdDocNumber());

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

            temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());
            temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

            if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                    && !temporaryTable.getOptionalData1().equals("0")) {
                temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());
            } else {
                temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
            }

            SubscriberObDetails subscriberObDetails = objectMapper.readValue(
                    temporaryTable.getStep1Data(),
                    SubscriberObDetails.class);

            temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
            temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());

            VideoDetailsDto videoDetailsDto = objectMapper.readValue(
                    temporaryTable.getStep2Data(),
                    VideoDetailsDto.class);

            temporaryResponseDto.setVideoDetailsDto(videoDetailsDto);
            temporaryResponseDto.setStep2Status(temporaryTable.getStep2Status());
            temporaryResponseDto.setMobileNumber(temporaryTable.getStep3Data());
            temporaryResponseDto.setStep3Status(temporaryTable.getStep3Status());
            temporaryResponseDto.setEmailId(temporaryTable.getStep4Data());
            temporaryResponseDto.setStep4Status(temporaryTable.getStep4Status());
            temporaryResponseDto.setStep5Details(temporaryTable.getStep5Data());
            temporaryResponseDto.setStep5Status(temporaryTable.getStep5Status());
            temporaryResponseDto.setStepCompleted(temporaryTable.getStepCompleted());
            temporaryResponseDto.setNextStep(temporaryTable.getNextStep());
            temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);
            temporaryResponseDto.setDataInTemporaryTable(true);
            temporaryResponseDto.setSelfieImage(temporaryTable.getSelfie());

            return exceptionHandlerUtil.createSuccessResponse(API_RESPOSNE_DETAILS_FOUND, temporaryResponseDto);
        }

        return null;
    }
    private ApiResponse saveStep2Data(TemporaryTableDTO temporaryTableDTO,
                                      OnbTemporaryTable temporaryTable,
                                      List<OnboardingStepDetails> onboardingStepDetailslist,
                                      String selfie) throws JsonProcessingException {

        TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

        String videoDetailsString = objectMapper.writeValueAsString(temporaryTableDTO.getVideoDetailsDto());

        temporaryTable.setStep2Status(COMPLETED);
        temporaryTable.setStep2Data(videoDetailsString);
        temporaryResponseDto.setStep2Status(COMPLETED);

        temporaryTable.setSelfie(selfie);

        temporaryTable.setUpdatedOn(AppUtil.getDate());

        ApiResponse res = nextStepDetails(temporaryTableDTO.getStep());
        String responseJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(res.getResult());

        OnboardingStepDetails responseDto = objectMapper.readValue(responseJson, OnboardingStepDetails.class);

        if (!res.isSuccess()) {
            temporaryTable.setNextStep(temporaryTableDTO.getStep());
            temporaryResponseDto.setNextStep(temporaryTable.getNextStep());
        }

        temporaryTable.setNextStep(responseDto.getStepId());
        temporaryResponseDto.setNextStep(temporaryTable.getNextStep());

        temporaryTable.setStepCompleted(temporaryTableDTO.getStep());
        temporaryResponseDto.setStepCompleted(temporaryTable.getStepCompleted());

        temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);

        temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());

        SubscriberObDetails subscriberObDetails = objectMapper.readValue(
                temporaryTable.getStep1Data(),
                SubscriberObDetails.class);

        temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
        temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());
        temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

        if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                && !temporaryTable.getOptionalData1().equals("0")) {
            temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());
        } else {
            temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
        }

        temporaryResponseDto.setCreatedOn(temporaryTable.getCreatedOn());
        temporaryResponseDto.setCreatedOn(temporaryTable.getCreatedOn());
        temporaryResponseDto.setUpdatedOn(temporaryTable.getUpdatedOn());

        temporaryTableRepo.save(temporaryTable);

        return exceptionHandlerUtil.createSuccessResponse(
                "api.response.details.of.step2.saved.successfully.temporary.table",
                temporaryResponseDto);
    }
    public ApiResponse flag3method(TemporaryTableDTO temporaryTableDTO) {
        try {

            ApiResponse validation = validateFlag3Input(temporaryTableDTO);
            if (validation != null) {
                return validation;
            }

            OnbSubscriber subscriber = subscriberRepoIface.findBymobileNumber(temporaryTableDTO.getMobileNumber());
            OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(temporaryTableDTO.getIdDocNumber());
            OnbTemporaryTable temporaryTableMobile = temporaryTableRepo
                    .getByMobNumber(temporaryTableDTO.getMobileNumber());
            List<OnboardingStepDetails> onboardingStepDetailslist = onboardingStepsRepoIface.getAllSteps();

            ApiResponse subscriberCheck = checkExistingMobileSubscriber(subscriber);
            if (subscriberCheck != null) {
                return subscriberCheck;
            }

            if (Objects.isNull(temporaryTable)) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
            }

            ApiResponse existingData = checkExistingMobileData(temporaryTableDTO, temporaryTable, onboardingStepDetailslist);
            if (existingData != null) {
                return existingData;
            }

            ApiResponse mobileCheck = checkMobileConditions(temporaryTableDTO, temporaryTable, temporaryTableMobile);
            if (mobileCheck != null) {
                return mobileCheck;
            }

            return saveStep3Data(temporaryTableDTO, temporaryTable, onboardingStepDetailslist);

        } catch (Exception e) {
            logger.error(EXCEPTION, CLASS, Utility.getMethodName(), e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
    private ApiResponse validateFlag3Input(TemporaryTableDTO temporaryTableDTO) {

        if (Objects.isNull(temporaryTableDTO)) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
        }

        if (temporaryTableDTO.getIdDocNumber() == null || temporaryTableDTO.getIdDocNumber().isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_ID_DOC_CANNOT_BE_NULL);
        }

        if (!StringUtils.hasText(temporaryTableDTO.getMobileNumber())) {
            return exceptionHandlerUtil.createErrorResponse("api.error.mobile.number.cant.be.empty");
        }

        return null;
    }
    private ApiResponse checkExistingMobileSubscriber(OnbSubscriber subscriber) {

        if (subscriber != null) {
            logger.info("{}{} - details of onboarded subscriber: {}", CLASS, Utility.getMethodName(), subscriber);

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setSubscriber(subscriber);
            temporaryResponseDto.setExistingSubscriber(true);

            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.this.mobile.number.belongs.to.onboard.user");
        }

        return null;
    }
    private ApiResponse checkExistingMobileData(TemporaryTableDTO temporaryTableDTO,
                                                OnbTemporaryTable temporaryTable,
                                                List<OnboardingStepDetails> onboardingStepDetailslist) throws JsonProcessingException {

        if (temporaryTableDTO.getIdDocNumber().equals(temporaryTable.getIdDocNumber())
                && temporaryTableDTO.getMobileNumber().equals(temporaryTable.getStep3Data())) {

            logger.info(DATA_ALREADY_EXISTS, CLASS,
                    Utility.getMethodName(), temporaryTableDTO.getIdDocNumber());

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

            temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());
            temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

            if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                    && !temporaryTable.getOptionalData1().equals("0")) {
                temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());
            } else {
                temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
            }

            SubscriberObDetails subscriberObDetails = objectMapper.readValue(
                    temporaryTable.getStep1Data(),
                    SubscriberObDetails.class);

            temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
            temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());
            temporaryResponseDto.setStep2Status(temporaryTable.getStep2Status());
            temporaryResponseDto.setMobileNumber(temporaryTable.getStep3Data());
            temporaryResponseDto.setStep3Status(temporaryTable.getStep3Status());
            temporaryResponseDto.setEmailId(temporaryTable.getStep4Data());
            temporaryResponseDto.setStep4Status(temporaryTable.getStep4Status());
            temporaryResponseDto.setStep5Details(temporaryTable.getStep5Data());
            temporaryResponseDto.setStep4Status(temporaryTable.getStep5Status());
            temporaryResponseDto.setStepCompleted(temporaryTable.getStepCompleted());
            temporaryResponseDto.setNextStep(temporaryTable.getNextStep());
            temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);
            temporaryResponseDto.setDataInTemporaryTable(true);
            temporaryResponseDto.setSelfieImage(temporaryTable.getSelfie());

            return exceptionHandlerUtil.createSuccessResponse(API_RESPOSNE_DETAILS_FOUND,
                    temporaryResponseDto);
        }

        return null;
    }
    private ApiResponse checkMobileConditions(TemporaryTableDTO temporaryTableDTO,
                                              OnbTemporaryTable temporaryTable,
                                              OnbTemporaryTable temporaryTableMobile) {

        if (temporaryTableMobile == null
                && temporaryTable.getIdDocNumber().equals(temporaryTableDTO.getIdDocNumber())
                && (temporaryTable.getStep3Data() != null
                && !temporaryTable.getStep3Data().equals(temporaryTableDTO.getMobileNumber()))) {

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setNewMobileNumber(true);

            logger.info("{}{} - Do you want to continue with this new Mobile number for idDocNumber: {}",
                    CLASS, Utility.getMethodName(), temporaryTable.getIdDocNumber());

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    "api.error.do.you.want.to.continue.with.this.new.mobile.number",
                    temporaryResponseDto);
        }

        if (temporaryTableMobile != null
                && !temporaryTableMobile.getIdDocNumber().equals(temporaryTableDTO.getIdDocNumber())) {

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setUsedMobNumber(true);

            logger.info("{}{} - This Mobile number belongs to another onboarding user for idDocNumber: {}",
                    CLASS, Utility.getMethodName(), temporaryTable.getIdDocNumber());

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    "api.error.this.mobile.number.belongs.to.onboard.user",
                    temporaryResponseDto);
        }

        return null;
    }
    private ApiResponse saveStep3Data(TemporaryTableDTO temporaryTableDTO,
                                      OnbTemporaryTable temporaryTable,
                                      List<OnboardingStepDetails> onboardingStepDetailslist) throws JsonProcessingException {

        TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

        temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());

        ObjectMapper objectmp = new ObjectMapper();

        SubscriberObDetails subscriberObDetails = objectmp.readValue(
                temporaryTable.getStep1Data(),
                SubscriberObDetails.class);

        temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
        temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());
        temporaryResponseDto.setStep2Status(temporaryTable.getStep2Status());
        temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

        if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                && !temporaryTable.getOptionalData1().equals("0")) {
            temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());
        } else {
            temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
        }

        temporaryResponseDto.setCreatedOn(temporaryTable.getCreatedOn());
        temporaryResponseDto.setUpdatedOn(temporaryTable.getUpdatedOn());

        temporaryTable.setStep3Data(temporaryTableDTO.getMobileNumber());
        temporaryResponseDto.setMobileNumber(temporaryTableDTO.getMobileNumber());

        temporaryTable.setStepCompleted(temporaryTableDTO.getStep());
        temporaryResponseDto.setStepCompleted(temporaryTableDTO.getStep());

        temporaryTable.setStep3Status(COMPLETED);
        temporaryResponseDto.setStep3Status(COMPLETED);

        ApiResponse res = nextStepDetails(temporaryTableDTO.getStep());
        String responseJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(res.getResult());

        OnboardingStepDetails responseDto = objectMapper.readValue(
                responseJson,
                OnboardingStepDetails.class);

        if (!res.isSuccess()) {
            temporaryTable.setNextStep(temporaryTableDTO.getStep());
            temporaryResponseDto.setNextStep(temporaryTableDTO.getStep());
        }

        temporaryTable.setNextStep(responseDto.getStepId());
        temporaryResponseDto.setNextStep(responseDto.getStepId());

        temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);

        temporaryTableRepo.save(temporaryTable);

        return exceptionHandlerUtil.createSuccessResponse(
                "api.response.details.of.step3.saved.successfully.in.temporary.table",
                temporaryResponseDto);
    }
    public ApiResponse flag4method(TemporaryTableDTO temporaryTableDTO) {
        try {

            ApiResponse validationResponse = validateInput(temporaryTableDTO);
            if (validationResponse != null) {
                return validationResponse;
            }

            logger.info("{}{} - flag4method EmailID: {}", CLASS, Utility.getMethodName(),
                    temporaryTableDTO.getEmailId());

            temporaryTableDTO.setEmailId(temporaryTableDTO.getEmailId().toLowerCase());

            OnbSubscriber subscriber = subscriberRepoIface.findByemailId(temporaryTableDTO.getEmailId());
            OnbTemporaryTable temporaryTableEmail = temporaryTableRepo.getByEmail(temporaryTableDTO.getEmailId());
            OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(temporaryTableDTO.getIdDocNumber());
            List<OnboardingStepDetails> onboardingStepDetailslist = onboardingStepsRepoIface.getAllSteps();

            ApiResponse subscriberCheck = checkExistingSubscriber(subscriber);
            if (subscriberCheck != null) {
                return subscriberCheck;
            }

            ApiResponse temporaryTableCheck = checkTemporaryTable(temporaryTable);
            if (temporaryTableCheck != null) {
                return temporaryTableCheck;
            }

            ApiResponse existingDataCheck = checkExistingData(temporaryTableDTO, temporaryTable, onboardingStepDetailslist);
            if (existingDataCheck != null) {
                return existingDataCheck;
            }

            ApiResponse emailCheck = checkEmailConditions(temporaryTableDTO, temporaryTable, temporaryTableEmail);
            if (emailCheck != null) {
                return emailCheck;
            }

            return saveStep4Data(temporaryTableDTO, temporaryTable, onboardingStepDetailslist);

        } catch (Exception e) {
            logger.error(EXCEPTION, CLASS, Utility.getMethodName(), e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private ApiResponse validateInput(TemporaryTableDTO temporaryTableDTO) {

        if (Objects.isNull(temporaryTableDTO)) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
        }

        if (temporaryTableDTO.getIdDocNumber() == null || temporaryTableDTO.getIdDocNumber().isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_ID_DOC_CANNOT_BE_NULL);
        }

        if (!StringUtils.hasText(temporaryTableDTO.getEmailId())) {
            return exceptionHandlerUtil.createErrorResponse("api.error.email.id.cant.be.empty");
        }

        return null;
    }
    private ApiResponse checkExistingSubscriber(OnbSubscriber subscriber) {

        if (subscriber != null) {
            logger.info("{}{} - details of onboarded subscriber: {}", CLASS, Utility.getMethodName(), subscriber);

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setSubscriber(subscriber);
            temporaryResponseDto.setExistingSubscriber(true);

            return exceptionHandlerUtil.createErrorResponse("api.error.this.email.id.belongs.to.onboard.user");
        }

        return null;
    }
    private ApiResponse checkTemporaryTable(OnbTemporaryTable temporaryTable) {

        if (temporaryTable == null) {
            return exceptionHandlerUtil.createErrorResponse("api.error.details.not.found");
        }

        if (temporaryTable.getStep3Data() == null || temporaryTable.getStep3Data().isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse("api.error.mobile.number.not.found");
        }

        return null;
    }

    private ApiResponse checkExistingData(TemporaryTableDTO temporaryTableDTO,
                                          OnbTemporaryTable temporaryTable,
                                          List<OnboardingStepDetails> onboardingStepDetailslist) throws JsonProcessingException {

        if (temporaryTableDTO.getIdDocNumber().equals(temporaryTable.getIdDocNumber())
                && temporaryTableDTO.getEmailId().equals(temporaryTable.getStep4Data())) {

            logger.info(DATA_ALREADY_EXISTS, CLASS,
                    Utility.getMethodName(), temporaryTableDTO.getIdDocNumber());

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

            temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());
            temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

            if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                    && !temporaryTable.getOptionalData1().equals("0")) {
                temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());
            } else {
                temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
            }

            SubscriberObDetails subscriberObDetails = objectMapper.readValue(temporaryTable.getStep1Data(),
                    SubscriberObDetails.class);

            temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
            temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());
            temporaryResponseDto.setStep2Status(temporaryTable.getStep2Status());
            temporaryResponseDto.setMobileNumber(temporaryTable.getStep3Data());
            temporaryResponseDto.setStep3Status(temporaryTable.getStep3Status());
            temporaryResponseDto.setEmailId(temporaryTable.getStep4Data());
            temporaryResponseDto.setStep4Status(temporaryTable.getStep4Status());
            temporaryResponseDto.setStep5Details(temporaryTable.getStep5Data());
            temporaryResponseDto.setStep4Status(temporaryTable.getStep5Status());
            temporaryResponseDto.setStepCompleted(temporaryTable.getStepCompleted());
            temporaryResponseDto.setNextStep(temporaryTable.getNextStep());
            temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);
            temporaryResponseDto.setSelfieImage(temporaryTable.getSelfie());
            temporaryResponseDto.setDataInTemporaryTable(true);

            return exceptionHandlerUtil.createSuccessResponse(API_RESPOSNE_DETAILS_FOUND, temporaryResponseDto);
        }

        return null;
    }
    private ApiResponse checkEmailConditions(TemporaryTableDTO temporaryTableDTO,
                                             OnbTemporaryTable temporaryTable,
                                             OnbTemporaryTable temporaryTableEmail) {

        if (temporaryTableEmail == null
                && temporaryTable.getIdDocNumber().equals(temporaryTableDTO.getIdDocNumber())
                && (temporaryTable.getStep4Data() != null
                && !temporaryTable.getStep4Data().equals(temporaryTableDTO.getEmailId()))) {

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setNewEmailId(true);

            logger.info("{}{} - Do you want to continue with this new Email id for idDocNumber: {}",
                    CLASS, Utility.getMethodName(), temporaryTable.getIdDocNumber());

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    "api.error.do.you.want.to.continue.with.this.new.email.id", temporaryResponseDto);
        }

        if (temporaryTableEmail != null
                && !temporaryTableEmail.getIdDocNumber().equals(temporaryTableDTO.getIdDocNumber())) {

            TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
            temporaryResponseDto.setUsedEmail(true);

            logger.info("{}{} - This email ID belongs to another onboarding user for idDocNumber: {}",
                    CLASS, Utility.getMethodName(), temporaryTable.getIdDocNumber());

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    "api.error.this.email.id.belongs.to.onboard.user", temporaryResponseDto);
        }

        return null;
    }
    private ApiResponse saveStep4Data(TemporaryTableDTO temporaryTableDTO,
                                      OnbTemporaryTable temporaryTable,
                                      List<OnboardingStepDetails> onboardingStepDetailslist) throws JsonProcessingException {

        TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();

        temporaryResponseDto.setIdDocNumber(temporaryTable.getIdDocNumber());

        SubscriberObDetails subscriberObDetails = objectMapper.readValue(
                temporaryTable.getStep1Data(),
                SubscriberObDetails.class);

        temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
        temporaryResponseDto.setStep1Status(temporaryTable.getStep1Status());
        temporaryResponseDto.setStep2Status(temporaryTable.getStep2Status());
        temporaryResponseDto.setDeviceId(temporaryTable.getDeviceId());

        if (temporaryTable.getOptionalData1() != null && !temporaryTable.getOptionalData1().isEmpty()
                && !temporaryTable.getOptionalData1().equals("0")) {
            temporaryResponseDto.setOptionalData1(temporaryTable.getOptionalData1());
        } else {
            temporaryResponseDto.setOptionalData1(temporaryTable.getIdDocNumber());
        }

        temporaryResponseDto.setCreatedOn(temporaryTable.getCreatedOn());
        temporaryResponseDto.setCreatedOn(temporaryTable.getCreatedOn());
        temporaryResponseDto.setUpdatedOn(temporaryTable.getUpdatedOn());
        temporaryResponseDto.setMobileNumber(temporaryTable.getStep3Data());
        temporaryResponseDto.setStep3Status(temporaryTable.getStep3Status());

        temporaryTable.setStep4Data(temporaryTableDTO.getEmailId());
        temporaryResponseDto.setEmailId(temporaryTable.getStep4Data());

        temporaryTable.setStep4Status(COMPLETED);
        temporaryResponseDto.setStep4Status(COMPLETED);

        temporaryTable.setStepCompleted(temporaryTableDTO.getStep());
        temporaryResponseDto.setStepCompleted(temporaryTable.getStepCompleted());

        ApiResponse res = nextStepDetails(temporaryTableDTO.getStep());

        if (!res.isSuccess()) {
            temporaryTable.setNextStep(temporaryTableDTO.getStep());
            temporaryResponseDto.setNextStep(temporaryTableDTO.getStep());
        } else {

            String responseJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(res.getResult());

            OnboardingStepDetails responseDto = objectMapper.readValue(
                    responseJson,
                    OnboardingStepDetails.class);

            temporaryTable.setNextStep(responseDto.getStepId());
            temporaryResponseDto.setNextStep(temporaryTable.getNextStep());
        }

        temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailslist);

        temporaryTableRepo.save(temporaryTable);

        return exceptionHandlerUtil.createSuccessResponse(
                "api.response.details.of.step4.saved.successfully.in.temporary.table",
                temporaryResponseDto);
    }

	@Override
	public ApiResponse submitObData(String idDocumentNumber) {
		try {
			logger.info("{}{} - Received request to submit data for idDocumentNumber: {}", CLASS,
					Utility.getMethodName(), idDocumentNumber);
			if (!StringUtils.hasText(idDocumentNumber) || "null".equalsIgnoreCase(idDocumentNumber)) {
				return exceptionHandlerUtil.createErrorResponse("api.error.id.document.number.cannot.be.null");
			}
			OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(idDocumentNumber);
			int countOfValues = onboardingStepsRepoIface.getNoOfOnboardingSteps();
			if (Objects.isNull(temporaryTable)) {
				return exceptionHandlerUtil.createErrorResponse("api.error.no.data.found.for.given.id.doc.number");
			} else if (temporaryTable.getStepCompleted() != countOfValues) {
				return exceptionHandlerUtil.createErrorResponse("api.error.please.complete.all.steps");
			}

			String featuresBase64 = null;

			if (Boolean.TRUE.equals(verifyPhoto)) {
				ApiResponse responseOfFaceVerification = verifyFaceFeatures(temporaryTable.getSelfie());
				if (!responseOfFaceVerification.isSuccess()) {
					return AppUtil.createApiResponse(false, responseOfFaceVerification.getMessage(),
							responseOfFaceVerification.getResult());
				}
				featuresBase64 = responseOfFaceVerification.getResult().toString();
			}

			String step1Json = temporaryTable.getStep1Data();

			String deviceInfo = temporaryTable.getDeviceInfo();

			MobileOTPDto mobileOTPDto = new MobileOTPDto();
			JsonNode documentDetailsJson = objectMapper.readTree(step1Json);
			JsonNode deviceDetailsJson = objectMapper.readTree(deviceInfo);

			mobileOTPDto.setSubscriberName(documentDetailsJson.get("subscriberName").asText());
			mobileOTPDto.setDeviceId(temporaryTable.getDeviceId());
			mobileOTPDto.setSubscriberMobileNumber(temporaryTable.getStep3Data());
			mobileOTPDto.setSubscriberEmail(temporaryTable.getStep4Data());
			mobileOTPDto.setFcmToken(deviceDetailsJson.get("fcmToken").asText());
			mobileOTPDto.setOtpStatus(true);
			mobileOTPDto.setOsName(deviceDetailsJson.get("osName").asText());
			mobileOTPDto.setOsVersion(deviceDetailsJson.get("osVersion").asText());
			mobileOTPDto.setAppVersion(deviceDetailsJson.get("appVersion").asText());
			mobileOTPDto.setDeviceInfo(deviceDetailsJson.get("deviceInfo").asText());
			mobileOTPDto.setIdDocNumber(idDocumentNumber);

			ApiResponse response = subscriberServiceIface.saveSubscribersData(mobileOTPDto);

			if (!response.isSuccess()) {
				logger.info(CLASS + " submitObData  saveSubscribersData: 2 {} " , response);
				subscriberServiceIface.deleteRecord("", mobileOTPDto.getSubscriberEmail());

				return response;
			}

			SubscriberRegisterResponseDTO responseDTO = (SubscriberRegisterResponseDTO) response.getResult();

			// Access the suID field
			String suID = responseDTO.getSuID();

			// saving data into photo features

			if (Boolean.TRUE.equals(verifyPhoto)) {
				byte[] decodedData = Base64.getDecoder().decode(featuresBase64);

				Blob blob = new SerialBlob(decodedData);

				OnbPhotoFeatures photoFeatures = new OnbPhotoFeatures();
				photoFeatures.setPhotoFeatures(blob);
				photoFeatures.setSuid(suID);
				photoFeatures.setCreatedOn(AppUtil.getDate());
				photoFeatures.setUpdatedOn(AppUtil.getDate());
				photoFeaturesRepo.save(photoFeatures);

			}




			SubscriberObRequestDTO subscriberObRequestDTO = createSubscriberObRequestDTO(suID, documentDetailsJson,
					temporaryTable, idDocumentNumber);
			ApiResponse res = subscriberServiceIface.addSubscriberObData(subscriberObRequestDTO);


			if (!res.isSuccess()) {
				ApiResponse deleteResponse = subscriberServiceIface.deleteRecord("", mobileOTPDto.getSubscriberEmail());
				logger.info("{}{} - deleteResponse: {}", CLASS, Utility.getMethodName(), deleteResponse);
				return res;
			}

			OnbSubscriberPreferences subscriberPreferences = new OnbSubscriberPreferences();
			Locale locale = LocaleContextHolder.getLocale();
			subscriberPreferences.setSuid(suID);
			subscriberPreferences.setLanguagePreferred(locale.getLanguage());
			subscriberPreferences.setCreatedOn(AppUtil.getDate());
			subscriberPreferences.setUpdatedOn(AppUtil.getDate());
			subscriberPreferencesRepo.save(subscriberPreferences);


			int deleteValue = temporaryTableRepo.deleteRecordByIdDocumentNumber(idDocumentNumber);
			if (deleteValue != 1) {
				logger.info("{}{} - deleteValue: {}", CLASS, Utility.getMethodName(), deleteValue);
				return exceptionHandlerUtil.createErrorResponse("api.error.Record.not.deleted.from.temporary.table");
			}
			return exceptionHandlerUtil.createSuccessResponseWithCustomMessage(res.getMessage(), res.getResult());
		} catch (Exception e) {
			logger.error( UNEXPECTED_EXCEPTION, e);
			logger.error("{}{} - submitObData Exception : {}", CLASS, Utility.getMethodName(), e);
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public SubscriberObRequestDTO createSubscriberObRequestDTO(String suID, JsonNode documentDetailsJson,
			OnbTemporaryTable temporaryTable, String idDocumentNumber) {
		try {
			SubscriberObRequestDTO subscriberObRequestDTO = new SubscriberObRequestDTO();
			subscriberObRequestDTO.setSuID(suID);
			subscriberObRequestDTO.setOnboardingMethod(documentDetailsJson.get("onboardingMethod").asText());
			subscriberObRequestDTO.setTemplateId(documentDetailsJson.get("templateID").asInt());
			subscriberObRequestDTO.setSubscriberType(documentDetailsJson.get("subscriberType").asText());
			subscriberObRequestDTO.setNiraResponse(temporaryTable.getNiraResponse());

			SubscriberObData subscriberObData = new SubscriberObData();
			subscriberObData.setDateOfBirth(documentDetailsJson.get("dateOfBirth").asText());
			subscriberObData.setDateOfExpiry(documentDetailsJson.get("dateOfExpiry").asText());
			subscriberObData.setNationality(documentDetailsJson.get("nationality").asText());
			subscriberObData.setGender(documentDetailsJson.get("gender").asText());
			subscriberObData.setPrimaryIdentifier(documentDetailsJson.get("primaryIdentifier").asText());
			subscriberObData.setSecondaryIdentifier(documentDetailsJson.get("secondaryIdentifier").asText());
			subscriberObData.setDocumentType(documentDetailsJson.get("documentType").asText());
			subscriberObData.setDocumentCode(documentDetailsJson.get("documentCode").asText());
			subscriberObData.setOptionalData1(documentDetailsJson.get("optionalData1").asText());
			subscriberObData.setOptionalData2(documentDetailsJson.get("optionalData2").asText());
			subscriberObData.setDocumentNumber(idDocumentNumber);
			subscriberObData.setIssuingState(documentDetailsJson.get("issuingState").asText());
			subscriberObData.setSubscriberSelfie(temporaryTable.getSelfie());
			subscriberObData.setGeoLocation(documentDetailsJson.get("geoLocation").asText());
			subscriberObData.setRemarks(documentDetailsJson.get("remarks").asText());
			subscriberObData.setSubscriberUniqueId(suID);

			subscriberObData.setNiraResponse(temporaryTable.getNiraResponse());
			subscriberObRequestDTO.setSubscriberData(subscriberObData);

			return subscriberObRequestDTO;
		} catch (Exception e) {
			logger.error( UNEXPECTED_EXCEPTION, e);
			return null;
		}

	}

	public FileUploadDTO populateFileUploadDTO(String step2Json, String suID) throws IOException {
		FileUploadDTO videoUploadReq = objectMapper.readValue(step2Json, FileUploadDTO.class);

		FileUploadDTO fileUploadDTO = new FileUploadDTO();
		fileUploadDTO.setSubscriberUid(suID);
		fileUploadDTO.setRecordedTime(videoUploadReq.getRecordedTime());
		fileUploadDTO.setRecordedGeoLocation(videoUploadReq.getRecordedGeoLocation());
		fileUploadDTO.setVerificationFirst(videoUploadReq.getVerificationFirst());
		fileUploadDTO.setVerificationSecond(videoUploadReq.getVerificationSecond());
		fileUploadDTO.setVerificationThird(videoUploadReq.getVerificationThird());
		fileUploadDTO.setTypeOfService(videoUploadReq.getTypeOfService());
		return fileUploadDTO;
	}


	public String generateSubscriberUniqueId() {
		UUID uuid = UUID.randomUUID();
		logger.info(CLASS + "Generate Subscriber UniqueId {}", uuid);
		return uuid.toString();
	}

	int isOptionData1Present(String optionalData1) {
		return onboardingDataRepoIface.getOptionalData1(optionalData1);

	}

	public void setLogModel(Boolean response, OnbSubscriber subscriber, String geoLocation)
			 {
		logger.info(CLASS + "Set LogModel {} and subscriber {} and geoLocation {}", response, subscriber, geoLocation);
		LogModelDTO logModel = new LogModelDTO();
		logModel.setIdentifier(subscriber.getSubscriberUid());
		logModel.setCorrelationID(generateSubscriberUniqueId());
		logModel.setTransactionID(generateSubscriberUniqueId());
		logModel.setTimestamp(null);
		logModel.setStartTime(getTimeStampString());
		logModel.setEndTime(getTimeStampString());
		logModel.setServiceName(ServiceNames.SUBSCRIBER_ONBOARDED.toString());
		logModel.setLogMessage("RESPONSE");
		logModel.setTransactionType(TransactionType.BUSINESS.toString());
		logModel.setGeoLocation(geoLocation);
		logModel.seteSealUsed(false);
		logModel.setSignatureType(null);

        if (Boolean.TRUE.equals(response)) {
			logModel.setLogMessageType(LogMessageType.SUCCESS.toString());
		} else {
			logModel.setLogMessageType(LogMessageType.FAILURE.toString());
		}
		logModel.setChecksum(null);

		try {


			String json = objectMapper.writeValueAsString(logModel);
			Result checksumResult = DAESService.addChecksumToTransaction(json);
			String push = new String(checksumResult.getResponse());
			LogModelDTO log = objectMapper.readValue(push, LogModelDTO.class);
			mqSender.send(log);
		} catch (Exception e) {
			logger.error("Set LogModel Exception {}", e.getMessage());
			logger.error( UNEXPECTED_EXCEPTION, e);
		}
	}

	private String getTimeStampString()  {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return f.format(new Date());
	}

	public ApiResponse nextStepDetails(int currentStepId) {
		try {
			int countNoOfSteps = onboardingStepsRepoIface.getNoOfOnboardingSteps();
			if (countNoOfSteps == currentStepId) {
				return AppUtil.createApiResponse(false, "Last Step", countNoOfSteps);
			}
			OnboardingStepDetails onboardingSteps = onboardingStepsRepoIface.getStepDetails(currentStepId + 1);
			return exceptionHandlerUtil.createSuccessResponse("api.response.next.step.details", onboardingSteps);
		} catch (Exception e) {
			logger.error( UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}

	@Override
	public ApiResponse updateRecord(UpdateTemporaryTableDto updateTemporaryTableDto) {
		try {
			logger.info("{}{} - Request for update record: {}", CLASS, Utility.getMethodName(),
					updateTemporaryTableDto);
			if (updateTemporaryTableDto.getIdDocNumber() == null
					|| updateTemporaryTableDto.getIdDocNumber().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_ID_DOC_CANNOT_BE_NULL);
			}
			List<OnboardingStepDetails> onboardingStepDetailsList = onboardingStepsRepoIface.getAllSteps();
			if (updateTemporaryTableDto.getSubscriberDeviceInfoDto() != null) {
				OnbTemporaryTable temporaryTable1 = temporaryTableRepo
						.getbyidDocNumber(updateTemporaryTableDto.getIdDocNumber());

				if (Objects.isNull(temporaryTable1)) {
					return exceptionHandlerUtil
							.createErrorResponse(API_ERROR_NO_RECORD_FOUND_FOR_ID_DOC);
				}

				String deviceDetailsJson = objectMapper
						.writeValueAsString(updateTemporaryTableDto.getSubscriberDeviceInfoDto());
				JsonNode jsonNode = objectMapper.readTree(deviceDetailsJson);
				String deviceId = jsonNode.get("deviceId").asText();

				temporaryTable1.setDeviceId(deviceId);
				temporaryTable1.setDeviceInfo(deviceDetailsJson);

				TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
				temporaryResponseDto.setIdDocNumber(temporaryTable1.getIdDocNumber());
				SubscriberObDetails subscriberObDetails = objectMapper.readValue(temporaryTable1.getStep1Data(),
						SubscriberObDetails.class);
				temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
				temporaryResponseDto.setStep1Status(temporaryTable1.getStep1Status());
				temporaryResponseDto.setStep2Status(temporaryTable1.getStep2Status());
				temporaryResponseDto.setDeviceId(temporaryTable1.getDeviceId());
				SubscriberDeviceInfoDto subscriberDeviceInfoDto = objectMapper
						.readValue(temporaryTable1.getDeviceInfo(), SubscriberDeviceInfoDto.class);
				temporaryResponseDto.setSubscriberDeviceInfoDto(subscriberDeviceInfoDto);
				temporaryResponseDto.setOptionalData1(temporaryTable1.getOptionalData1());
				temporaryResponseDto.setCreatedOn(temporaryTable1.getCreatedOn());
				temporaryResponseDto.setCreatedOn(temporaryTable1.getCreatedOn());
				temporaryResponseDto.setUpdatedOn(temporaryTable1.getUpdatedOn());
				temporaryResponseDto.setMobileNumber(temporaryTable1.getStep3Data());
				temporaryResponseDto.setStep3Status(temporaryTable1.getStep3Status());

				temporaryResponseDto.setStepCompleted(temporaryTable1.getStepCompleted());
				temporaryResponseDto.setNextStep(temporaryTable1.getNextStep());
				temporaryResponseDto.setStep4Status(temporaryTable1.getStep4Status());
				temporaryResponseDto.setEmailId(temporaryTable1.getStep4Data());
				temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailsList);

				temporaryTableRepo.save(temporaryTable1);
				return exceptionHandlerUtil.createSuccessResponse("api.response.device.updated.successfully",
						temporaryResponseDto);
			} else if (updateTemporaryTableDto.getMobileNumber() != null
					|| !updateTemporaryTableDto.getMobileNumber().isEmpty()) {
				OnbTemporaryTable temporaryTable1 = temporaryTableRepo
						.getbyidDocNumber(updateTemporaryTableDto.getIdDocNumber());

				if (Objects.isNull(temporaryTable1)) {
					return exceptionHandlerUtil
							.createErrorResponse(API_ERROR_NO_RECORD_FOUND_FOR_ID_DOC);
				}
				temporaryTable1.setStep3Data(updateTemporaryTableDto.getMobileNumber());

				TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
				temporaryResponseDto.setIdDocNumber(temporaryTable1.getIdDocNumber());
				SubscriberObDetails subscriberObDetails = objectMapper.readValue(temporaryTable1.getStep1Data(),
						SubscriberObDetails.class);
				temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
				temporaryResponseDto.setStep1Status(temporaryTable1.getStep1Status());
				temporaryResponseDto.setStep2Status(temporaryTable1.getStep2Status());
				temporaryResponseDto.setDeviceId(temporaryTable1.getDeviceId());
				SubscriberDeviceInfoDto subscriberDeviceInfoDto = objectMapper
						.readValue(temporaryTable1.getDeviceInfo(), SubscriberDeviceInfoDto.class);
				temporaryResponseDto.setSubscriberDeviceInfoDto(subscriberDeviceInfoDto);
				temporaryResponseDto.setOptionalData1(temporaryTable1.getOptionalData1());
				temporaryResponseDto.setCreatedOn(temporaryTable1.getCreatedOn());
				temporaryResponseDto.setCreatedOn(temporaryTable1.getCreatedOn());
				temporaryResponseDto.setUpdatedOn(temporaryTable1.getUpdatedOn());
				temporaryResponseDto.setMobileNumber(temporaryTable1.getStep3Data());
				temporaryResponseDto.setStep3Status(temporaryTable1.getStep3Status());

				temporaryResponseDto.setStepCompleted(temporaryTable1.getStepCompleted());
				temporaryResponseDto.setStep4Status(temporaryTable1.getStep4Status());
				temporaryResponseDto.setEmailId(temporaryTable1.getStep4Data());
				temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailsList);

				temporaryTableRepo.save(temporaryTable1);
				return exceptionHandlerUtil.createSuccessResponse("api.response.mobile.number.updated.successfully",
						temporaryResponseDto);
			} else if (updateTemporaryTableDto.getEmailId() != null
					|| !updateTemporaryTableDto.getEmailId().isEmpty()) {
				OnbTemporaryTable temporaryTable1 = temporaryTableRepo
						.getbyidDocNumber(updateTemporaryTableDto.getIdDocNumber());

				if (Objects.isNull(temporaryTable1)) {
					return exceptionHandlerUtil
							.createErrorResponse(API_ERROR_NO_RECORD_FOUND_FOR_ID_DOC);

				}
				temporaryTable1.setStep4Data(updateTemporaryTableDto.getEmailId());

				TemporaryResponseDto temporaryResponseDto = new TemporaryResponseDto();
				temporaryResponseDto.setIdDocNumber(temporaryTable1.getIdDocNumber());
				SubscriberObDetails subscriberObDetails = objectMapper.readValue(temporaryTable1.getStep1Data(),
						SubscriberObDetails.class);
				temporaryResponseDto.setSubscriberObDetails(subscriberObDetails);
				temporaryResponseDto.setStep1Status(temporaryTable1.getStep1Status());
				temporaryResponseDto.setStep2Status(temporaryTable1.getStep2Status());
				temporaryResponseDto.setDeviceId(temporaryTable1.getDeviceId());
				SubscriberDeviceInfoDto subscriberDeviceInfoDto = objectMapper
						.readValue(temporaryTable1.getDeviceInfo(), SubscriberDeviceInfoDto.class);
				temporaryResponseDto.setSubscriberDeviceInfoDto(subscriberDeviceInfoDto);
				temporaryResponseDto.setOptionalData1(temporaryTable1.getOptionalData1());
				temporaryResponseDto.setCreatedOn(temporaryTable1.getCreatedOn());
				temporaryResponseDto.setCreatedOn(temporaryTable1.getCreatedOn());
				temporaryResponseDto.setUpdatedOn(temporaryTable1.getUpdatedOn());
				temporaryResponseDto.setMobileNumber(temporaryTable1.getStep3Data());
				temporaryResponseDto.setStep3Status(temporaryTable1.getStep3Status());

				temporaryResponseDto.setStepCompleted(temporaryTable1.getStepCompleted());
				temporaryResponseDto.setStep4Status(temporaryTable1.getStep4Status());
				temporaryResponseDto.setEmailId(temporaryTable1.getStep4Data());
				temporaryResponseDto.setOnboardingStepDetails(onboardingStepDetailsList);

				temporaryTableRepo.save(temporaryTable1);
				return exceptionHandlerUtil.createSuccessResponse("api.response.email.id.updated.successfully",
						temporaryResponseDto);
			}
			return exceptionHandlerUtil.createErrorResponse("api.error.update.record.type.not.valid");
		} catch (Exception e) {
			logger.error( UNEXPECTED_EXCEPTION, e);
			logger.error("{}{} - Exception for update record: {}", CLASS, Utility.getMethodName(), e);
			return exceptionHandlerUtil.handleException(e);

		}
	}
    @Override
    public ApiResponse deleteRecord(UpdateTemporaryTableDto dto) {
        try {

            if (dto.getMobileNumber() != null) {
                OnbTemporaryTable temptable = temporaryTableRepo.getByMobNumber(dto.getMobileNumber());
                if (temptable == null) {
                    return exceptionHandlerUtil
                            .createErrorResponse("api.error.there.is.no.record.with.this.mobile.number");
                }
                return deleteAndRespond(dto.getMobileNumber(), null, null);
            }

            if (dto.getEmailId() != null) {
                OnbTemporaryTable table = temporaryTableRepo.getByEmail(dto.getEmailId());
                if (table == null) {
                    return exceptionHandlerUtil
                            .createErrorResponse("api.error.there.is.no.record.with.this.email.id");
                }
                return deleteAndRespond(null, dto.getEmailId(), null);
            }

            if (dto.getDeviceId() != null) {
                OnbTemporaryTable table1 = temporaryTableRepo.getByDevice(dto.getDeviceId());
                if (table1 == null) {
                    return exceptionHandlerUtil
                            .createErrorResponse("api.error.There.is.no.record.with.this.device.id");
                }
                return deleteAndRespond(null, null, dto.getDeviceId());
            }

            return exceptionHandlerUtil
                    .createErrorResponse("api.error.something.went.wrong.please.try.after.sometime");

        } catch (Exception e) {
            logger.error(UNEXPECTED_EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private ApiResponse deleteAndRespond(String mobile, String email, String deviceId) {

        int result = temporaryTableRepo.deleteRecord(mobile, email, deviceId);

        if (result != 1) {
            return exceptionHandlerUtil
                    .createErrorResponse(API_TEMP_TABLE_RECORD_IS_NOT_DELETED_BY_USING_DEVICE_ID);
        }

        return exceptionHandlerUtil
                .successResponse(API_RESPOSNE_TEMP_TABLE_RECORD_DELETED_SUCCESSFULLY);
    }

	public static File convert(MultipartFile file) {

		String tomcatBasePath = System.getProperty("catalina.home");
		// Create a File object representing the folder
		File folder = new File(tomcatBasePath, "ObTempFiles");
		File convFile = new File(folder.getAbsolutePath() + File.separator + file.getOriginalFilename());
		// Check if the folder already exists
		if (folder.exists()) {
			logger.info("Folder already exists. PATH :: {} " , folder.getAbsolutePath());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
				logger.error( UNEXPECTED_EXCEPTION, e);
			}
			return convFile;
		} else {
			// Create the folder
			boolean created = folder.mkdir();
			// Check if the folder creation was successful
			if (created) {
				logger.info("Folder created successfully. PATH :: {}" , folder.getAbsolutePath());
			} else {
				logger.info("Failed to create the folder.");
			}
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
				logger.error( UNEXPECTED_EXCEPTION, e);
			}
			return convFile;
		}
	}

	@Override
	public ApiResponse saveStep2Details(TemporaryTableDTO temporaryTableDTO, MultipartFile livelinessVideo,
			String selfie) {
		try {
			if (Objects.isNull(temporaryTableDTO)) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
			}
			if (temporaryTableDTO.getIdDocNumber() == null || temporaryTableDTO.getIdDocNumber().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_ID_DOC_CANNOT_BE_NULL);
			}
			if (temporaryTableDTO.getStep() == 2) {
				return flag2method(temporaryTableDTO, livelinessVideo, selfie);
			} else {
				return exceptionHandlerUtil.createErrorResponse("api.error.step.not.found");
			}
		} catch (Exception e) {
			logger.error("{}{} - getOnBoardingSteps Exception: {}", CLASS, Utility.getMethodName(), e.getMessage());
			logger.error( UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Scheduled(cron = "0 0 0 * * ?")
	@Override
	public void deleteOldRecords() {
		try {
			logger.info("{}{} - deleteOldRecords corn job Started", CLASS, Utility.getMethodName());
			List<OnbTemporaryTable> records = temporaryTableRepo.findAll();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			for (OnbTemporaryTable table : records) {
				if (table.getUpdatedOn() != null) {
					LocalDateTime updatedOn = LocalDateTime.parse(table.getUpdatedOn(), formatter);
					LocalDateTime threshold = now.minusHours(24);
					if (updatedOn.isBefore(threshold) || updatedOn.isEqual(threshold)) {
						temporaryTableRepo.deleteRecordByIdDocumentNumber(table.getIdDocNumber());
					}
				}
			}
            logger.info("{}{} - deleteOldRecords record deleted", CLASS, Utility.getMethodName());
		} catch (Exception e) {
			logger.error( UNEXPECTED_EXCEPTION, e);
			logger.error("{}{} - deleteOldRecords Exception: {}", CLASS, Utility.getMethodName(), e.getMessage());
		}
	}

	// Api to save face features into database in photo_features table
    @Override
    public ApiResponse getAllSubscriberExtractFeatures() {

        try {
            List<OnbSubscriberOnboardingData> subscriberOnboardingDataList =
                    subscriberOnboardingDataRepoIface.getAllSelfies();

            for (OnbSubscriberOnboardingData data : subscriberOnboardingDataList) {
                ApiResponse processResponse = processSingleSubscriber(data);
                if (!processResponse.isSuccess()) {
                    return processResponse;
                }
            }

            return exceptionHandlerUtil
                    .createSuccessResponse("api.response.features.extracted", null);

        } catch (Exception e) {
            logger.error(CLASS + " getAllSubscriberExtractFeatures Exception {}", e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private ApiResponse processSingleSubscriber(OnbSubscriberOnboardingData subscriberData) {

        try {
            ApiResponse response = externalEdmsApi(subscriberData.getSelfieUri());
            ApiResponse featureResponse = extractFeatchersPython(response.getResult().toString());

            if (!featureResponse.isSuccess()) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.response.from.facefeature.python.api.is.negative"
                );
            }

            String featuresBase64 = featureResponse.getResult().toString();
            byte[] decodedData = Base64.getDecoder().decode(featuresBase64);

            Blob blob = new SerialBlob(decodedData);

            OnbPhotoFeatures photoFeatures = new OnbPhotoFeatures();
            photoFeatures.setSuid(subscriberData.getSubscriberUid());
            photoFeatures.setPhotoFeatures(blob);
            photoFeatures.setCreatedOn(AppUtil.getDate());
            photoFeatures.setUpdatedOn(AppUtil.getDate());

            photoFeaturesRepo.save(photoFeatures);

            return exceptionHandlerUtil.createSuccessResponse(null, null);

        } catch (Exception e) {
            logger.error(CLASS + " processSingleSubscriber Exception {}", e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

	// Python api called to fetch face featues
	public ApiResponse extractFeatchersPython(String subscriberPhoto) {
		try {
			HttpHeaders headers = new HttpHeaders();

			ExtractFeatureInputDto extractFeatureInputDto = new ExtractFeatureInputDto();
			extractFeatureInputDto.setSubscriberPhoto(subscriberPhoto);
			HttpEntity<Object> request = new HttpEntity<>(extractFeatureInputDto, headers);

			ResponseEntity<ApiResponse> response = restTemplate.exchange(exractFeatures, HttpMethod.POST, request,
					ApiResponse.class);

            var body = response.getBody();

            if (body == null || !body.isSuccess()) {
                return exceptionHandlerUtil.createErrorResponse("api.error.Extract.feature.python.api.failed");
            }

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.features.extracted.successfully",
                    body.getResult());

		} catch (Exception e) {
			logger.error(CLASS + " extractFeaturesPython Exception {}", e.getMessage());
			logger.error( UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}

	// api to fetch subscriber selfie base 64 from edms
	public ApiResponse externalEdmsApi(String edmsUrl) {
            try {
                logger.info("{}{} - externalEdmsApi request: {}", CLASS, Utility.getMethodName(), edmsUrl);
                HttpHeaders head = new HttpHeaders();
                HttpEntity<Object> request = new HttpEntity<>(head);
                ResponseEntity<byte[]> resp = restTemplate.exchange(edmsUrl, HttpMethod.GET, request, byte[].class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    String selfieBase64 = AppUtil.getBase64FromByteArr(resp.getBody());
                    return exceptionHandlerUtil.createSuccessResponse("api.response.edms.selfie", selfieBase64);
                } else {
                    return exceptionHandlerUtil.createErrorResponse("api.error.edms.selfie.fetchednot.fetched");
                }
            } catch (Exception e) {
                logger.error(UNEXPECTED_EXCEPTION, e);
                return exceptionHandlerUtil.handleException(e);
            }
        }


	public ApiResponse verifyFaceFeatures(String selfieBase64) {
		try {
			ApiResponse response1 = findDetails(selfieBase64);
			if (!response1.isSuccess()) {
				return exceptionHandlerUtil.createFailedResponseWithCustomMessage(response1.getMessage(),
						response1.getResult());
			}
			return exceptionHandlerUtil.createSuccessResponseWithCustomMessage(response1.getMessage(),
					response1.getResult());

		} catch (Exception e) {
			logger.error("{}{} - Exception occurred in verifyFaceFeatures: {}", CLASS, Utility.getMethodName(),
					e.getMessage(), e);
				logger.error( UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public ApiResponse findDetails(String subscriberPhoto) {
		try {
			HttpHeaders headers = new HttpHeaders();
			ExtractFeatureInputDto extractFeatureInputDto = new ExtractFeatureInputDto();
			extractFeatureInputDto.setImage(subscriberPhoto);
			HttpEntity<Object> request = new HttpEntity<>(extractFeatureInputDto, headers);

			ResponseEntity<ApiResponse> response = restTemplate.exchange(findDetails, HttpMethod.POST, request,
					ApiResponse.class);

            var body = response.getBody();

            if (body == null || !body.isSuccess()) {
                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                        body != null ? body.getMessage() : "Empty response body",
                        body != null ? body.getResult() : null);
            }

            return exceptionHandlerUtil.createSuccessResponseWithCustomMessage(
                    body.getMessage(), body.getResult());
		} catch (Exception e) {
			logger.error("{}{} - Exception occurred in extractFeatchersPython: {}", CLASS, Utility.getMethodName(),
					e.getMessage(), e);
				logger.error( UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}

	@Override
	public ApiResponse encriptedString(TemporaryTableDTO temporaryTableDTO) {
		try {
			Result r = DAESService.createSecureWireData(temporaryTableDTO.getNiraResponse());
			String decryptedString = new String(r.getResponse());
			Result result = DAESService.decryptSecureWireData(decryptedString);
			String ss = new String(result.getResponse());
			return exceptionHandlerUtil.createSuccessResponseWithCustomMessage("Success", ss);

		} catch (Exception e) {
			logger.error(CLASS + "extractFeatchersPython Exception {}", e.getMessage());
				logger.error( UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse niraResponse(String docNumber) {
		try {


			OnbSubscriberOnboardingData temporaryTable = onboardingDataRepoIface
					.findLatestSubscriber(subscriber.getSubscriberUid()).stream().findFirst().orElse(null);

			if (temporaryTable != null) {
				Result result = DAESService.decryptSecureWireData(temporaryTable.getNiraResponse());
				String s = new String(result.getResponse());
				return AppUtil.createApiResponse(true, "Success", s);
			} else {
				return AppUtil.createApiResponse(false, "no record found", null);
			}

		} catch (Exception e) {
				logger.error( UNEXPECTED_EXCEPTION, e);
			return AppUtil.createApiResponse(false, "falied", null);
		}
	}

	public ApiResponse validationsForName(TemporaryTableDTO temporaryTableDTO) {
		try {
			logger.info(" IN validationsForName");
			SubscriberObDetails subscriber = temporaryTableDTO.getSubscriberObDataDTO();
			logger.info(" IN subscriber.getSubscriberName():  {}" , subscriber.getSubscriberName());
			if (!subscriber.getSubscriberName().matches("^[a-zA-Z\\s]+$")) {
				logger.info(" IN subscriber.getSubscriberName(): {}" , subscriber.getSubscriberName());
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.name.not.valid");
			}

			if (!subscriber.getNationality().matches("^[a-zA-Z]+$")) {
			logger.info(" IN subscriber.getNationality(): {}" , subscriber.getNationality());
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.nationality.not.valid");
			}
			if (!subscriber.getGender().matches("^[a-zA-Z]+$")) {
				logger.info(" IN subscriber.getGender(): {}" , subscriber.getGender());
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.gender.not.valid");
			}
			// Parse the issueDate and dateOfExpiry to LocalDate
			LocalDate expiryDate = AppUtil
					.parseDateWithoutTime(temporaryTableDTO.getSubscriberObDataDTO().getDateOfExpiry());
			LocalDate currentDate = LocalDate.now(); // Current date for validation

			// Check if the expiry date is after the current date and after the issue date
			if (expiryDate.isBefore(currentDate)) {
				logger.error("validationsForName(): Expiry date cannot be before today.");
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.date.of.expiry.not.valid");
			}

			LocalDate dateOfbirth = AppUtil
					.parseDateWithoutTime(temporaryTableDTO.getSubscriberObDataDTO().getDateOfBirth());

			// Check if the expiry date is after the current date and after the issue date
			if (dateOfbirth.isAfter(currentDate)) {
				logger.error("validationsForName(): Date of Birth cannot be after today.");
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.date.of.birth.not.valid");
			}
			return exceptionHandlerUtil.createSuccessResponse("api.response.all.validations.are.passed", null);
		} catch (NullPointerException npe) {
			logger.error(CLASS + " validations() NullPointerException: {}", npe.getMessage(), npe);
			return exceptionHandlerUtil.handleException(npe);
		} catch (DateTimeParseException dtpe) {
			logger.error(CLASS + " validations() DateTimeParseException: {}", dtpe.getMessage(), dtpe);
			return exceptionHandlerUtil.handleException(dtpe);
		} catch (IllegalArgumentException iae) {
			logger.error(CLASS + " validations() IllegalArgumentException: {}", iae.getMessage(), iae);
			return exceptionHandlerUtil.handleException(iae);
		} catch (Exception e) {
			logger.error(CLASS + " validations() Unexpected Exception: {}", e.getMessage(), e);
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);
		}
	}


    @Override
    public ApiResponse checkMobileOrEmail(TemporaryTableDTO temporaryTableDTO) {
        try {
            if (isMobileAndEmailAbsent(temporaryTableDTO)) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_TEMP_TABLE_DTO_CANNOT_BE_NULL);
            }

            ApiResponse mobileResponse = processMobileCheck(temporaryTableDTO);
            if (mobileResponse != null) return mobileResponse;

            ApiResponse emailResponse = processEmailCheck(temporaryTableDTO);
            if (emailResponse != null) return emailResponse;

            return exceptionHandlerUtil.createSuccessResponse("api.response.email.mobile.validated", null);

        } catch (Exception e) {
            logger.error(CLASS + "check Mobile or Email Exception {}", e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }


    private boolean isMobileAndEmailAbsent(TemporaryTableDTO dto) {
        return (dto.getMobileNumber() == null || dto.getMobileNumber().isBlank())
                && (dto.getEmailId() == null || dto.getEmailId().isBlank());
    }



    private ApiResponse processMobileCheck(TemporaryTableDTO dto) {
        try {
            if (dto.getMobileNumber() == null || dto.getMobileNumber().isEmpty()) return null;

            OnbSubscriber subscriber = subscriberRepoIface.findBymobileNumber(dto.getMobileNumber());
            OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(dto.getIdDocNumber());
            OnbTemporaryTable temporaryTableMobile = temporaryTableRepo.getByMobNumber(dto.getMobileNumber());
            List<OnboardingStepDetails> steps = onboardingStepsRepoIface.getAllSteps();

            ApiResponse subscriberCheck = checkExistingMobileSubscriber(subscriber);
            if (subscriberCheck != null) return subscriberCheck;

            ApiResponse existingData = checkExistingMobileData(dto, temporaryTable, steps);
            if (existingData != null) return existingData;

            return checkMobileConditions(dto, temporaryTable, temporaryTableMobile);
        }catch (Exception e) {
            return null;
        }
    }



    private ApiResponse processEmailCheck(TemporaryTableDTO dto) {
        try {
            if (dto.getEmailId() == null || dto.getEmailId().isEmpty()) return null;

            OnbSubscriber subscriber = subscriberRepoIface.findByemailId(dto.getEmailId());
            OnbTemporaryTable temporaryTableEmail = temporaryTableRepo.getByEmail(dto.getEmailId());
            OnbTemporaryTable temporaryTable = temporaryTableRepo.getbyidDocNumber(dto.getIdDocNumber());
            List<OnboardingStepDetails> steps = onboardingStepsRepoIface.getAllSteps();

            ApiResponse subscriberCheck = checkExistingSubscriber(subscriber);
            if (subscriberCheck != null) return subscriberCheck;

            ApiResponse temporaryTableCheck = checkTemporaryTable(temporaryTable);
            if (temporaryTableCheck != null) return temporaryTableCheck;

            ApiResponse existingDataCheck = checkExistingData(dto, temporaryTable, steps);
            if (existingDataCheck != null) return existingDataCheck;

            return checkEmailConditions(dto, temporaryTable, temporaryTableEmail);
        } catch (Exception e) {
            return null;
        }
    }
}
