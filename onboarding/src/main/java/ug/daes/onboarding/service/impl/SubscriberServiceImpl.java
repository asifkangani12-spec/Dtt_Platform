
package ug.daes.onboarding.service.impl;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.common.util.Utility;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ug.daes.DAESService;
import ug.daes.Result;
import ug.daes.onboarding.config.OnboardingSentryClientExceptions;
import ug.daes.onboarding.constant.Constant;
import ug.daes.onboarding.dto.*;
import ug.daes.onboarding.enums.LogMessageType;
import ug.daes.onboarding.enums.ServiceNames;
import ug.daes.onboarding.enums.TransactionType;
import ug.daes.onboarding.exceptions.ApplicationException;
import ug.daes.onboarding.exceptions.OnBoardingServiceException;
import ug.daes.onboarding.model.*;
import ug.daes.onboarding.repository.*;
import ug.daes.onboarding.service.iface.DeviceUpdateIface;
import ug.daes.onboarding.service.iface.SubscriberServiceIface;

import ug.daes.onboarding.util.*;



@Primary
@Service
public class SubscriberServiceImpl implements SubscriberServiceIface {

	private static final Logger logger = LoggerFactory.getLogger(SubscriberServiceImpl.class);

	private static final String CLASS = "SubscriberServiceImpl";
    private static final String API_ERROR_SOMETHING_WENT_WRONG =  "api.error.something.went.wrong.please.try.after.sometime";
    private static final String DEVICEID = "deviceId";
    private static final String UNEXPECTED_EXCEPTION ="Unexpected exception";
    private static final String API_ERROR_SUBSCRIBER_NOT_FOUND =  "api.error.subscriber.not.found";
    private static final String RESULT =  "Result";
    private static final String ACTIVE =   "ACTIVE";
    private static final String EMAILID = "emailId";
    private static final String SUBSCRIBER_CONTROLLER =  "SubscriberController";
    private static final String API_ERROR_SUBSCRIBERUID_CANNOT_BE_NULL =  "api.error.subscriberuid.cant.be.null";
    private static final String API_ERROR_PHONE_NUMBER_IS_INVALID = "api.error.phone.number.is.invalid.please.enter.correct.phone.number";
    private static final String GET_SUBSCRIBER_REQUEST =  "getSubscriberDetailsBySerachType request searchType and searchValue {},{}";
    private static final String API_RESPONSE_SUBSCRIBER_RECORD_DELETED_SUCCESSFULLY =  "api.response.subscriber.record.deleted.successfully";
    private static final String API_ERROR_YOU_ARE_USING_LOW_LEVEL_ASSURANCE =  "api.error.you.are.using.low.level.of.assurance";
    private static final String API_ERROR_SUBSCRIBER_DETAIS_NOT_FOUND = "api.error.subscriber.details.not.found";
    private static final String API_RESPONSE_OK =  "api.response.ok";
    private static final String API_ERROR_NO_DATA_FOUND =  "api.error.no.data.found";
    private static final String API_ERROR_BAD_REQUEST =  "api.error.bad.request";
    private static final String API_RESPONSE_SUBSCIBER_DETAILS =  "api.response.subscriber.details";
    private static final String CUSTOMER_DETAILS =  "customerDetails";
    private static final String MOBILE_NUMBER =  "mobileNumber";
    private final SubscriberRepoIface subscriberRepoIface;
    private final SubscriberFcmTokenRepoIface subscriberFcmTokenRepoIface;
    private final SubscriberDeviceRepoIface deviceRepoIface;
    private final SubscriberDeviceHistoryRepoIface subscriberDeviceHistoryRepoIface;
    private final SubscriberFcmTokenRepoIface fcmTokenRepoIface;
    private final SubscriberOnboardingDataRepoIface onboardingDataRepoIface;
    private final SubscriberStatusRepoIface statusRepoIface;
    private final SubscriberRaDataRepoIface raRepoIface;
    private final SubscriberCertificatesRepoIface subscriberCertificatesRepoIface;

    private final OnboardingLivelinessRepository livelinessRepository;

    private final TrustedUserRepoIface trustedUserRepoIface;
    private final SubscriberDeletionRepository subscriberDeletionRepository;
    private final SubscriberCertificateDetailsRepoIface subscriberCertificateDetailsRepoIface;
    private final SubscriberCompleteDetailRepoIface subscriberCompleteDetailRepoIface;
    private final OnbKafkaSender mqSender;
    private final RestTemplate restTemplate;

    private final SubscriberConsentsRepo subscriberConsentsRepo;
    private final ConsentHistoryRepo consentHistoryRepo;
    private final OnboardingSentryClientExceptions sentryClientExceptions;
    private final MinioStorageServiceImpl minioStorageService;
    private final LogModelServiceImpl logModelServiceImpl;
    private final SubscriberHistoryRepo subscriberHistoryRepo;
    private final OnBoardingMethodRepoIface onBoardingMethodRepoIface;
    private final OnbOrgContactsEmailRepository onbOrgContactsEmailRepository;
    private final DeviceUpdateIface deviceUpdateIface;
    private final OnBoardingServiceException onBoardingServiceException;
    private final SubscriberPersonalDocumentRepo subscriberPersonalDocumentRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionHandlerUtil exceptionHandlerUtil;
    private final JavaMailSender mailSender;

    public SubscriberServiceImpl(SubscriberRepoIface subscriberRepoIface, SubscriberFcmTokenRepoIface subscriberFcmTokenRepoIface, SubscriberDeviceRepoIface deviceRepoIface, SubscriberDeviceHistoryRepoIface subscriberDeviceHistoryRepoIface, SubscriberFcmTokenRepoIface fcmTokenRepoIface, SubscriberOnboardingDataRepoIface onboardingDataRepoIface, SubscriberStatusRepoIface statusRepoIface, SubscriberRaDataRepoIface raRepoIface, SubscriberCertificatesRepoIface subscriberCertificatesRepoIface, OnboardingLivelinessRepository livelinessRepository, TrustedUserRepoIface trustedUserRepoIface, SubscriberDeletionRepository subscriberDeletionRepository, SubscriberCertificateDetailsRepoIface subscriberCertificateDetailsRepoIface, SubscriberCompleteDetailRepoIface subscriberCompleteDetailRepoIface, OnbKafkaSender mqSender, RestTemplate restTemplate, SubscriberConsentsRepo subscriberConsentsRepo, ConsentHistoryRepo consentHistoryRepo, OnboardingSentryClientExceptions sentryClientExceptions, MinioStorageServiceImpl minioStorageService, LogModelServiceImpl logModelServiceImpl, SubscriberHistoryRepo subscriberHistoryRepo, OnBoardingMethodRepoIface onBoardingMethodRepoIface, OnbOrgContactsEmailRepository onbOrgContactsEmailRepository, DeviceUpdateIface deviceUpdateIface, OnBoardingServiceException onBoardingServiceException, SubscriberPersonalDocumentRepo subscriberPersonalDocumentRepo, ExceptionHandlerUtil exceptionHandlerUtil, JavaMailSender mailSender) {
        this.subscriberRepoIface = subscriberRepoIface;
        this.subscriberFcmTokenRepoIface = subscriberFcmTokenRepoIface;
        this.deviceRepoIface = deviceRepoIface;
        this.subscriberDeviceHistoryRepoIface = subscriberDeviceHistoryRepoIface;
        this.fcmTokenRepoIface = fcmTokenRepoIface;
        this.onboardingDataRepoIface = onboardingDataRepoIface;
        this.statusRepoIface = statusRepoIface;
        this.raRepoIface = raRepoIface;
        this.subscriberCertificatesRepoIface = subscriberCertificatesRepoIface;
        this.livelinessRepository = livelinessRepository;
        this.trustedUserRepoIface = trustedUserRepoIface;
        this.subscriberDeletionRepository = subscriberDeletionRepository;
        this.subscriberCertificateDetailsRepoIface = subscriberCertificateDetailsRepoIface;
        this.subscriberCompleteDetailRepoIface = subscriberCompleteDetailRepoIface;
        this.mqSender = mqSender;
        this.restTemplate = restTemplate;
        this.subscriberConsentsRepo = subscriberConsentsRepo;
        this.consentHistoryRepo = consentHistoryRepo;
        this.sentryClientExceptions = sentryClientExceptions;
        this.minioStorageService = minioStorageService;
        this.logModelServiceImpl = logModelServiceImpl;
        this.subscriberHistoryRepo = subscriberHistoryRepo;
        this.onBoardingMethodRepoIface = onBoardingMethodRepoIface;
        this.onbOrgContactsEmailRepository = onbOrgContactsEmailRepository;
        this.deviceUpdateIface = deviceUpdateIface;
        this.onBoardingServiceException = onBoardingServiceException;
        this.subscriberPersonalDocumentRepo = subscriberPersonalDocumentRepo;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.mailSender = mailSender;
    }

    @Value("${is.onboarding.fee}")
	boolean isOnboardingFee;

	@Value("${dtportal.base.url}")
	private String dtportal;

	@Value("${ra.priauth.scheme}")
	private String priAuthScheme;

	@Value("${priauth.url.boolean}")
	private boolean priAuthSchemeBoolean;

	@Value(value = "${email.url}")
	private String emailBaseUrl;
	@Value(value = "${ind.api.sms}")
	private String indApiSMS;
	@Value(value = "${nira.api.sms}")
	private String niraApiSMS;

	@Value(value = "${nira.username}")
	private String niraUserName;

	@Value(value = "${nira.password}")
	private String niraPassword;
	@Value(value = "${nira.api.token}")
	private String niraApiToken;

	@Value(value = "${uae.api.sms}")
	private String uaeApiSMS;

	@Value(value = "${registerface.url}")
	private String registerFaceURL;

	@Value("${register.face.url}")
	private boolean registerFaceBoolean;

	@Value("${test.android.email}")
	private String testAndroidEmail;

	@Value("${test.ios.email}")
	private String testIosEmail;

	@Value("${test.android.mobile.no}")
	private String testAndroidOtp;

	@Value("${test.ios.mobile.no}")
	private String testIosOtp;

	@Value("${border.control.photo.isrequired}")
	private boolean boarderControllPhotoRequired;
	@Value(value = "${remove.background.url}")
	private String removeBackGroundFromImageURL;

	@Value(value = "${remove.background.boolean}")
	private boolean removeBackGroundFromImageBoolean;

    @Value("${ra.base.url}")
	private String raBaseUrl;

	@Value(value = "${nira.api.timetolive}")
	private int timeToLive;

	@Value("${au.log.url}")
	private String auditLogUrl;

	@Value("${config.validation.allowTrustedUsersOnly}")
	private String trustedUserStatus;

	// Re-onboard
	@Value("${re.onboard.dateofbirth}")
	private boolean checkDateOfBirth;

	@Value("${re.onboard.gender}")
	private boolean checkGender;

	@Value("${re.onboard.documentnumber}")
	private boolean checkDocumentNumber;

	@Value("${expiry.days}")
	private int expiryDays;

	@Value("${signed.required.by.user}")
	private boolean signRequired;

	@Value("${visitorCardUrl}")
	private String visitorCardUrl;
    @Value("${sms.api.key}")
    private String smsApiKey;

    @Value("${spring.mail.username}")
    private String senderEmail;
    public String generateSubscriberUniqueId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	private String encryptedString(String s) {
		try {
			Result result = DAESService.encryptData(s);
			return new String(result.getResponse());
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return e.getMessage();
		}
	}

    @Override
    public ApiResponse saveSubscribersData(MobileOTPDto subscriberDTO) throws ParseException, UnknownHostException {

        String result = ValidationUtil.validate(subscriberDTO);
        if (result != null) {
            System.out.println(" saveSubscribersData Validation errors: " + result);
            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(result, null);
        }
        if (subscriberDTO.getOsName() == null || subscriberDTO.getAppVersion() == null
                || subscriberDTO.getOsVersion() == null || subscriberDTO.getDeviceInfo() == null) {
            return exceptionHandlerUtil.createErrorResponse("api.error.application.info.not.found");
        } else if (subscriberDTO.getFcmToken() == null && subscriberDTO.getFcmToken().isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse("api.error.fcmtoken.cant.be.null.or.empty");
        }
        Date startTime = new Date();
        String OtpReqTime = AppUtil.getTimeStamping();
        OnbSubscriber subscriber = new OnbSubscriber();
        OnbSubscriberDevice subscriberDevice = new OnbSubscriberDevice();
        OnbSubscriberFcmToken fcmToken = new OnbSubscriberFcmToken();
        OnbSubscriberStatus subscriberStatus = new OnbSubscriberStatus();

        SubscriberRegisterResponseDTO responseDTO = new SubscriberRegisterResponseDTO();

        if (!Boolean.TRUE.equals(subscriberDTO.getOtpStatus())) {
            return exceptionHandlerUtil.createErrorResponse("api.error.otp.not.verified");
        }
        ApiResponse response = checkValidationForSubscriber(subscriberDTO);
        logger.info("{}{} - Response from checkValidationForSubscriber in saveSubscriberData: {}", CLASS,
                Utility.getMethodName(), response);
        if (!response.isSuccess() && response.getResult() != null) {
            response.setSuccess(true);
            return response;
        }
        if (!response.isSuccess() && response.getResult() == null) {
            return response;
        }

        try {
            String suid = generateSubscriberUniqueId();
            logger.info(CLASS + "saveSubscriberData req for suid {}", suid);
            if (subscriberDTO != null) {
                OnbSubscriber previousSuid = subscriberRepoIface.getSubscriberUidByEmailAndMobile(
                        subscriberDTO.getSubscriberEmail(), subscriberDTO.getSubscriberMobileNumber());
                if (previousSuid != null) {
                    OnbSubscriberFcmToken preSubscriberFcmToken = fcmTokenRepoIface
                            .findBysubscriberUid(previousSuid.getSubscriberUid());
                    OnbSubscriberStatus preSubscriberStatus = statusRepoIface
                            .findBysubscriberUid(previousSuid.getSubscriberUid());
                    OnbSubscriberDevice preSubscriberDevice = deviceRepoIface
                            .getSubscriber(previousSuid.getSubscriberUid());

                    if (preSubscriberDevice.getDeviceStatus().equals(Constant.DEVICE_STATUS_DISABLED)) {

                        OnbSubscriberDeviceHistory subscriberDeviceHistory = new OnbSubscriberDeviceHistory();
                        subscriberDeviceHistory.setSubscriberUid(previousSuid.getSubscriberUid());
                        subscriberDeviceHistory.setDeviceUid(preSubscriberDevice.getDeviceUid());
                        subscriberDeviceHistory.setDeviceStatus(Constant.DEVICE_STATUS_DISABLED);
                        subscriberDeviceHistory.setCreatedDate(AppUtil.getDate());
                        subscriberDeviceHistory.setUpdatedDate(AppUtil.getDate());
                        subscriberDeviceHistoryRepoIface.save(subscriberDeviceHistory);

                    }

                    System.out.println("previousSuid :: " + previousSuid);
                    subscriber.setSubscriberId(previousSuid.getSubscriberId());
                    subscriber.setSubscriberUid(previousSuid.getSubscriberUid());

                    subscriberDevice.setSubscriberDeviceId(preSubscriberDevice.getSubscriberDeviceId());
                    subscriberDevice.setSubscriberUid(previousSuid.getSubscriberUid());

                    fcmToken.setSubscriberFcmTokenId(preSubscriberFcmToken.getSubscriberFcmTokenId());
                    fcmToken.setSubscriberUid(previousSuid.getSubscriberUid());
                    subscriberStatus.setSubscriberStatusId(preSubscriberStatus.getSubscriberStatusId());
                    subscriberStatus.setSubscriberUid(previousSuid.getSubscriberUid());
                    responseDTO.setSuID(previousSuid.getSubscriberUid());

                } else {
                    subscriber.setSubscriberUid(suid);
                    subscriberDevice.setSubscriberUid(suid);
                    fcmToken.setSubscriberUid(suid);
                    subscriberStatus.setSubscriberUid(suid);
                    responseDTO.setSuID(suid);
                }

                subscriber.setCreatedDate(AppUtil.getDate());
                subscriber.setUpdatedDate(AppUtil.getDate());
                subscriber.setEmailId(subscriberDTO.getSubscriberEmail().toLowerCase());
                subscriber.setMobileNumber(subscriberDTO.getSubscriberMobileNumber());
                subscriber.setFullName(subscriberDTO.getSubscriberName());
                subscriber.setOsName(subscriberDTO.getOsName());
                subscriber.setOsVersion(subscriberDTO.getOsVersion());
                subscriber.setDeviceInfo(subscriberDTO.getDeviceInfo());
                subscriber.setAppVersion(subscriberDTO.getAppVersion());

                subscriberDevice.setCreatedDate(AppUtil.getDate());
                subscriberDevice.setUpdatedDate(AppUtil.getDate());
                subscriberDevice.setDeviceUid(subscriberDTO.getDeviceId());
                subscriberDevice.setDeviceStatus(Constant.DEVICE_STATUS_ACTIVE);

                fcmToken.setCreatedDate(AppUtil.getDate());
                fcmToken.setFcmToken(subscriberDTO.getFcmToken());

                subscriberStatus.setOtpVerifiedStatus(Constant.OTP_VERIFIED_STATUS);
                subscriberStatus.setSubscriberStatus(Constant.SUBSCRIBER_STATUS);
                subscriberStatus.setCreatedDate(AppUtil.getDate());
                subscriberStatus.setUpdatedDate(AppUtil.getDate());

                subscriber = subscriberRepoIface.save(subscriber);

                if (previousSuid != null) {

                    OnbSubscriberDevice device = deviceRepoIface.getSubscriber(previousSuid.getSubscriberUid());

                    System.out.println("old device  >> " + device.getSubscriberDeviceId());
                    deviceRepoIface.updateSubscriber(subscriberDTO.getDeviceId(), "ACTIVE", AppUtil.getDate(),
                            device.getSubscriberDeviceId());

                    System.out.println("Old device updated with new deviceid and Status ");

                } else {
                    subscriberDevice = deviceRepoIface.save(subscriberDevice);
                }

                if (previousSuid != null) {
                    List<String> firstTimeOnboardingList = subscriberRepoIface
                            .firstTimeOnboardingPaymentStatus(previousSuid.getSubscriberUid());

                    String firstTimeOnboarding = firstTimeOnboardingList.isEmpty() ? null
                            : firstTimeOnboardingList.get(0);

                    responseDTO.setFirstTimeOnboarding(firstTimeOnboarding == null);
                } else {
                    responseDTO.setFirstTimeOnboarding(true);
                }

                fcmToken = fcmTokenRepoIface.save(fcmToken);
                subscriberStatus = statusRepoIface.save(subscriberStatus);

                if (subscriber != null) {

                    responseDTO.setSubscriberStatus(Constant.SUBSCRIBER_STATUS);
                    Date endTime = new Date();

                    double toatlTime = AppUtil.getDifferenceInSeconds(startTime, endTime);
                    System.out.println("toatlTime :: " + toatlTime);

                    logModelServiceImpl.setLogModel(true, subscriber.getSubscriberUid(), null,
                            "SUBSCRIBER_REGISTRATION", subscriber.getSubscriberUid(), String.valueOf(toatlTime),
                            startTime, endTime, null);
                    logger.info(CLASS + " saveSubscriberData Subscriber Detail saved {}", responseDTO);
                    if (!signRequired) {
                        OnbSubscriberConsents subscriberConsents = new OnbSubscriberConsents();
                        String consentData = "I agreed to above Terms and conditions and Data privacy terms";
                        List<OnbConsentHistory> latestConsentList = consentHistoryRepo.findLatestConsent();

                        OnbConsentHistory consentHistory = latestConsentList.isEmpty() ? null : latestConsentList.get(0);

                        if (consentHistory == null) {
                            // No consent history found, handle gracefully

                            // maybe skip consent validation or return early
                        } else {
                            // Safe to call getId()
                            if (subscriberConsentsRepo.findSubscriberConsentBySuidAndConsentId(responseDTO.getSuID(),
                                    consentHistory.getId()) == null) {

                                subscriberConsents.setCreatedOn(AppUtil.getDate());
                                subscriberConsents.setConsentData(consentData);
                                subscriberConsents.setSuid(responseDTO.getSuID());
                                subscriberConsents.setConsentId(consentHistory.getId());
                                subscriberConsentsRepo.save(subscriberConsents);

                                // proceed with logic when subscriber has not given consent
                            }
                        }

                    }
                    return exceptionHandlerUtil.createSuccessResponse(
                            "api.response.subscriber.email.and.mobile.number.is.verified", responseDTO);
                } else {
                    logModelServiceImpl.setLogModel(false, subscriber.getSubscriberUid(), null,
                            "SUBSCRIBER_REGISTRATION", subscriber.getSubscriberUid(), null, null, null, null);

                    return exceptionHandlerUtil
                            .createErrorResponse("api.response.subscriber.email.and.mobile.number.is.not.verified");
                }
            } else {
                return exceptionHandlerUtil.createErrorResponse("api.error.empty.fields");
            }

        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            sentryClientExceptions.captureTags(subscriberDTO.getSuID(), subscriberDTO.getSubscriberMobileNumber(),
                    "saveSubscribersData", "SubscriberController");
            sentryClientExceptions.captureExceptions(e);
            logger.error(CLASS + "saveSubscriberData Exception {}", e.getMessage());
            return exceptionHandlerUtil.handleException(e);

        }
    }


    @Override
    public ApiResponse addSubscriberObData(SubscriberObRequestDTO obRequestDTO) throws Exception {
        try {
            if (Objects.isNull(obRequestDTO)) {
                return exceptionHandlerUtil
                        .createErrorResponse("api.error.subscriber.ob.request.cant.be.null.or.empty");
            }

            String validationMessage = ValidationUtil.validate(obRequestDTO);
            if (validationMessage != null) {
                System.out.println(" addSubscriberObData Validation errors: " + validationMessage);
                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(validationMessage, null);
            }

            String subscriberData = ValidationUtil.validate(obRequestDTO.getSubscriberData());
            if (subscriberData != null) {
                System.out.println(" addSubscriberObData Validation errors: " + subscriberData);
                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(subscriberData, null);
            }

            Date startTime = new Date();
            SubscriberObData subscriberObData = new SubscriberObData();
            SubscriberObData additionalFile = new SubscriberObData();
            OnbSubscriber subscriber = new OnbSubscriber();
            OnbSubscriber savedSubscriber = new OnbSubscriber();
            OnbSubscriberOnboardingData onboardingData = new OnbSubscriberOnboardingData();
            OnbSubscriberDevice subscriberDevice = new OnbSubscriberDevice();
            OnbSubscriberRaData raData = new OnbSubscriberRaData();
            OnbSubscriberStatus status = new OnbSubscriberStatus();
            IssueCertDTO issueCertDTO = new IssueCertDTO();
            int idDocNumberCount;
            String subscriberStatus = null;
            subscriber = subscriberRepoIface.findBysubscriberUid(obRequestDTO.getSuID());
            if (Objects.isNull(subscriber)) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
            }

            subscriberObData = obRequestDTO.getSubscriberData();

            if (!isOnboardingFee) {
                if ("Citizen".equals(obRequestDTO.getSubscriberType())) {
                    if (obRequestDTO.getSubscriberData().getOptionalData1() != null
                            && !obRequestDTO.getSubscriberData().getOptionalData1().isEmpty()) {
                        int count = isOptionData1Present(obRequestDTO.getSubscriberData().getOptionalData1());
                        if (count == 1) {
                            String suid = onboardingDataRepoIface
                                    .getOptionalData1Subscriber(obRequestDTO.getSubscriberData().getOptionalData1());
                            if (!suid.equals(obRequestDTO.getSuID())) {
                                logger.info(
                                        "{}{} - addSubscriberObData isOptionData1Present: Onboarding cannot be processed because the same national ID already exists: {}",
                                        CLASS, Utility.getMethodName(), count);
                                return exceptionHandlerUtil.createErrorResponse(
                                        "api.error.onboarding.can.not.be.processed.because.the.same.national.id.already.exists");
                            }
                        }
                    } else {
                        return exceptionHandlerUtil.createErrorResponse("api.error.optional.data.is.empty");
                    }
                }
            } else {
                if (!Constant.RESIDENT.equals(obRequestDTO.getSubscriberType())) {
                    if (obRequestDTO.getSubscriberData().getOptionalData1() != null
                            && !obRequestDTO.getSubscriberData().getOptionalData1().isEmpty()) {
                        int count = isOptionData1Present(obRequestDTO.getSubscriberData().getOptionalData1());
                        if (count == 1) {
                            String suid = onboardingDataRepoIface
                                    .getOptionalData1Subscriber(obRequestDTO.getSubscriberData().getOptionalData1());
                            if (!suid.equals(obRequestDTO.getSuID())) {
                                logger.info(
                                        "{}{} - addSubscriberObData isOptionData1Present: Onboarding cannot be processed because the same national ID already exists: {}",
                                        CLASS, Utility.getMethodName(), count);
                                return exceptionHandlerUtil.createErrorResponse(
                                        "api.error.onboarding.can.not.be.processed.because.the.same.national.id.already.exists");
                            }
                        }
                    } else {
                        return exceptionHandlerUtil.createErrorResponse("api.error.optional.data.is.empty");
                    }
                }
            }

            subscriberStatus = subscriberRepoIface.getSubscriberStatus(obRequestDTO.getSuID());
            logger.info("{}{} - addSubscriberObData request for subscriberStatus: {}", CLASS, Utility.getMethodName(),
                    subscriberStatus);
            if (Objects.isNull(subscriberStatus)) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
            }
            subscriberDevice = deviceRepoIface.getSubscriber(obRequestDTO.getSuID());
            if (Constant.DEVICE_STATUS_DISABLED.equals(subscriberDevice.getDeviceStatus())) {
                logger.info("{}{} - subscriberDevice DEVICE_STATUS_DISABLED: {}", CLASS, Utility.getMethodName(),
                        subscriberDevice);
                return exceptionHandlerUtil.createErrorResponse("api.error.this.device.is.disabled");
            }
            int idDocCount = subscriberRepoIface.getIdDocCount(subscriberObData.getDocumentNumber());
            idDocNumberCount = subscriberRepoIface.getSubscriberIdDocNumber(subscriberObData.getDocumentNumber(),
                    obRequestDTO.getSuID());
            if (idDocCount <= 0) {
                // id Doc count
            } else {
                if (idDocCount > 0 && idDocNumberCount == 0) {
                    return exceptionHandlerUtil.createErrorResponse("api.error.this.document.is.already.onboarded");
                }
            }

            String fullName = Stream
                    .of(subscriberObData.getSecondaryIdentifier(), subscriberObData.getPrimaryIdentifier())
                    .filter(s -> s != null && !s.trim().isEmpty()) // skip null or empty values
                    .map(s -> s.replaceAll("\\s+", " ").trim()) // clean spaces properly
                    .collect(Collectors.joining(" "));

            raData.setCommonName(fullName);

            subscriber.setFullName(fullName);
            subscriber.setDateOfBirth(subscriberObData.getDateOfBirth());
            subscriber.setIdDocType(subscriberObData.getDocumentType());
            subscriber.setIdDocNumber(subscriberObData.getDocumentNumber());
            subscriber.setUpdatedDate(AppUtil.getDate());
            subscriber.setSubscriberUid(obRequestDTO.getSuID());
            if (!Constant.RESIDENT.equals(obRequestDTO.getSubscriberType())) {
                subscriber.setNationalId(subscriberObData.getOptionalData1());
            }

            onboardingData.setCreatedDate(AppUtil.getDate());
            onboardingData.setIdDocType(subscriberObData.getDocumentType());
            onboardingData.setIdDocNumber(subscriberObData.getDocumentNumber());
            onboardingData.setOnboardingMethod(obRequestDTO.getOnboardingMethod());
            onboardingData.setSubscriberUid(obRequestDTO.getSuID());
            onboardingData.setTemplateId(obRequestDTO.getTemplateId());
            onboardingData.setOnboardingMethod(obRequestDTO.getOnboardingMethod());
            onboardingData.setSubscriberType(obRequestDTO.getSubscriberType());
            onboardingData.setIdDocCode(subscriberObData.getDocumentCode());
            onboardingData.setGender(subscriberObData.getGender());
            onboardingData.setGeolocation(subscriberObData.getGeoLocation());

            if (subscriberObData.getOptionalData1() != null && !subscriberObData.getOptionalData1().isEmpty()
                    && !subscriberObData.getOptionalData1().equals("0")) {
                onboardingData.setOptionalData1(subscriberObData.getOptionalData1());
            } else {
                onboardingData.setOptionalData1(subscriberObData.getDocumentNumber());
            }

            onboardingData.setDateOfExpiry(subscriberObData.getDateOfExpiry());

            if (subscriberObData.getNiraResponse() != null && !subscriberObData.getNiraResponse().isEmpty()) {
                String photo = null;

                if (isOnboardingFee) {

                    Result result = DAESService.decryptSecureWireData(subscriberObData.getNiraResponse());
                    String s = new String(result.getResponse());
                    JsonNode jsonNode = objectMapper.readTree(s);
                    photo = jsonNode.get("authenticPhoto").asText();
                } else {
                    JsonNode root = objectMapper.readTree(subscriberObData.getNiraResponse());
                    JsonNode dataNode = root.path("customerDetails").path("Result").path("Data");
                    // uaeKycId
                    String uaeKycId = dataNode.path("UaeKycId").asText(null);
                    System.out.println("uaeKycId = " + uaeKycId);
                    onboardingData.setUaeKycId(uaeKycId);
                    // documents -> personFace
                    photo = dataNode.path("Documents").path("PersonFace").asText(null);
                    // generate hash using Data json
                    String hash = AppUtil.hmacSha256Base64(root.toString());

                    onboardingData.setDocumentResponseHash(hash);
                    onboardingData.setNiraResponse(root.toString());

                    String passportNumber = dataNode.path("ActivePassport").path("DocumentNo").asText(null);
                    System.out.println(" passport number ::" + passportNumber);
                    if (passportNumber != null && !passportNumber.trim().isEmpty()) {
                        subscriber.setPassportNumber(passportNumber);
                    }

                    String emiratesIdNumber = dataNode.path("ResidenceInfo").path("EmiratesIdNumber").asText(null);
                    System.out.println(" EmiratesIdNumber  ::" + emiratesIdNumber);

                    if (emiratesIdNumber != null && !emiratesIdNumber.trim().isEmpty()) {
                        subscriber.setNationalIdNumber(emiratesIdNumber);
                    }

                    String emiratesIdDocumentNumber = dataNode.path("ResidenceInfo").path("DocumentNo").asText(null);
                    System.out.println(" EmiratesIdDocumentNumber number ::" + emiratesIdDocumentNumber);

                    if (emiratesIdDocumentNumber != null && !emiratesIdDocumentNumber.trim().isEmpty()) {
                        subscriber.setNationalIdCardNumber(emiratesIdDocumentNumber);
                    }

                    if (obRequestDTO.getOnboardingMethod().equalsIgnoreCase("NIN")) {

                        if (emiratesIdNumber != null && !emiratesIdNumber.trim().isEmpty()) {

                            subscriber.setIdDocNumber(emiratesIdNumber);
                            subscriber.setNationalId(emiratesIdNumber);
                        }

                    } else {
                        if (!obRequestDTO.getOnboardingMethod().equalsIgnoreCase("PASSPORT")) {
                            // if block
                        } else {
                            if (passportNumber != null && !passportNumber.trim().isEmpty()) {
                                subscriber.setIdDocNumber(passportNumber);
                            }
                        }
                    }
                }
                onboardingData.setVerifierProvidedPhoto(photo);
            } else {
                onboardingData.setVerifierProvidedPhoto(obRequestDTO.getSubscriberData().getSubscriberSelfie());
            }

            // Set nira Response
            Optional.ofNullable(subscriberObData.getNiraResponse()).filter(response -> !response.isEmpty())
                    .ifPresent(onboardingData::setNiraResponse);

            // set LOA based on onboarding method
            OnboardingMethod onboardingMethod = onBoardingMethodRepoIface
                    .findByonboardingMethod(obRequestDTO.getOnboardingMethod());
            onboardingData.setLevelOfAssurance(onboardingMethod.getLevelOfAssurance());

            raData.setCommonName(fullName);
            raData.setCertificateType(Constant.BOTH);
            raData.setCountryName(subscriberObData.getNationality());
            raData.setCreatedDate(AppUtil.getDate());
            raData.setPkiPassword(fullName);
            raData.setPkiPasswordHash(subscriberObData.getSecondaryIdentifier() + " "
                    + subscriberObData.getPrimaryIdentifier().hashCode());

            raData.setPkiUserName(fullName);
            raData.setPkiUserNameHash(subscriberObData.getSecondaryIdentifier() + " "
                    + subscriberObData.getPrimaryIdentifier().hashCode());

            raData.setSubscriberUid(obRequestDTO.getSuID());

            issueCertDTO.setSubscriberUniqueId(obRequestDTO.getSuID());

            FaceFeaturesDto faceFeaturesDto = new FaceFeaturesDto();
            faceFeaturesDto.setSubscriberPhoto(obRequestDTO.getSubscriberData().getSubscriberSelfie());
            Selfie selfie = new Selfie();

            // Remove BackGround image
            if (removeBackGroundFromImageBoolean) {
                ApiResponse apiResponseGetImage = getImageWithOutBackGround(
                        obRequestDTO.getSubscriberData().getDocumentNumber(),
                        obRequestDTO.getSubscriberData().getSubscriberSelfie());

                if (apiResponseGetImage.getResult() == null) {
                    selfie.setSubscriberSelfie(obRequestDTO.getSubscriberData().getSubscriberSelfie());
                } else {
                    selfie.setSubscriberSelfie(apiResponseGetImage.getResult().toString());
                }
            } else {
                selfie.setSubscriberSelfie(obRequestDTO.getSubscriberData().getSubscriberSelfie());
            }

            selfie.setSubscriberUniqueId(obRequestDTO.getSuID());

            if (isOnboardingFee) {
                // UgPASS
                CompletableFuture<ApiResponse> selfieResponse = minioStorageService.saveFileToMinio(selfie, "selfie",
                        null);

                ApiResponse apiResponse = selfieResponse.get();
                if (apiResponse.isSuccess()) {
                    logger.info(CLASS + " addSubscriberObData res for saveFileToEdms: ", selfieResponse);
                    String selfieURI = (String) apiResponse.getResult();
                    onboardingData.setSelfieUri(selfieURI);
                } else {
                    logger.info(CLASS + "addSubscriberObData res in false for saveFileToEdms: ",
                            apiResponse.getMessage());
                    return exceptionHandlerUtil.createFailedResponseWithCustomMessage(apiResponse.getMessage(), null);

                }
            } else {
                // UAEID EDMS remove in UAEID
                onboardingData.setSelfie(obRequestDTO.getSubscriberData().getSubscriberSelfie());
            }

            CompletableFuture<ApiResponse> selfieThumbnailResponse = minioStorageService
                    .createThumbnailOfSelfie(selfie);

            ApiResponse selfieApiResponse = selfieThumbnailResponse.get();
            if (selfieApiResponse.isSuccess()) {
                logger.info(CLASS + "addSubscriberObData res for createThumlbnailOfSelfie: ",
                        selfieApiResponse.isSuccess());
                onboardingData.setSelfieThumbnailUri(selfieApiResponse.getResult().toString());
            } else {
                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(selfieApiResponse.getMessage(), null);

            }

            savedSubscriber = subscriberRepoIface.save(subscriber);

            additionalFile = subscriberObData;
            additionalFile.setSubscriberSelfie(null);
            additionalFile.setSubscriberUniqueId(onboardingData.getSubscriberUid());
            String additionalFieldSaved = objectMapper.writeValueAsString(additionalFile);
            onboardingData.setOnboardingDataFieldsJson(additionalFieldSaved);
            onboardingData.setRemarks(obRequestDTO.getSubscriberData().getRemarks());

            faceFeaturesDto.setSuid(onboardingData.getSubscriberUid());
            faceFeaturesDto.setSubscriberName(savedSubscriber.getFullName());
            faceFeaturesDto.setSubscriberDataJson(additionalFieldSaved);

            if (registerFaceBoolean) {
                ExecutorService executor1 = Executors.newFixedThreadPool(1000);
                try {
                    Runnable registerFaceWorkerThread =
                            new RegisterFaceWorkerThread(registerFaceURL, faceFeaturesDto);
                    executor1.execute(registerFaceWorkerThread);
                } catch (Exception e) {
                } finally {
                    shutdownExecutor(executor1);
                }
            }


            onboardingData = onboardingDataRepoIface.save(onboardingData);
            status = statusRepoIface.findBysubscriberUid(onboardingData.getSubscriberUid());

            String subStatus = subscriberRepoIface.getSubscriberStatus(onboardingData.getSubscriberUid());
            logger.info("{}{} - getSubscriberStatus: {}", CLASS, Utility.getMethodName(), subStatus);
            if (subStatus != null) {
                if (subStatus.equals(Constant.ACTIVE)) {
                    status.setSubscriberStatus(Constant.ACTIVE);
                    status.setSubscriberStatusDescription(Constant.LOA_UPDATED);
                    status.setUpdatedDate(AppUtil.getDate());
                    status = statusRepoIface.save(status);
                } else if (subStatus.equals(Constant.PIN_SET_REQUIRED)) {
                    status.setSubscriberStatus(Constant.PIN_SET_REQUIRED);
                    status.setSubscriberStatusDescription(Constant.LOA_UPDATED);
                    status.setUpdatedDate(AppUtil.getDate());
                    status = statusRepoIface.save(status);
                } else {

                    if (isOnboardingFee) {
                        System.out.println("isOnboardingFee false :::::" + isOnboardingFee);
                        status.setSubscriberStatus("ONBOARDED");
                    } else {
                        System.out.println("It should come here and make it ACTIVE");
                        System.out.println("COnstant active value:::ACTIVE");
                        status.setSubscriberStatus("ACTIVE");
                        System.out.println("Status after changed::::" + status.getSubscriberStatus());
                    }
                    status.setSubscriberStatusDescription(Constant.ONBOARDED_SUCESSFULLY);
                    status.setUpdatedDate(AppUtil.getDate());
                    status = statusRepoIface.save(status);
                    raData = raRepoIface.save(raData);
                }
            } else {
                if (isOnboardingFee) {
                    System.out.println("isOnboardingFee false :::::" + isOnboardingFee);
                    status.setSubscriberStatus("ONBOARDED");
                } else {
                    System.out.println("It should come here and make it ACTIVE");
                    System.out.println("COnstant active value:::ACTIVE");
                    status.setSubscriberStatus("ACTIVE");
                    System.out.println("Status after changed::::" + status.getSubscriberStatus());
                }
                status.setSubscriberStatusDescription(Constant.ONBOARDED_SUCESSFULLY);
                status.setUpdatedDate(AppUtil.getDate());
                status = statusRepoIface.save(status);
                raData = raRepoIface.save(raData);
            }

            OnbSubscriber s = subscriberRepoIface.findBysubscriberUid(onboardingData.getSubscriberUid());
            if (s != null) {

                Date endTime = new Date();
                double toatlTime = AppUtil.getDifferenceInSeconds(startTime, endTime);
                logModelServiceImpl.setLogModel(true, s.getSubscriberUid(), onboardingData.getGeolocation(),
                        Constant.SUBSCRIBER_ONBOARDED, s.getSubscriberUid(), String.valueOf(toatlTime), startTime,
                        endTime, null);
                logger.info("{}{} - Subscriber OnBoarding Data Saved: {}", CLASS, Utility.getMethodName(), s);
                return exceptionHandlerUtil
                        .createSuccessResponse("api.response.ugpass.application.submitted.successfully", s);
            } else {
                String subscriberUid = onboardingData.getSubscriberUid();
                logModelServiceImpl.setLogModel(false, subscriberUid, null, Constant.SUBSCRIBER_ONBOARDED,
                        subscriberUid, null, null, null, null);
                return exceptionHandlerUtil
                        .createErrorResponseWithResult("api.error.ugpass.application.submission.failed", s);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            logger.error("{}{} - Subscriber OnBoarding Data Exception: {}", CLASS, Utility.getMethodName(), e);
            sentryClientExceptions.captureExceptions(e);
            return onBoardingServiceException.handleExceptionWithStaticMessageWithSentry(e,obRequestDTO != null ? obRequestDTO.getSuID():null);
        }
    }

    private void shutdownExecutor(ExecutorService executor1) {
        executor1.shutdown();
        try {
            if (!executor1.awaitTermination(60, TimeUnit.SECONDS)) {
                executor1.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor1.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("unused")

    private ApiResponse validateSubscriberDTO(MobileOTPDto subscriberDTO) {

        String result = ValidationUtil.validate(subscriberDTO);

        if (result != null) {
            logger.info(" saveSubscribersData Validation errors: {} ", result);
            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(result, null);
        }

        if (subscriberDTO.getOsName() == null ||
                subscriberDTO.getAppVersion() == null ||
                subscriberDTO.getOsVersion() == null ||
                subscriberDTO.getDeviceInfo() == null) {

            return exceptionHandlerUtil.createErrorResponse("api.error.application.info.not.found");
        }

        if (subscriberDTO.getFcmToken() == null && subscriberDTO.getFcmToken().isEmpty()) {

            return exceptionHandlerUtil.createErrorResponse("api.error.fcmtoken.cant.be.null.or.empty");
        }

        return null;
    }
    private ApiResponse processSubscriberSave(MobileOTPDto subscriberDTO, Date startTime)
            throws ParseException {

        OnbSubscriber subscriber = new OnbSubscriber();
        OnbSubscriberDevice subscriberDevice = new OnbSubscriberDevice();
        OnbSubscriberFcmToken fcmToken = new OnbSubscriberFcmToken();
        OnbSubscriberStatus subscriberStatus = new OnbSubscriberStatus();
        SubscriberRegisterResponseDTO responseDTO = new SubscriberRegisterResponseDTO();

        String suid = generateSubscriberUniqueId();

        logger.info(CLASS + "saveSubscriberData req for suid {}", suid);

        OnbSubscriber previousSuid = subscriberRepoIface.getSubscriberUidByEmailAndMobile(
                subscriberDTO.getSubscriberEmail(),
                subscriberDTO.getSubscriberMobileNumber());

        prepareSubscriberEntities(
                subscriber,
                subscriberDevice,
                fcmToken,
                subscriberStatus,
                responseDTO,
                suid,
                previousSuid);


        saveSubscriberEntities(
                subscriberDTO,
                subscriber,
                subscriberDevice,
                fcmToken,
                subscriberStatus,
                previousSuid
               );

        return buildSubscriberResponse(subscriber, responseDTO, startTime);
    }

    private void prepareSubscriberEntities(
            OnbSubscriber subscriber,
            OnbSubscriberDevice subscriberDevice,
            OnbSubscriberFcmToken fcmToken,
            OnbSubscriberStatus subscriberStatus,
            SubscriberRegisterResponseDTO responseDTO,
            String suid,
            OnbSubscriber previousSuid) {

        if (previousSuid != null) {

            OnbSubscriberFcmToken preSubscriberFcmToken =
                    fcmTokenRepoIface.findBysubscriberUid(previousSuid.getSubscriberUid());

            OnbSubscriberStatus preSubscriberStatus =
                    statusRepoIface.findBysubscriberUid(previousSuid.getSubscriberUid());

            OnbSubscriberDevice preSubscriberDevice =
                    deviceRepoIface.getSubscriber(previousSuid.getSubscriberUid());

            subscriber.setSubscriberId(previousSuid.getSubscriberId());
            subscriber.setSubscriberUid(previousSuid.getSubscriberUid());

            subscriberDevice.setSubscriberDeviceId(preSubscriberDevice.getSubscriberDeviceId());
            subscriberDevice.setSubscriberUid(previousSuid.getSubscriberUid());

            fcmToken.setSubscriberFcmTokenId(preSubscriberFcmToken.getSubscriberFcmTokenId());
            fcmToken.setSubscriberUid(previousSuid.getSubscriberUid());

            subscriberStatus.setSubscriberStatusId(preSubscriberStatus.getSubscriberStatusId());
            subscriberStatus.setSubscriberUid(previousSuid.getSubscriberUid());

            responseDTO.setSuID(previousSuid.getSubscriberUid());

        } else {

            subscriber.setSubscriberUid(suid);
            subscriberDevice.setSubscriberUid(suid);
            fcmToken.setSubscriberUid(suid);
            subscriberStatus.setSubscriberUid(suid);
            responseDTO.setSuID(suid);
        }
    }

    private void saveSubscriberEntities(
            MobileOTPDto subscriberDTO,
            OnbSubscriber subscriber,
            OnbSubscriberDevice subscriberDevice,
            OnbSubscriberFcmToken fcmToken,
            OnbSubscriberStatus subscriberStatus,
            OnbSubscriber previousSuid) {

        subscriber.setCreatedDate(AppUtil.getDate());
        subscriber.setUpdatedDate(AppUtil.getDate());
        subscriber.setEmailId(subscriberDTO.getSubscriberEmail().toLowerCase());
        subscriber.setMobileNumber(subscriberDTO.getSubscriberMobileNumber());
        subscriber.setFullName(subscriberDTO.getSubscriberName());
        subscriber.setOsName(subscriberDTO.getOsName());
        subscriber.setOsVersion(subscriberDTO.getOsVersion());
        subscriber.setDeviceInfo(subscriberDTO.getDeviceInfo());
        subscriber.setAppVersion(subscriberDTO.getAppVersion());

        subscriberRepoIface.save(subscriber);

        if (previousSuid != null) {

            OnbSubscriberDevice device =
                    deviceRepoIface.getSubscriber(previousSuid.getSubscriberUid());

            deviceRepoIface.updateSubscriber(
                    subscriberDTO.getDeviceId(),
                    ACTIVE,
                    AppUtil.getDate(),
                    device.getSubscriberDeviceId());

        } else {

            deviceRepoIface.save(subscriberDevice);
        }

        fcmTokenRepoIface.save(fcmToken);
        statusRepoIface.save(subscriberStatus);
    }

    private ApiResponse buildSubscriberResponse(
            OnbSubscriber subscriber,
            SubscriberRegisterResponseDTO responseDTO,
            Date startTime) throws ParseException {

        if (subscriber == null) {

            return exceptionHandlerUtil.createErrorResponse(
                    "api.response.subscriber.email.and.mobile.number.is.not.verified");
        }

        Date endTime = new Date();

        double totalTime = AppUtil.getDifferenceInSeconds(startTime, endTime);

        logModelServiceImpl.setLogModel(
                true,
                subscriber.getSubscriberUid(),
                null,
                "SUBSCRIBER_REGISTRATION",
                subscriber.getSubscriberUid(),
                String.valueOf(totalTime),
                startTime,
                endTime,
                null);

        responseDTO.setSubscriberStatus(Constant.SUBSCRIBER_STATUS);

        return exceptionHandlerUtil.createSuccessResponse(
                "api.response.subscriber.email.and.mobile.number.is.verified",
                responseDTO);
    }


    public ApiResponse checkValidationForSubscriber(MobileOTPDto mobileOTPDto) {

        logger.info("{}{} - Request received in checkValidationForSubscriber: {}",
                CLASS, Utility.getMethodName(), mobileOTPDto);

        try {

            if (!Boolean.TRUE.equals(mobileOTPDto.getOtpStatus())) {
                logger.info("{}{} - OTP verification failed", CLASS, Utility.getMethodName());
                return exceptionHandlerUtil.createErrorResponse("api.error.otp.verification.is.failed");
            }

            ValidationCounts counts = getValidationCounts(mobileOTPDto);

            OnbSubscriber previousSuid = validateExistingSubscriber(mobileOTPDto, counts);

            ApiResponse registeredDeviceResponse =
                    handleRegisteredDeviceFlow(mobileOTPDto, counts, previousSuid);

            if (registeredDeviceResponse != null) {
                return registeredDeviceResponse;
            }

            return handleNewDeviceFlow(mobileOTPDto, counts);

        } catch (Exception e) {

            logger.error("{}{} - Exception: {}", CLASS, Utility.getMethodName(), e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);

            sentryClientExceptions.captureTags(
                    mobileOTPDto.getSuID(),
                    mobileOTPDto.getSubscriberMobileNumber(),
                    "checkValidationSubscriber",
                    SUBSCRIBER_CONTROLLER);

            sentryClientExceptions.captureExceptions(e);

            return exceptionHandlerUtil.handleException(e);
        }
    }
    private ValidationCounts getValidationCounts(MobileOTPDto mobileOTPDto) {

        ValidationCounts counts = new ValidationCounts();

        counts.setCountDevice(
                subscriberRepoIface.countSubscriberDevice(mobileOTPDto.getDeviceId()));

        logger.info("{}{} - Device count: {}", CLASS, Utility.getMethodName(),
                counts.getCountDevice());

        counts.setCountMobile(
                subscriberRepoIface.countSubscriberMobile(
                        mobileOTPDto.getSubscriberMobileNumber()));

        counts.setCountEmail(
                subscriberRepoIface.countSubscriberEmailId(
                        mobileOTPDto.getSubscriberEmail().toLowerCase()));

        return counts;
    }
    private OnbSubscriber validateExistingSubscriber(MobileOTPDto mobileOTPDto,
                                                  ValidationCounts counts) {

        OnbSubscriber previousSuid = null;

        if (counts.getCountEmail() == 1 && counts.getCountMobile() == 1 && counts.getCountDevice() >= 1) {

            previousSuid = subscriberRepoIface.getSubscriberDetailsByEmailAndMobile(
                    mobileOTPDto.getSubscriberEmail().toLowerCase(),
                    mobileOTPDto.getSubscriberMobileNumber());

            if (previousSuid == null) {
                throw new ApplicationException(
                        "api.error.this.mobile.no.is.already.register.with.different.email.id");
            }
        }

        return previousSuid;
    }
    private ApiResponse handleRegisteredDeviceFlow(MobileOTPDto mobileOTPDto,
                                                   ValidationCounts counts,
                                                   OnbSubscriber previousSuid) {

        if (counts.getCountDevice() == 1 && counts.getCountMobile() == 1 && counts.getCountEmail() == 1) {

            OnbSubscriberDevice deviceDetails =
                    deviceRepoIface.getSubscriber(previousSuid.getSubscriberUid());

            SubscriberRegisterResponseDTO responseDTO = new SubscriberRegisterResponseDTO();

            responseDTO.setSuID(deviceDetails.getSubscriberUid());

            OnbSubscriber subscriber =
                    subscriberRepoIface.findBysubscriberUid(deviceDetails.getSubscriberUid());

            if (!subscriber.getEmailId().equals(mobileOTPDto.getSubscriberEmail().toLowerCase())
                    || !subscriber.getMobileNumber()
                    .equals(mobileOTPDto.getSubscriberMobileNumber())) {

                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.this.device.is.already.register.with.differet.email.or.mobile.no");
            }

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    "api.error.this.device.is.already.registered.please.continue",
                    responseDTO);
        }

        return null;
    }
    private ApiResponse handleNewDeviceFlow(MobileOTPDto mobileOTPDto,
                                            ValidationCounts counts) {

        if (counts.getCountDevice() >= 1) {

            OnbSubscriberDevice deviceDetails =
                    deviceRepoIface.findBydeviceUidAndStatus(
                            mobileOTPDto.getDeviceId(),
                            ACTIVE);

            if (deviceDetails != null) {

                if (counts.getCountEmail() == 0) {

                    return exceptionHandlerUtil.createErrorResponse(
                            "api.error.this.device.is.already.registered.with.different.email");
                }

                if (counts.getCountMobile() == 0) {

                    return exceptionHandlerUtil.createErrorResponse(
                            "api.error.this.device.is.already.register.with.different.mobile.number");
                }
            }
        }

        return exceptionHandlerUtil.successResponse("api.response.success");
    }


    @Override
	public ApiResponse saveSubscriberDocument(SubscriberDocumentDto subscriberDocumentDto) {
		try {
			OnbSubscriberPersonalDocument personalDocument = subscriberPersonalDocumentRepo
					.findBySubscriberUniqueId(subscriberDocumentDto.getSubscriberUID());

			if (personalDocument != null) {
				personalDocument.setSubscriberUniqueId(subscriberDocumentDto.getSubscriberUID());
				personalDocument.setDocument(subscriberDocumentDto.getDocument());
				personalDocument.setUpdatedDate(AppUtil.getDate());
				subscriberPersonalDocumentRepo.save(personalDocument);

				return exceptionHandlerUtil.successResponse("api.response.subscriber.document.updated.successfully");
			} else {
				OnbSubscriberPersonalDocument subscriberPersonalDocument = new OnbSubscriberPersonalDocument();
				subscriberPersonalDocument.setSubscriberUniqueId(subscriberDocumentDto.getSubscriberUID());
				subscriberPersonalDocument.setDocument(subscriberDocumentDto.getDocument());
				subscriberPersonalDocument.setCreatedDate(AppUtil.getDate());
				subscriberPersonalDocument.setUpdatedDate(AppUtil.getDate());
				subscriberPersonalDocumentRepo.save(subscriberPersonalDocument);

				return exceptionHandlerUtil.successResponse("api.response.subscriber.document.save.successfully");
			}

		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);

		}
	}

    private OnbSubscriberOnboardingData prepareOnboardingData(
            SubscriberObRequestDTO obRequestDTO,
            SubscriberObData subscriberObData) {

        OnbSubscriberOnboardingData onboardingData = new OnbSubscriberOnboardingData();

        onboardingData.setCreatedDate(AppUtil.getDate());
        onboardingData.setIdDocType(subscriberObData.getDocumentType());
        onboardingData.setIdDocNumber(subscriberObData.getDocumentNumber());
        onboardingData.setOnboardingMethod(obRequestDTO.getOnboardingMethod());
        onboardingData.setSubscriberUid(obRequestDTO.getSuID());
        onboardingData.setTemplateId(obRequestDTO.getTemplateId());
        onboardingData.setSubscriberType(obRequestDTO.getSubscriberType());
        onboardingData.setIdDocCode(subscriberObData.getDocumentCode());
        onboardingData.setGender(subscriberObData.getGender());
        onboardingData.setGeolocation(subscriberObData.getGeoLocation());

        if (subscriberObData.getOptionalData1() != null
                && !subscriberObData.getOptionalData1().isEmpty()
                && !subscriberObData.getOptionalData1().equals("0")) {

            onboardingData.setOptionalData1(subscriberObData.getOptionalData1());

        } else {

            onboardingData.setOptionalData1(subscriberObData.getDocumentNumber());
        }

        onboardingData.setDateOfExpiry(subscriberObData.getDateOfExpiry());

        OnboardingMethod onboardingMethod =
                onBoardingMethodRepoIface.findByonboardingMethod(
                        obRequestDTO.getOnboardingMethod());

        onboardingData.setLevelOfAssurance(onboardingMethod.getLevelOfAssurance());

        return onboardingData;
    }
    private OnbSubscriberRaData prepareRaData(
            SubscriberObData subscriberObData,
            String fullName,
            SubscriberObRequestDTO obRequestDTO) {

        OnbSubscriberRaData raData = new OnbSubscriberRaData();

        raData.setCommonName(fullName);
        raData.setCertificateType(Constant.BOTH);
        raData.setCountryName(subscriberObData.getNationality());
        raData.setCreatedDate(AppUtil.getDate());

        raData.setPkiPassword(fullName);

        raData.setPkiPasswordHash(
                subscriberObData.getSecondaryIdentifier() + " "
                        + subscriberObData.getPrimaryIdentifier().hashCode());

        raData.setPkiUserName(fullName);

        raData.setPkiUserNameHash(
                subscriberObData.getSecondaryIdentifier() + " "
                        + subscriberObData.getPrimaryIdentifier().hashCode());

        raData.setSubscriberUid(obRequestDTO.getSuID());

        return raData;
    }
    private void handleNiraResponse(
            SubscriberObRequestDTO obRequestDTO,
            SubscriberObData subscriberObData,
            OnbSubscriberOnboardingData onboardingData) {

        try {

            if (subscriberObData.getNiraResponse() != null
                    && !subscriberObData.getNiraResponse().isEmpty()) {

                String photo = null;

                if (isOnboardingFee) {

                    Result result =
                            DAESService.decryptSecureWireData(subscriberObData.getNiraResponse());

                    String s = new String(result.getResponse());

                    JsonNode jsonNode = objectMapper.readTree(s);

                    photo = jsonNode.get("authenticPhoto").asText();

                } else {

                    JsonNode root = objectMapper.readTree(subscriberObData.getNiraResponse());

                    JsonNode dataNode =
                            root.path(CUSTOMER_DETAILS).path(RESULT).path("Data");

                    String uaeKycId = dataNode.path("UaeKycId").asText(null);

                    onboardingData.setUaeKycId(uaeKycId);

                    photo = dataNode.path("Documents").path("PersonFace").asText(null);

                    String hash = AppUtil.hmacSha256Base64(root.toString());

                    onboardingData.setDocumentResponseHash(hash);

                    onboardingData.setNiraResponse(root.toString());
                }

                onboardingData.setVerifierProvidedPhoto(photo);

            } else {

                onboardingData.setVerifierProvidedPhoto(
                        obRequestDTO.getSubscriberData().getSubscriberSelfie());
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {

            throw new ApplicationException("Error while processing NIRA response", e);

        } catch (Exception e) {

            throw new ApplicationException("Error while handling NIRA response", e);
        }
    }
    private void handleSelfieProcessing(
            SubscriberObRequestDTO obRequestDTO,
            OnbSubscriberOnboardingData onboardingData) {

        try {

            Selfie selfie = new Selfie();

            selfie.setSubscriberSelfie(
                    obRequestDTO.getSubscriberData().getSubscriberSelfie());

            selfie.setSubscriberUniqueId(obRequestDTO.getSuID());

            if (isOnboardingFee) {

                CompletableFuture<ApiResponse> selfieResponse =
                        minioStorageService.saveFileToMinio(selfie, "selfie", null);

                ApiResponse apiResponse = selfieResponse.get();

                if (apiResponse.isSuccess()) {

                    String selfieURI = (String) apiResponse.getResult();
                    onboardingData.setSelfieUri(selfieURI);

                } else {
                    throw new ApplicationException(apiResponse.getMessage());
                }

            } else {

                onboardingData.setSelfie(
                        obRequestDTO.getSubscriberData().getSubscriberSelfie());
            }

            CompletableFuture<ApiResponse> selfieThumbnailResponse =
                    minioStorageService.createThumbnailOfSelfie(selfie);

            ApiResponse selfieApiResponse = selfieThumbnailResponse.get();

            if (selfieApiResponse.isSuccess()) {

                onboardingData.setSelfieThumbnailUri(
                        selfieApiResponse.getResult().toString());

            } else {
                throw new ApplicationException(selfieApiResponse.getMessage());
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt(); // ✅ Sonar required
            throw new ApplicationException("Thread interrupted while processing selfie", e);

        } catch (ExecutionException e) {

            throw new ApplicationException("Error while processing selfie", e);
        }
    }
    private void saveAdditionalFields(
            OnbSubscriberOnboardingData onboardingData,
            SubscriberObData subscriberObData,
            SubscriberObRequestDTO obRequestDTO) {

        try {

            SubscriberObData additionalFile = subscriberObData;

            additionalFile.setSubscriberSelfie(null);

            additionalFile.setSubscriberUniqueId(onboardingData.getSubscriberUid());

            String additionalFieldSaved =
                    objectMapper.writeValueAsString(additionalFile);

            onboardingData.setOnboardingDataFieldsJson(additionalFieldSaved);

            onboardingData.setRemarks(
                    obRequestDTO.getSubscriberData().getRemarks());

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {

            throw new ApplicationException("Error while saving additional onboarding fields", e);
        }
    }
    private void updateSubscriberStatus(
            OnbSubscriberOnboardingData onboardingData,
            OnbSubscriberRaData raData) {

        OnbSubscriberStatus status =
                statusRepoIface.findBysubscriberUid(onboardingData.getSubscriberUid());

        String subStatus =
                subscriberRepoIface.getSubscriberStatus(onboardingData.getSubscriberUid());

        if (subStatus != null) {

            if (subStatus.equals(Constant.ACTIVE)) {

                status.setSubscriberStatus(Constant.ACTIVE);

            } else if (subStatus.equals(Constant.PIN_SET_REQUIRED)) {

                status.setSubscriberStatus(Constant.PIN_SET_REQUIRED);

            } else {

                if (isOnboardingFee) {
                    status.setSubscriberStatus("ONBOARDED");
                } else {
                    status.setSubscriberStatus(ACTIVE);
                }

              raRepoIface.save(raData);
            }

        } else {

            if (isOnboardingFee) {
                status.setSubscriberStatus("ONBOARDED");
            }

           raRepoIface.save(raData);
        }

        status.setSubscriberStatusDescription(Constant.ONBOARDED_SUCESSFULLY);

        status.setUpdatedDate(AppUtil.getDate());

        statusRepoIface.save(status);
    }
    private ApiResponse buildFinalResponse(
            Date startTime,
            OnbSubscriberOnboardingData onboardingData) throws ParseException {

        OnbSubscriber s =
                subscriberRepoIface.findBysubscriberUid(onboardingData.getSubscriberUid());

        if (s != null) {

            Date endTime = new Date();

            double totalTime =
                    AppUtil.getDifferenceInSeconds(startTime, endTime);

            logModelServiceImpl.setLogModel(
                    true,
                    s.getSubscriberUid(),
                    onboardingData.getGeolocation(),
                    Constant.SUBSCRIBER_ONBOARDED,
                    s.getSubscriberUid(),
                    String.valueOf(totalTime),
                    startTime,
                    endTime,
                    null);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.ugpass.application.submitted.successfully",
                    s);

        } else {

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    "api.error.ugpass.application.submission.failed",
                    s);
        }
    }

    private ApiResponse validateRequest(SubscriberObRequestDTO obRequestDTO) {

        if (Objects.isNull(obRequestDTO)) {
            return exceptionHandlerUtil
                    .createErrorResponse("api.error.subscriber.ob.request.cant.be.null.or.empty");
        }

        String validationMessage = ValidationUtil.validate(obRequestDTO);
        if (validationMessage != null) {

            logger.info(" addSubscriberObData Validation errors: {}", validationMessage);

            return exceptionHandlerUtil
                    .createFailedResponseWithCustomMessage(validationMessage, null);
        }

        String subscriberData = ValidationUtil.validate(obRequestDTO.getSubscriberData());

        if (subscriberData != null) {

            logger.info(" addSubscriberObData Validation errors: {}", subscriberData);

            return exceptionHandlerUtil
                    .createFailedResponseWithCustomMessage(subscriberData, null);
        }

        return null;
    }
    private ApiResponse validateOptionalData(
            SubscriberObRequestDTO obRequestDTO,
            SubscriberObData subscriberObData) {

        if (subscriberObData.getOptionalData1() == null
                || subscriberObData.getOptionalData1().isEmpty()) {

            return exceptionHandlerUtil
                    .createErrorResponse("api.error.optional.data.is.empty");
        }

        int count = isOptionData1Present(subscriberObData.getOptionalData1());

        if (count == 1) {

            String suid = onboardingDataRepoIface
                    .getOptionalData1Subscriber(subscriberObData.getOptionalData1());

            if (!suid.equals(obRequestDTO.getSuID())) {

                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.onboarding.can.not.be.processed.because.the.same.national.id.already.exists");
            }
        }

        return null;
    }
    private ApiResponse validateDevice(SubscriberObRequestDTO obRequestDTO) {

        System.out.println("ObRequestDTO:::"+obRequestDTO);
        OnbSubscriberDevice subscriberDevice =
                deviceRepoIface.getSubscriber(obRequestDTO.getSuID());
        System.out.println("ghujk"+subscriberDevice);


        if (subscriberDevice.getDeviceStatus().equals(Constant.DEVICE_STATUS_DISABLED)) {

            return exceptionHandlerUtil
                    .createErrorResponse("api.error.this.device.is.disabled");
        }

        return null;
    }
    private ApiResponse validateDocument(
            SubscriberObData subscriberObData,
            SubscriberObRequestDTO obRequestDTO) {

        int idDocCount =
                subscriberRepoIface.getIdDocCount(subscriberObData.getDocumentNumber());

        int idDocNumberCount =
                subscriberRepoIface.getSubscriberIdDocNumber(
                        subscriberObData.getDocumentNumber(),
                        obRequestDTO.getSuID());

        if (idDocCount > 0 && idDocNumberCount == 0) {

            return exceptionHandlerUtil
                    .createErrorResponse("api.error.this.document.is.already.onboarded");
        }

        return null;
    }
    private String buildFullName(SubscriberObData subscriberObData) {

        return Stream.of(
                        subscriberObData.getSecondaryIdentifier(),
                        subscriberObData.getPrimaryIdentifier())
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(s -> s.replaceAll("\\s+", " ").trim())
                .collect(Collectors.joining(" "));
    }
    private void updateSubscriber(
            OnbSubscriber subscriber,
            SubscriberObData subscriberObData,
            SubscriberObRequestDTO obRequestDTO,
            String fullName) {

        subscriber.setFullName(fullName);
        subscriber.setDateOfBirth(subscriberObData.getDateOfBirth());
        subscriber.setIdDocType(subscriberObData.getDocumentType());
        subscriber.setIdDocNumber(subscriberObData.getDocumentNumber());
        subscriber.setUpdatedDate(AppUtil.getDate());
        subscriber.setSubscriberUid(obRequestDTO.getSuID());

        if (!Constant.RESIDENT.equals(obRequestDTO.getSubscriberType())) {

            subscriber.setNationalId(subscriberObData.getOptionalData1());
        }
    }

	@Async
	public void saveFaceFeaturesAsync(FaceFeaturesDto faceFeaturesDto, String faceUrl, RestTemplate restTemplate) {
		try {
			logger.info(CLASS + " Calling Face Features Save URL {} " , faceFeaturesDto);
			validateUrl(faceUrl);

			// Set up headers and request entity
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> reqEntity = new HttpEntity<>(faceFeaturesDto, headers);
			// Send POST request asynchronously
			ResponseEntity<ApiResponse> res = restTemplate.exchange(faceUrl, HttpMethod.POST, reqEntity,
					ApiResponse.class);
			// Return the result in a CompletableFuture
			logger.info("saveFaceFeaturesAsync res: {} " ,   res);
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " Error in saveFaceFeaturesAsync: {} ", e.getMessage());
		}
	}

	public ApiResponse getImageWithOutBackGround(String idDocNumber, String selfie) {
		String boarderControlImage;
		logger.info(" Fetch Border Controll image getImageWithOutBackGround doc number  {}  " , idDocNumber);
		String image = subscriberRepoIface.getSimulatedBoarderControlImage(idDocNumber);
        try {
            String jsonString;

            if (selfie != null && !selfie.isEmpty()) {
                jsonString = createJsonString(selfie);
            } else {
                jsonString = createJsonString(image);
            }
			HttpHeaders headers2 = new HttpHeaders();
			headers2.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> reqEntity2 = new HttpEntity<>(jsonString, headers2);
			ResponseEntity<ApiResponse> res2 = restTemplate.exchange(removeBackGroundFromImageURL, HttpMethod.POST,
					reqEntity2, ApiResponse.class);
            if (res2.getStatusCode() == HttpStatus.OK
                    || res2.getStatusCode() == HttpStatus.CREATED) {

                var body = res2.getBody();

                if (body != null && body.getResult() != null) {
                    boarderControlImage = body.getResult().toString();
                } else {
                    boarderControlImage = image;
                }

            } else {
                boarderControlImage = image;
            }
		} catch (Exception e) {
            logger.info(" getImageWithOutBackGround ");
				logger.error(UNEXPECTED_EXCEPTION, e);
			boarderControlImage = image;
		}
		return AppUtil.createApiResponse(true, "fetch image without background", boarderControlImage);
	}

	public static String createJsonString(String image) {
		// Use string concatenation to build the JSON

		return "{\n" + "\"image\": \"" + image + "\"\n" + "}";
	}
    private void validateUrl(String url) {

        if (url == null || url.trim().isEmpty()) {
            throw new ApplicationException("URL cannot be null or empty");
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (Exception e) {
            throw new ApplicationException("Invalid URL format", e);
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new ApplicationException("Only HTTPS protocol is allowed");
        }

        String allowedHost = "internal-edms.company.com";

        if (!allowedHost.equalsIgnoreCase(uri.getHost())) {
            throw new ApplicationException("Unauthorized host detected: " + uri.getHost());
        }

        try {
            InetAddress address = InetAddress.getByName(uri.getHost());

            if (address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isSiteLocalAddress()) {

                throw new ApplicationException("Access to internal/private IPs is not allowed");
            }

        } catch (Exception e) {
            throw new ApplicationException("Unable to resolve host", e);
        }
    }

    private OnbSubscriber getSubscriber(GetSubscriberObDataDTO subscriberUID) {

        OnbSubscriber subscriber =
                subscriberRepoIface.findBysubscriberUid(subscriberUID.getSuid());

        if (subscriber == null) {
            throw new ApplicationException(API_ERROR_SUBSCRIBER_DETAIS_NOT_FOUND);
        }

        return subscriber;
    }

    @Override
    public ApiResponse getSubscriberObData(HttpServletRequest httpServletRequest,
                                           GetSubscriberObDataDTO subscriberUID) {

        logger.info("{} getSubscriberObData req {}", CLASS, subscriberUID);

        try {
            ApiResponse validationResponse = validateSubscriberRequest(subscriberUID);
            if (validationResponse != null) {
                return validationResponse;
            }

            OnbSubscriber subscriber = getSubscriber(subscriberUID);

            // Update version async
            updateSubscriberVersionAsync(httpServletRequest, subscriber);

            // Fetch required data
            PaymentStatusHolder paymentStatusHolder = getPaymentStatuses(subscriberUID);
            String certStatus = getCertStatus(subscriberUID);

            OnbSubscriberOnboardingData data = getLatestOnboardingData(
                    onboardingDataRepoIface.getBySubUid(subscriberUID.getSuid()));

            OnbSubscriberStatus status =
                    statusRepoIface.findBysubscriberUid(subscriberUID.getSuid());

            // Build response
            return buildSubscriberResponse(
                    subscriberUID,
                    subscriber,
                    data,
                    status,
                    paymentStatusHolder,
                    certStatus
            );

        }
        catch (ApplicationException e) {

            logger.warn("{} Business exception {}", CLASS, e.getMessage());
            return exceptionHandlerUtil.createErrorResponse(e.getMessage());

        }
        catch (Exception e) {

            logger.error("{} Unexpected exception", CLASS, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
    @SuppressWarnings("unused")


    private ApiResponse buildSubscriberResponse(
            GetSubscriberObDataDTO subscriberUID,
            OnbSubscriber subscriber,
            OnbSubscriberOnboardingData data,
            OnbSubscriberStatus status,
            PaymentStatusHolder paymentStatusHolder,
            String certStatus) {

        try {

            if (data == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.id.not.matched");
            }

            SubscriberObRequestDTO obRequestDTO = new SubscriberObRequestDTO();
            SubscriberObData onboardingData = prepareOnboardingData(subscriber, data);

            JsonNode root = objectMapper.readTree(data.getNiraResponse());
            JsonNode dataNode = root.path(CUSTOMER_DETAILS).path(RESULT).path("Data");

            obRequestDTO.setResidenceInfo(dataNode.path("ResidenceInfo"));
            obRequestDTO.setActivePassport(dataNode.path("ActivePassport"));

            applySelfieLogic(subscriberUID, data, obRequestDTO, onboardingData);

            applyNinExpiryLogic(data, onboardingData);

            applyNonOnboardingFeeLogic(subscriber, data, onboardingData, obRequestDTO);

            applySubscriberDataToRequest(subscriber, data, status, onboardingData, obRequestDTO);

            applyPaymentStatus(obRequestDTO, paymentStatusHolder);

            applyCertificateStatus(obRequestDTO, certStatus, paymentStatusHolder.getPaymentCertStatus());

            applyTotpIfRequired(subscriber, obRequestDTO);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.subscriber.onboarding.data",
                    obRequestDTO);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {

            throw new ApplicationException("Error while parsing NIRA response JSON", e);
        }
    }
    private ApiResponse validateSubscriberRequest(GetSubscriberObDataDTO subscriberUID) {

        if (subscriberUID == null) {
            return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.id.cant.be.empty");
        }

        String result = ValidationUtil.validate(subscriberUID);

        if (result != null) {
            logger.info("Validation errors getSubscriberObData: {}", result);
            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(result, null);
        }

        return null;
    }
    private void updateSubscriberVersionAsync(HttpServletRequest httpServletRequest,
                                              OnbSubscriber subscriber) {

        try (ExecutorService executor = Executors.newFixedThreadPool(20)) {

            Runnable visitorWorkerThread =
                    new VersionComparatorThread(subscriber, subscriberRepoIface, httpServletRequest);

            executor.execute(visitorWorkerThread);

        } catch (Exception e) {

            logger.error(UNEXPECTED_EXCEPTION, e);
        }
    }
    private PaymentStatusHolder getPaymentStatuses(GetSubscriberObDataDTO subscriberUID) {

        PaymentStatusHolder holder = new PaymentStatusHolder();

        holder.setPaymentStatus(
                subscriberRepoIface.subscriberPaymnetStatus(subscriberUID.getSuid()));

        List<String> statuss =
                subscriberRepoIface.subscriberPaymnetInitaiatedStatus(subscriberUID.getSuid());

        holder.setPaymentIntiatiedStatus(statuss.isEmpty() ? null : statuss.get(0));

        List<String> statuses =
                subscriberRepoIface.subscriberPaymnetCertStatus(subscriberUID.getSuid());

        holder.setPaymentCertStatus(statuses.isEmpty() ? null : statuses.get(0));

        return holder;
    }
    private String getCertStatus(GetSubscriberObDataDTO subscriberUID) {

        List<String> list =
                subscriberRepoIface.getCertStatus(subscriberUID.getSuid());

        return list.isEmpty() ? null : list.get(0);
    }
    private OnbSubscriberCertificate getSubscriberCertificates(GetSubscriberObDataDTO subscriberUID) {

        List<OnbSubscriberCertificate> certificates =
                subscriberCertificatesRepoIface.findBySubscriberUniqueId(subscriberUID.getSuid());

        return certificates.isEmpty() ? null : certificates.get(0);
    }
    private OnbSubscriberOnboardingData getLatestOnboardingData(List<OnbSubscriberOnboardingData> dataList) {
        OnbSubscriberOnboardingData data = null;
        if (!dataList.isEmpty()) {

            if (dataList.size() > 1) {

                data = findLatestOnboardedSub(dataList);

            } else {

                data = dataList.get(0);

            }
        }

        return data;
    }


    private void applySelfieLogic(GetSubscriberObDataDTO subscriberUID,
                                  OnbSubscriberOnboardingData data,
                                  SubscriberObRequestDTO obRequestDTO,
                                  SubscriberObData onboardingData) {

        if (!subscriberUID.isSelfieRequired()) {
            return;
        }

        if (!isOnboardingFee) {
            handleNonOnboardingFee(data, obRequestDTO, onboardingData);
            return;
        }

        ApiResponse response = getSubscriberSelfie(data.getSelfieUri());

        handleBoarderControlPhoto(data, obRequestDTO, response);

        if (response.isSuccess()) {
            onboardingData.setSubscriberSelfie((String) response.getResult());
        } else {
            onboardingData.setSubscriberSelfie("");
        }
    }

    private void handleBoarderControlPhoto(OnbSubscriberOnboardingData data,
                                           SubscriberObRequestDTO obRequestDTO,
                                           ApiResponse response) {

        if (boarderControllPhotoRequired) {

            if (data.getVerifierProvidedPhoto() != null && !data.getVerifierProvidedPhoto().isEmpty()) {

                logger.info("Inside isSelfieRequired boarderControlPhoto is not null");
                obRequestDTO.setBoarderControlPhoto(data.getVerifierProvidedPhoto());
            }

        } else if (response.isSuccess()) {

            obRequestDTO.setBoarderControlPhoto((String) response.getResult());
        }
    }

    private void handleNonOnboardingFee(OnbSubscriberOnboardingData data,
                                        SubscriberObRequestDTO obRequestDTO,
                                        SubscriberObData onboardingData) {

        obRequestDTO.setBoarderControlPhoto(data.getSelfie());
        onboardingData.setSubscriberSelfie(data.getSelfie());
    }


    private void applyNinExpiryLogic(OnbSubscriberOnboardingData data,
                                     SubscriberObData onboardingData) {

        if ("NIN".equalsIgnoreCase(data.getOnboardingMethod())) {

            logger.info(" data.getOnboardingMethod() :: {}", data.getOnboardingMethod());

            LocalDateTime expiry =
                    LocalDateTime.now().plusYears(1).toLocalDate().atStartOfDay();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            onboardingData.setDateOfExpiry(expiry.format(formatter));
        }
    }
    private void applyPaymentStatus(SubscriberObRequestDTO obRequestDTO,
                                    PaymentStatusHolder paymentStatusHolder) {

        if (!paymentStatusHolder.getPaymentStatus().isEmpty()) {

            obRequestDTO.setOnboardingPaymentStatus(
                    paymentStatusHolder.getPaymentStatus().get(0));

        } else if (paymentStatusHolder.getPaymentIntiatiedStatus() != null) {

            obRequestDTO.setOnboardingPaymentStatus(Constant.PAYMENT_STATUS_INITIATED);

        } else {

            obRequestDTO.setOnboardingPaymentStatus(Constant.PAYMENT_STATUS_PENDING);
        }
    }
    private void applyCertificateStatus(SubscriberObRequestDTO obRequestDTO,
                                        String certStatus,
                                        String paymentCertStatus) {

        if (certStatus == null) {

            obRequestDTO.setCertStatus(Constant.PENDING);

        } else if (certStatus.equalsIgnoreCase(Constant.FAIL)
                || certStatus.equals(Constant.FAILED)) {

            obRequestDTO.setCertStatus(Constant.FAILED);

        } else if (certStatus.equalsIgnoreCase(Constant.CERT_REVOKED)
                || certStatus.equals(Constant.REVOKED)) {

            if (paymentCertStatus == null) {

                obRequestDTO.setCertStatus(Constant.REVOKED);
                obRequestDTO.setOnboardingPaymentStatus(Constant.PAYMENT_STATUS_PENDING);

            } else if (paymentCertStatus.equalsIgnoreCase(Constant.SUCCESS)) {

                obRequestDTO.setCertStatus(Constant.REVOKED);
                obRequestDTO.setOnboardingPaymentStatus(Constant.PAYMENT_STATUS_SUCCESS);

            } else if (paymentCertStatus.equalsIgnoreCase(Constant.FAILED)) {

                obRequestDTO.setCertStatus(Constant.REVOKED);
                obRequestDTO.setOnboardingPaymentStatus(Constant.PAYMENT_STATUS_FAILED);

            } else {

                obRequestDTO.setCertStatus(Constant.REVOKED);
                obRequestDTO.setOnboardingPaymentStatus(Constant.PAYMENT_STATUS_INITIATED);
            }

        } else {

            obRequestDTO.setCertStatus(certStatus);
        }
    }
    private void applyTotpIfRequired(OnbSubscriber subscriber,
                                     SubscriberObRequestDTO obRequestDTO) {

        if (priAuthSchemeBoolean) {

            try {

                TotpDto totpDto = new TotpDto();

                totpDto.setSuid(subscriber.getSubscriberUid());
                totpDto.setFullName(subscriber.getFullName());
                totpDto.setPriAuthScheme(priAuthScheme);

                ApiResponse totpApiResponse = getTotp(totpDto);

                String totpResp = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(totpApiResponse.getResult());

                TotpDtoResp totpDtoResp =
                        objectMapper.readValue(totpResp, TotpDtoResp.class);

                if (totpDtoResp == null) {

                    throw new ApplicationException(
                            "api.error.authentication.data.should.not.be.null.or.empty.subscriber.onBoarding.data.not.saved");
                }

                obRequestDTO.setTotpResp(totpDtoResp);

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {

                throw new ApplicationException("Error while processing TOTP response", e);
            }
        }
    }

    private SubscriberObData prepareOnboardingData(OnbSubscriber subscriber,
                                                   OnbSubscriberOnboardingData data) {

        try {

            SubscriberObData onboardingData = new SubscriberObData();

            onboardingData.setDateOfBirth(subscriber.getDateOfBirth());
            onboardingData.setDocumentCode(data.getIdDocCode());
            onboardingData.setDocumentNumber(data.getIdDocNumber());
            onboardingData.setDocumentType(data.getIdDocType());
            onboardingData.setSubscriberUniqueId(data.getSubscriberUid());

            ObjectMapper mapper = new ObjectMapper();

            onboardingData = mapper.readValue(
                    data.getOnboardingDataFieldsJson(),
                    SubscriberObData.class);

            onboardingData.setNationality(onboardingData.getNationality());

            return onboardingData;

        } catch (Exception e) {

            throw new ApplicationException("Error while preparing onboarding data", e);
        }
    }
    private void applyNonOnboardingFeeLogic(OnbSubscriber subscriber,
                                            OnbSubscriberOnboardingData data,
                                            SubscriberObData onboardingData,
                                            SubscriberObRequestDTO obRequestDTO) {

        if (!isOnboardingFee) {

            onboardingData.setNiraResponse("{\"uaeKycId\":\"" + data.getUaeKycId() + "\"}");

            obRequestDTO.setPassportNumber(subscriber.getPassportNumber());
            obRequestDTO.setEmiratesIdNumber(subscriber.getNationalIdNumber());
            obRequestDTO.setEmiratesIdDocumentNumber(subscriber.getNationalIdCardNumber());
        }
    }
    private void applySubscriberDataToRequest(OnbSubscriber subscriber,
                                              OnbSubscriberOnboardingData data,
                                              OnbSubscriberStatus status,
                                              SubscriberObData onboardingData,
                                              SubscriberObRequestDTO obRequestDTO) {

        obRequestDTO.setSubscriberData(onboardingData);
        obRequestDTO.setSubscriberType(data.getSubscriberType());
        obRequestDTO.setConsentId(1);
        obRequestDTO.setSuID(data.getSubscriberUid());
        obRequestDTO.setOnboardingMethod(data.getOnboardingMethod());
        obRequestDTO.setLevelOfAssurance(data.getLevelOfAssurance());
        obRequestDTO.setTemplateId(data.getTemplateId());
        obRequestDTO.setOnboardingApprovalStatus(status.getSubscriberStatus());

        if (subscriber.getMobileNumber() != null) {
            obRequestDTO.setMobileNo(subscriber.getMobileNumber());
        }

        if (subscriber.getEmailId() != null) {
            obRequestDTO.setEmailId(subscriber.getEmailId());
        }

        if (subscriber.getTitle() != null) {
            obRequestDTO.setTitle(subscriber.getTitle());
        }
    }


	@Override
	public ApiResponse getVerificationChannelResponse(HttpServletRequest request, String subscriberUID) {
		try {
			logger.info(" getVerificationChannelResponse suid ::  {} " , subscriberUID);
			JsonNode root = null;
			OnbSubscriberOnboardingData subscriberOnboardingData = new OnbSubscriberOnboardingData();
			List<OnbSubscriberOnboardingData> subscriberOnboardingDataList = onboardingDataRepoIface
					.getBySubUid(subscriberUID);
			if (!subscriberOnboardingDataList.isEmpty()) {
				if (subscriberOnboardingDataList.size() > 1) {
					subscriberOnboardingData = findLatestOnboardedSub(subscriberOnboardingDataList);
				} else {
					subscriberOnboardingData = subscriberOnboardingDataList.get(0);
				}
			}

			if (subscriberOnboardingData != null) {
				root = objectMapper.readTree(subscriberOnboardingData.getNiraResponse());
				ObjectNode dataNode = (ObjectNode) root.path(CUSTOMER_DETAILS).path(RESULT).path("Data");
				// Set fields to explicit JSON null
				dataNode.putNull("ImmigrationFile");
				dataNode.putNull("TravelDetail");
				dataNode.putNull("Documents");
				return exceptionHandlerUtil.createSuccessResponse("api.response.verification.channel.response",
						dataNode);
			} else {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_NO_DATA_FOUND);
			}
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
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
            logger.info("json => {} " , json);
			Result checksumResult = DAESService.addChecksumToTransaction(json);
			String push = new String(checksumResult.getResponse());
			LogModelDTO log = objectMapper.readValue(push, LogModelDTO.class);
			mqSender.send(log);
		} catch (Exception e) {
			logger.error("Set LogModel Exception {}", e.getMessage());
				logger.error(UNEXPECTED_EXCEPTION, e);
		}
	}

    @Override
    public ApiResponse resetPin(GetSubscriberObDataDTO subscriberObDataDTO) {

        OnbSubscriberOnboardingData onboardingData = new OnbSubscriberOnboardingData();
        ResetPinDTO pinDTO = new ResetPinDTO();

        String result = ValidationUtil.validate(subscriberObDataDTO);
        if (result != null) {
            logger.info("Validation errors: {} ", result);
            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(result, null);
        }

        try {

            List<OnbSubscriberOnboardingData> onboardingDataList =
                    onboardingDataRepoIface.getBySubUid(subscriberObDataDTO.getSuid());

            if (onboardingDataList != null) {
                onboardingData = getOnboardingData(onboardingDataList);
            }

            if (onboardingData == null) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_NO_DATA_FOUND);
            }

            pinDTO.setIdDocNumber(onboardingData.getIdDocNumber());

            handleSelfie(subscriberObDataDTO, onboardingData, pinDTO);

            return exceptionHandlerUtil.createSuccessResponse("api.response.reset.pin.data", pinDTO);

        } catch (Exception e) {
            logger.error(CLASS + "resetPin Exception {}", e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }
    private OnbSubscriberOnboardingData getOnboardingData(List<OnbSubscriberOnboardingData> list) {

        if (list.size() > 1) {
            return findLatestOnboardedSub(list);
        } else {
            return list.get(0);
        }
    }
    private void handleSelfie(GetSubscriberObDataDTO subscriberObDataDTO,
                              OnbSubscriberOnboardingData onboardingData,
                              ResetPinDTO pinDTO) {

        if (!subscriberObDataDTO.isSelfieRequired()) {
            pinDTO.setSelfie(null);
            return;
        }

        if (isOnboardingFee) {
            pinDTO.setSelfie(onboardingData.getSelfie());
            return;
        }

        ApiResponse response = getSubscriberSelfie(onboardingData.getSelfieUri());

        if (response.isSuccess()) {
            pinDTO.setSelfie((String) response.getResult());
        }
    }

    @Override
    public ApiResponse getSubscriberSelfie(String uri) {

        logger.info(CLASS + " getBase64String uri {}", uri);

        try {

            validateUrl(uri);

            HttpHeaders headersForGet = new HttpHeaders();
            HttpEntity<Object> requestEntityForGet = new HttpEntity<>(headersForGet);

            ResponseEntity<Resource> downloadUrlResult =
                    restTemplate.exchange(uri, HttpMethod.GET, requestEntityForGet, Resource.class);

            Resource resource = downloadUrlResult.getBody();

            if (resource == null) {
                throw new ApplicationException("Image resource is null");
            }

            byte[] buffer = IOUtils.toByteArray(resource.getInputStream());

            String image2 = Base64.getEncoder().encodeToString(buffer);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.base64.of.image.fetched.successfully",
                    image2);

        } catch (Exception e) {

            logger.error(CLASS + " getBase64String Exception {}", e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);

            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public ResponseEntity<Object> getVideoLiveStreaming(String subscriberUid) {

        logger.info(CLASS + "getVideoLiveStreaming subscriberUid {}", subscriberUid);

        try {

            if (subscriberUid == null || subscriberUid.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(exceptionHandlerUtil.createErrorResponse(
                                API_ERROR_SUBSCRIBERUID_CANNOT_BE_NULL));
            }

            String url = subscriberRepoIface.getSubscriberUid(subscriberUid);

            if (url != null) {

                validateUrl(url);

                HttpHeaders headersForGet = new HttpHeaders();
                HttpEntity<Object> requestEntityForGet = new HttpEntity<>(headersForGet);

                ResponseEntity<Resource> downloadUrlResult =
                        restTemplate.exchange(url, HttpMethod.GET,
                                requestEntityForGet, Resource.class);

                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", "video/mp4")
                        .body(downloadUrlResult.getBody());

            } else {

                logger.info(CLASS + "getVideoLiveStreaming No video found {}", HttpStatus.NOT_FOUND);

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(exceptionHandlerUtil.createErrorResponse(
                                "api.error.no.video.found"));
            }

        } catch (Exception e) {

            logger.error(CLASS + "getVideoLiveStreaming Exception {}", e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(exceptionHandlerUtil.handleException(e));
        }
    }

	public static OnbSubscriberOnboardingData findLatestOnboardedSub(
			List<OnbSubscriberOnboardingData> subscriberOnboardingData) {
        Date[] dates = new Date[subscriberOnboardingData.size()];

        int i = 0;
        SimpleDateFormat simpleDateFormat = null;
        for (OnbSubscriberOnboardingData s : subscriberOnboardingData) {

            try {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = simpleDateFormat.parse(s.getCreatedDate());

                dates[i] = date;
                i++;
            } catch (Exception e) {
                logger.error(UNEXPECTED_EXCEPTION, e);
            }
        }

        Date latestDate = getLatestDate(dates);

        if (simpleDateFormat == null) {
            throw new ApplicationException("Date formatter is not initialized");
        }

        String latestDateString = simpleDateFormat.format(latestDate);

        for (OnbSubscriberOnboardingData s : subscriberOnboardingData) {
            if (s.getCreatedDate() != null && s.getCreatedDate().equals(latestDateString)) {
                return s;
            }
        }

        return null;
    }
	public static Date getLatestDate(Date[] dates) {
		Date latestDate = null;
		if ((dates != null) && (dates.length > 0)) {
			for (Date date : dates) {
				if (date != null) {
					if (latestDate == null) {
						latestDate = date;
					}
					latestDate = date.after(latestDate) ? date : latestDate;
				}
			}
		}
		return latestDate;
	}

	private String getTimeStampString() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return f.format(new Date());
	}

	@Override
	public ResponseEntity<Object> getVideoLiveStreamingLocalEdms(String subscriberUid) {
		try {

			if (subscriberUid == null || subscriberUid.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBERUID_CANNOT_BE_NULL));
			}

			logger.info(CLASS + "getVideoLiveStreamingLocalEdms req subscriberUid {}", subscriberUid);
            if (StringUtils.hasText(subscriberUid)) {
				String url = livelinessRepository.getSubscriberUid(subscriberUid);
				if (!Objects.isNull(url)) {
					validateUrl(url);
					HttpHeaders headersForGet = new HttpHeaders();
					HttpEntity<Object> requestEntityForGet = new HttpEntity<>(headersForGet);
					ResponseEntity<Resource> downloadUrlResult = restTemplate.exchange(url, HttpMethod.GET,
							requestEntityForGet, Resource.class);

					return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "video/mp4")
							.body(downloadUrlResult.getBody());
				} else {

					logger.error(CLASS + "getVideoLiveStreamingLocalEdms No video found {}", HttpStatus.NOT_FOUND);
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(exceptionHandlerUtil.createErrorResponse("api.error.no.video.found"));
				}
			} else {
				logger.error(CLASS + "getVideoLiveStreamingLocalEdms Subscriber not found {}", HttpStatus.NOT_FOUND);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND));

			}
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {

			logger.error(CLASS + "saveSubscriberData Exception {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					AppUtil.createApiResponse(false, API_ERROR_SOMETHING_WENT_WRONG, null));
		} catch (Exception e) {
			logger.error(CLASS + "getVideoLiveStreamingLocalEdms Exception {}", e.getMessage());
				logger.error(UNEXPECTED_EXCEPTION, e);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					AppUtil.createApiResponse(false, API_ERROR_SOMETHING_WENT_WRONG, null));
		}
	}

    @Override
    public ApiResponse addTrustedUsers(TrustedUserDto emails) {
        try {

            List<String> emailsListDb = trustedUserRepoIface.getTrustedEmails();
            List<String> secondList = new ArrayList<>();
            List<OnbTrustedUser> saveTrustedUser = new ArrayList<>();

            logger.info(CLASS + "addTrustedUsers emailsListDb {}", emailsListDb);
            logger.info(CLASS + "addTrustedUsers secondList {}", secondList);

            if (Objects.isNull(emails) || CollectionUtils.isEmpty(emails.getEmails())) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.trusted.user.email.list.is.empty");
            }

            if (CollectionUtils.isEmpty(emailsListDb)) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.trusted.user.email.list.is.empty");
            }

            for (TrustedEmails trustedEmails : emails.getEmails()) {
                secondList.add(trustedEmails.getEmail());
            }

            secondList.retainAll(emailsListDb);

            if (!secondList.isEmpty()) {
                return exceptionHandlerUtil.createErrorResponseWithResult(
                        "api.error.duplicate.emails.are.present", secondList);
            }

            for (TrustedEmails trustedEmails : emails.getEmails()) {
                saveTrustedUser.add(saveTrustedUsers(trustedEmails));
            }

            trustedUserRepoIface.saveAll(saveTrustedUser);

            return exceptionHandlerUtil.successResponse(
                    "api.response.list.save.successfully");

        } catch (Exception e) {
            logger.error(UNEXPECTED_EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

	public OnbTrustedUser saveTrustedUsers(TrustedEmails trustedEmails) {
		OnbTrustedUser trustedUser = new OnbTrustedUser();
		trustedUser.setEmailId(trustedEmails.getEmail());
		trustedUser.setFullName(trustedEmails.getName());
		trustedUser.setMobileNumber(trustedEmails.getMobileNo());
		trustedUser.setTrustedUserStatus(trustedUserStatus);
		return trustedUser;
	}

	@Override
	public ApiResponse getSubscriberDetailsReports(String startDate, String endDate) {
		try {
			logger.info(CLASS + "getSubscriberDetailsReport req startDate {} and endDate {}", startDate, endDate);
			if (startDate != null && endDate != null) {
				List<OnbSubscriberCertificateDetails> completeDetail = subscriberCertificateDetailsRepoIface
						.getSubscriberReports(startDate, endDate);
				List<SubscriberReportsResponseDto> details = new ArrayList<>();

				if (Objects.nonNull(completeDetail) && !completeDetail.isEmpty()) {

					for (OnbSubscriberCertificateDetails subscriberCompleteDetail : completeDetail) {
						SubscriberReportsResponseDto reportsResponseDto = new SubscriberReportsResponseDto();
						reportsResponseDto.setFullName(subscriberCompleteDetail.getFullName());
						reportsResponseDto.setIdDocNumber(subscriberCompleteDetail.getIdDocNumber());
						reportsResponseDto.setOnboardingMethod(subscriberCompleteDetail.getOnboardingMethod());
						reportsResponseDto
								.setCertificateSerialNumber(subscriberCompleteDetail.getCertificateSerialNumber());
						reportsResponseDto
								.setCertificateIssueDate(subscriberCompleteDetail.getCertificateIssueDate());
						reportsResponseDto
								.setCerificateExpiryDate(subscriberCompleteDetail.getCerificateExpiryDate());
						details.add(reportsResponseDto);
					}
					logger.info(
							CLASS + "getSubscriberDetailsReports Succssfully fetched subscriber certificate details {}",
							details);

					return exceptionHandlerUtil.createSuccessResponse(
							"api.error.successfully.fetched.subscriber.certificate.details", details);
				} else {
					logger.info(CLASS + " getSubscriberDetailsReports No Records Found");
					return exceptionHandlerUtil.createErrorResponse("api.response.no.records.found");

				}
			} else {
				logger.info(CLASS + "getSubscriberDetailsReports Date cant should be null or empty");
				return exceptionHandlerUtil.createErrorResponse("api.error.date.cant.should.be.null.or.empty");

			}
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " getSubscriberDetailsReports Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	int isOptionData1Present(String optionalData1) {

      return onboardingDataRepoIface.getOptionalData1(optionalData1);
	}

	@Override
	public ApiResponse updatePhoneNumber(UpdateDto updateDto) {
		try {
			logger.info(CLASS + " updatePhoneNumber Suid {}",updateDto.getMobileNumber());
			if (updateDto.getSuid() == null || updateDto.getSuid().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBERUID_CANNOT_BE_NULL);

			}
			if (updateDto.getMobileNumber() == null || updateDto.getMobileNumber().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse("api.error.mobile.number.cant.be.null.or.empty");
			}

			Date d1 = subscriberHistoryRepo.getLatestForMobile(updateDto.getSuid());
			logger.info(CLASS + " updatePhoneNumber latest date {} ", d1);
			if (d1 != null) {
				Date d2 = AppUtil.getCurrentDate();
				long differenceInTime = d2.getTime() - d1.getTime();
				long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365;
				logger.info("differenceInDays {} " , differenceInDays);
				if (differenceInDays <= 30) {

					return exceptionHandlerUtil.createErrorResponse(
							"api.error.cant.change.the.phone.number.because.you.changed.it.recently");

				}
			}

			OnbSubscriber sub = subscriberRepoIface.findBymobileNumber(updateDto.getMobileNumber());

			OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(updateDto.getSuid());
			if (subscriber == null) {
				return AppUtil.createApiResponse(false, API_ERROR_SUBSCRIBER_NOT_FOUND, null);
			}
			if (subscriber.getMobileNumber().equals(updateDto.getMobileNumber())) {

				return exceptionHandlerUtil
						.createErrorResponse("api.error.your.old.number.and.entered.mobile.number.are.same");
			}
			if (sub != null) {

				return exceptionHandlerUtil.createErrorResponse("api.error.this.mobile.number.is.already.in.use");

			}
			// create new subHistory instance and save old records
			OnbSubscriberContactHistory subscriberContactHistory = new OnbSubscriberContactHistory();
			subscriberContactHistory.setSubscriberUid(subscriber.getSubscriberUid());
			subscriberContactHistory.setMobileNumber(subscriber.getMobileNumber());
			subscriberContactHistory.setCreatedDate(AppUtil.getCurrentDate());
			subscriberHistoryRepo.save(subscriberContactHistory);

			// update subscriber phone
			subscriber.setMobileNumber(updateDto.getMobileNumber());
			subscriberRepoIface.save(subscriber);

			return exceptionHandlerUtil.createSuccessResponse("api.error.phone.number.updated", subscriber);

		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " updatePhoneNumber Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);

		}
	}

	@Override
	public ApiResponse updateEmail(UpdateDto updateDto) {
		try {
			logger.error(CLASS + " updatePhoneNumber Suid {}", updateDto.getSuid());
			if (updateDto.getSuid() == null || updateDto.getSuid().isEmpty()) {

				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBERUID_CANNOT_BE_NULL);

			}
			if (updateDto.getEmail() == null || updateDto.getEmail().isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse("api.error.email.id.cant.be.empty");
			}
			Date d1 = subscriberHistoryRepo.getLatestForEmail(updateDto.getSuid());
			if (d1 != null) {
				Date d2 = AppUtil.getCurrentDate();
				long differenceInTime = d2.getTime() - d1.getTime();
				long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365;
                logger.info("differenceInDays {} " , differenceInDays);
				if (differenceInDays <= 30) {

					return exceptionHandlerUtil
							.createErrorResponse("api.error.cant.change.the.email.because.you.changed.it.recently");
				}
			}

			OnbSubscriber sub = subscriberRepoIface.findByemailId(updateDto.getEmail());

			OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(updateDto.getSuid());
			if (subscriber == null) {

				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);

			}
			if (subscriber.getEmailId().equals(updateDto.getEmail())) {

				return exceptionHandlerUtil
						.createErrorResponse("api.error.your.old.email.and.entered.emailId.are.same");

			}
			// checking if entered mail is already in use with other subscriber
			if (sub != null) {

				return exceptionHandlerUtil.createErrorResponse("api.error.this.email.is.already.in.use");

			}

			int orgEmailCount = onbOrgContactsEmailRepository.findByOrgEmailAndNotUgPassEmail(updateDto.getEmail(),
					subscriber.getEmailId());
			int orgMobileCount = onbOrgContactsEmailRepository.findByOrgEmailAndNotMobile(updateDto.getEmail(),
					subscriber.getMobileNumber());
			int orgNinCount = onbOrgContactsEmailRepository.findByOrgEmailAndNotNin(updateDto.getEmail(),
					subscriber.getIdDocNumber());
			int orgPassportCount = onbOrgContactsEmailRepository.findByOrgEmailAndNotPassport(updateDto.getEmail(),
					subscriber.getIdDocNumber());
			if (orgEmailCount != 0 || orgPassportCount != 0 || orgMobileCount != 0 || orgNinCount != 0) {

				return exceptionHandlerUtil.createErrorResponse(
						"api.error.this.email.is.already.registered.with.another.organization.subscriber.email");

			}

			// create new subHistory instance and save old records
			OnbSubscriberContactHistory subscriberContactHistory = new OnbSubscriberContactHistory();
			subscriberContactHistory.setSubscriberUid(subscriber.getSubscriberUid());
			subscriberContactHistory.setEmailId(subscriber.getEmailId());
			subscriberContactHistory.setCreatedDate(AppUtil.getCurrentDate());
			subscriberHistoryRepo.save(subscriberContactHistory);


			subscriber.setEmailId(updateDto.getEmail());
			subscriberRepoIface.save(subscriber);

			return exceptionHandlerUtil.createSuccessResponse("api.response.email.updated", subscriber);

		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " updatePhoneNumber Exception ", e.getMessage());
			return exceptionHandlerUtil.handleException(e);

		}
	}

    @Override
    public ApiResponse sendOtpEmail(UpdateOtpDto otpDto) {
        try {
            if (otpDto.getEmail() == null || otpDto.getEmail().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse("api.error.email.id.cant.be.empty");
            }
            OTPResponseDTO otpResponse = new OTPResponseDTO();

            if (otpDto.getEmail().equals(testAndroidEmail) || otpDto.getEmail().equals(testIosEmail)) {
                ApiResponse apiResponseDemo = verifyOtp(null, otpDto.getEmail());
                if (apiResponseDemo.isSuccess()) {

                    return exceptionHandlerUtil.createSuccessResponse("api.response.ok", apiResponseDemo.getResult());

                }
            }

            String emailOTP = generateOtp(6);
            System.out.println("emailOTP >> " + emailOTP + " : " + AppUtil.encrypt(emailOTP));
            EmailReqDto dto = new EmailReqDto();
            dto.setEmailOtp(emailOTP);
            dto.setEmailId(otpDto.getEmail());
            dto.setTtl(timeToLive);

            boolean sent = sendOtpEmail(dto.getEmailId(), dto.getEmailOtp(), dto.getTtl());

            if (sent) {
                otpResponse.setMobileOTP(null);
                otpResponse.setEmailOTP(null);
                otpResponse.setTtl(timeToLive);
                otpResponse.setEmailEncrptyOTP(encryptedString(emailOTP));
                return exceptionHandlerUtil.createSuccessResponse("api.response.ok", otpResponse);
            } else {
                return exceptionHandlerUtil
                        .createErrorResponse("api.error.something.went.wrong.please.try.after.sometime");
            }

        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }


    public boolean sendOtpEmail(String receiver, String otp, int ttl) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            String language = locale.getLanguage();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(receiver);
            String subject;
            String body;

            if ("ar".equalsIgnoreCase(language)) {

                subject = "رمز التحقق لنظام UAEID";

                body = """
                   <html dir="rtl">
                   <body>
                   <p style="color:black">
                   عزيزي العميل،<br><br>
                   رمز التحقق الخاص بك لتسجيل UAEID هو <b>%s</b>.
                   يرجى استخدام هذا الرمز للتحقق من بريدك الإلكتروني.
                   <br>هذا الرمز صالح لمدة %s ثانية.
                   <br><br>- نظام UAEID
                   </p>
                   <br>
                   <img src='cid:logoImage' width='150' height='51'/>
                   </body>
                   </html>
                   """.formatted(otp, ttl);

            } else {

                subject = "UAEID System OTP";

                body = """
                   <html>
                   <body>
                   <p style="color:black">
                   Dear Customer,<br><br>
                   Your OTP for UAEID Registration is <b>%s</b>, Please use this OTP to validate your Email.
                   <br>This OTP is valid for %s seconds.
                   <br><br>- UAEID System
                   </p>
                   <br>
                   <img src='cid:logoImage' width='150' height='51'/>
                   </body>
                   </html>
                   """.formatted(otp, ttl);
            }

            helper.setSubject(subject);
            helper.setText(body, true);

            ClassPathResource image = new ClassPathResource("UAEID.png");
            helper.addInline("logoImage", image);

            mailSender.send(message);

            return true;

        } catch (Exception e) {
            logger.info("{} sendOtpEmail :: {}" ,CLASS, e.getMessage());
            return false;
        }
    }

	public ApiResponse verifyOtp(String mobNo, String email) {
		ApiResponse apiResponse = new ApiResponse();

		OTPResponseDTO otpResponse = new OTPResponseDTO();

		if (email != null) {
			otpResponse.setEmailEncrptyOTP(AppUtil.encryptedString("12345"));
		} else {
			otpResponse.setMobileEncrptyOTP(AppUtil.encryptedString("123456"));
		}
		otpResponse.setTtl(180);
		apiResponse.setMessage("Otp verfication done");
		apiResponse.setSuccess(true);
		apiResponse.setResult(otpResponse);
		return apiResponse;

	}

    @Override
    public ApiResponse sendOtpMobile(UpdateOtpDto otpDto) {
        try {

            if (Objects.isNull(otpDto) || !StringUtils.hasText(otpDto.getMobileNumber())) {
                return exceptionHandlerUtil.createErrorResponse("api.error.mobile.number.cant.be.empty");
            }

            String mobileOTP = generateOtp(6);
            String mobileNumber = otpDto.getMobileNumber();

            if (isTestOtpNumber(mobileNumber)) {
                return handleTestOtp(mobileNumber);
            }

            logger.info(CLASS + "sendOTPMobileSms req IND {}", mobileNumber);

            if (mobileNumber.startsWith("+91")) {
                return handleIndiaOtp(mobileNumber, mobileOTP);
            }

            if (mobileNumber.startsWith("+256")) {
                return handleUgandaOtp(mobileNumber, mobileOTP);
            }

            if (mobileNumber.startsWith("+971")) {
                return handleUaeOtp(mobileNumber, mobileOTP);
            }

            return exceptionHandlerUtil.createErrorResponse("api.error.invalid.country.code");

        } catch (Exception e) {
            logger.error(UNEXPECTED_EXCEPTION, e);
            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
    private boolean isTestOtpNumber(String mobileNumber) {
        return mobileNumber.equals(testIosOtp) || mobileNumber.equals(testAndroidOtp);
    }

    private ApiResponse handleTestOtp(String mobileNumber) {
        ApiResponse apiResponseDemo = verifyOtp(mobileNumber, null);

        if (apiResponseDemo.isSuccess()) {
            return exceptionHandlerUtil.createSuccessResponse(
                    API_RESPONSE_OK,
                    apiResponseDemo.getResult());
        }

        return apiResponseDemo;
    }
    private ApiResponse handleIndiaOtp(String mobileNumber, String mobileOTP) {

        if (mobileNumber.length() != 13) {
            return exceptionHandlerUtil.createErrorResponse(
                    API_ERROR_PHONE_NUMBER_IS_INVALID);
        }

        logger.info("IND");

        ApiResponse apiResponse = sendSMSIND(mobileOTP, mobileNumber.substring(3, 13));

        if (!apiResponse.isSuccess()) {
            return AppUtil.createApiResponse(false,
                    "Unable to perform action. Please try after sometime", null);
        }

        OTPResponseDTO otpResponse = buildOtpResponse(mobileOTP);

        return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_OK, otpResponse);
    }
    private ApiResponse handleUgandaOtp(String mobileNumber, String mobileOTP) {

        if (mobileNumber.length() != 13) {
            return exceptionHandlerUtil.createErrorResponseWithResult(
                    API_ERROR_PHONE_NUMBER_IS_INVALID,
                    new OTPResponseDTO());
        }

        logger.info(CLASS + "sendOTPMobileSms req UGA {}", mobileNumber);

        try {

            ApiResponse response = sendSMSUGA(mobileOTP, mobileNumber, timeToLive);

            SmsOtpResponseDTO smsOtpResponse = objectMapper.readValue(
                    response.getResult().toString(),
                    SmsOtpResponseDTO.class);

            if (smsOtpResponse.getNon_field_errors() != null) {

                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                        smsOtpResponse.getNon_field_errors().get(0),
                        null);
            }

            OTPResponseDTO otpResponse = buildOtpResponse(mobileOTP);

            return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_OK, otpResponse);

        } catch (Exception e) {

            sentryClientExceptions.captureExceptions(e);
            logger.error(CLASS + "sendSMSUGA IN UGA Exception {}", e.getMessage());
            logger.error(UNEXPECTED_EXCEPTION, e);

            return exceptionHandlerUtil
                    .createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);
        }
    }
    private ApiResponse handleUaeOtp(String mobileNumber, String mobileOTP) {

        if (mobileNumber.length() != 13) {
            return exceptionHandlerUtil.createErrorResponse(
                    API_ERROR_PHONE_NUMBER_IS_INVALID);
        }

        logger.info(CLASS + "sendOTPMobileSms req +971 {}", mobileNumber);

        Object obj = sendSMSUAE(mobileOTP, mobileNumber, timeToLive);

        try {

            String sms = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

            LinkedHashMap<String, String> smsOtpResponse =
                    objectMapper.readValue(sms, LinkedHashMap.class);

            if (Objects.equals(smsOtpResponse.get("code"), "406")) {
                return exceptionHandlerUtil.createErrorResponse("api.error.invalid.number");
            }

            OTPResponseDTO otpResponse = buildOtpResponse(mobileOTP);

            return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_OK, otpResponse);

        } catch (Exception e) {

            sentryClientExceptions.captureExceptions(e);
            logger.error(UNEXPECTED_EXCEPTION, e);
            logger.error(CLASS + "sendSMSUAE IN UAE Exception {}", e.getMessage());

            return exceptionHandlerUtil
                    .createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);
        }
    }
    private OTPResponseDTO buildOtpResponse(String mobileOTP) {

        OTPResponseDTO otpResponse = new OTPResponseDTO();

        otpResponse.setMobileOTP(null);
        otpResponse.setEmailOTP(null);
        otpResponse.setTtl(timeToLive);
        otpResponse.setMobileEncrptyOTP(encryptedString(mobileOTP));

        return otpResponse;
    }


	private ApiResponse sendSMSIND(String otp, String mobileNumber) {
		logger.info(CLASS + "sendSMSIND req  otp {} and mobileNumber {}", otp, mobileNumber);
		String smsBody = "Dear Subscriber, " + otp + " is your DigitalTrust Mobile verification one-time code";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
        String smsUrlWithBody = indApiSMS
                + "?APIKey=" + smsApiKey
                + "&senderid=DGTRST"
                + "&channel=2"
                + "&DCS=0"
                + "&flashsms=0"
                + "&number=" + mobileNumber
                + "&text=" + smsBody
                + "&route=1"
                + "&dlttemplateid=1307162619898313468";

		HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
		try {

			logger.info(CLASS + "sendSMSIND req for restTemplate smsUrlWithBody {} and requestEntity {}",
					smsUrlWithBody, requestEntity);

			ResponseEntity<Object> res = restTemplate.exchange(smsUrlWithBody, HttpMethod.GET, requestEntity,
					Object.class);
			String smsResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res.getBody());
			LinkedHashMap<String, String> indiaSmsOtpResponse = objectMapper.readValue(smsResponse,
					LinkedHashMap.class);
			if (Objects.equals(indiaSmsOtpResponse.get("ErrorCode"), "000") || indiaSmsOtpResponse.get("ErrorCode").equals("000")) {
				logger.info(CLASS + "sendSMSIND res for restTemplate {}", indiaSmsOtpResponse);
				return exceptionHandlerUtil
						.createSuccessResponseWithCustomMessage(indiaSmsOtpResponse.get("ErrorMessage"), null);
			} else {
				return exceptionHandlerUtil
						.createFailedResponseWithCustomMessage(indiaSmsOtpResponse.get("ErrorMessage"), null);
			}
		} catch (Exception e) {
			logger.error(CLASS + "sendSMSIND Exception {}", e.getMessage());
				logger.error(UNEXPECTED_EXCEPTION, e);
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public ApiResponse sendSMSUGA(String otp, String mobileNumber, int timeToLive)  {
		logger.info(CLASS + "sendSMSUGA otp {} and mobileNumber {} and timeToLive {} ", otp, mobileNumber, timeToLive);
		String url = niraApiSMS;
		String basicAuth = getBasicAuth();
		SmsDTO smsDTO = new SmsDTO();
		smsDTO.setPhoneNumber(mobileNumber);
		smsDTO.setSmsText("Dear Customer, your OTP for UgPass Registration is " + otp
				+ ", Please use this OTP to validate your Mobile number. This OTP is valid for " + timeToLive
				+ " Seconds - UgPass System");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("daes-authorization", basicAuth);
		headers.set("access_token", getToken());
		HttpEntity<Object> requestEntity = new HttpEntity<>(smsDTO, headers);
		try {
			logger.info(CLASS + " sendSMSUGA req for restTemplate url {} and requestEntity {} ", url, requestEntity);
			ResponseEntity<ApiResponse> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			ApiResponse api = res.getBody();
			logger.info("sendSMSUGA res for restTemplate {}", res);
			return api;
		} catch (Exception e) {
			logger.error(CLASS + "sendSMSUGA Exception {}", e.getMessage());
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public Object sendSMSUAE(String otp, String mobileNumber, int timeToLive)  {
		logger.info("sendSMSUAE  otp {} and mobileNumber {} and timeToLive {}", otp, mobileNumber, timeToLive);
		String url = uaeApiSMS;

		String text = "Your OTP for UAEID Registration is " + otp +
				". Please use this OTP to validate your Phone Number. " +
				"This OTP is valid for 180 Seconds. - UAEID System";
		Map<String, String> uaeSmsBody = new HashMap<>();
		uaeSmsBody.put("mobileno", mobileNumber);
		uaeSmsBody.put("smstext", text);

		HttpEntity<Object> requestEntity = new HttpEntity<>(uaeSmsBody);
		try {
			logger.info(CLASS + "sendSMSUAE req for restTemplate url {} and requestEntity {} ", url, requestEntity);
			ResponseEntity<Object> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
			ApiResponse api = new ApiResponse();
			api.setSuccess(true);
			api.setMessage("");
			api.setResult(res.getBody());
			logger.info(CLASS + "sendSMSUAE res for restTemplate {}", res);
			return api.getResult();
		} catch (Exception e) {
			logger.error("sendSMSUAE Exception {}", e.getMessage());
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);

		}
	}

	public String getBasicAuth() {
		String userCredentials = niraUserName + ":" + niraPassword;
		return new String(Base64.getEncoder().encode(userCredentials.getBytes()));

	}

	public String getToken() {
		String url = niraApiToken;
		logger.info(CLASS + "getToken req url {}", url);
		String basicAuth = getBasicAuth();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("daes-authorization", basicAuth);
		HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
		try {
			logger.info(CLASS + "getToken req for restTemplate {}", requestEntity);
			ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
			logger.info(CLASS + "getToken res for restTemplate {}", res);
			return res.getBody();
		} catch (Exception e) {
			logger.error(CLASS + "getToken Exception {}", e.getMessage());
				logger.error(UNEXPECTED_EXCEPTION, e);
			return e.getMessage();
		}

	}


	public String generateOtp(int maxLength) {
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			StringBuilder otp = new StringBuilder(maxLength);

			for (int i = 0; i < maxLength; i++) {
				otp.append(secureRandom.nextInt(9));
			}
			return otp.toString();
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return null;
		}
	}

	public ApiResponse sendEmailToSubscriber(EmailReqDto emailReqDto) {
		try {
			sentryClientExceptions.captureTags(null, emailReqDto.getEmailId(), "sendEmailToSubscriber",
					CLASS);
			String url = emailBaseUrl;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(emailReqDto, headers);
            logger.info("requestEntity >> {} " , requestEntity);
			ResponseEntity<ApiResponse> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
            logger.info("res >> {} " , res);
            if (res.getStatusCode() == HttpStatus.OK) {

                logger.info("sendEmailToSubscriber");

                return exceptionHandlerUtil.createSuccessResponse("api.response.sent", res);

            } else if (res.getStatusCode() == HttpStatus.BAD_REQUEST) {

                return exceptionHandlerUtil.createSuccessResponse(API_ERROR_BAD_REQUEST, res);

            } else if (res.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {

                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);
            }

            ApiResponse responseBody = res.getBody();

            if (responseBody == null) {
                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                        API_ERROR_SOMETHING_WENT_WRONG, null);
            }

            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                    responseBody.getMessage(), null);

        } catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);

		}

	}

    @Override
    public ApiResponse reOnboardAddSubscriberObData(SubscriberObRequestDTO obRequestDTO) {
        try {

            ApiResponse validationResponse = validateRequest(obRequestDTO);
            if (validationResponse != null) {
                return validationResponse;
            }

            OnbSubscriber subscriberData = subscriberRepoIface.findBysubscriberUid(obRequestDTO.getSuID());
            if (subscriberData == null) {
                return AppUtil.createApiResponse(false, API_ERROR_SUBSCRIBER_NOT_FOUND, null);
            }

            OnbSubscriberOnboardingData subscriberOnboardingData =
                    onboardingDataRepoIface.findLatestSubscriber(subscriberData.getSubscriberUid())
                            .stream()
                            .findFirst()
                            .orElse(null);

            long differenceInDays = 0;

            if (subscriberOnboardingData != null) {
                differenceInDays =
                        AppUtil.getDifferenceBetDates(subscriberOnboardingData.getCreatedDate());
            }

            SubscriberObData subscriberObData = obRequestDTO.getSubscriberData();

            ApiResponse genderDobResponse =
                    validateGenderAndDOB(subscriberObData, subscriberOnboardingData, subscriberData);
            if (genderDobResponse != null) {
                return genderDobResponse;
            }

            ApiResponse documentResponse =
                    validateDocumentNumber(obRequestDTO, subscriberObData);
            if (documentResponse != null) {
                return documentResponse;
            }


            String oldExpireDate = subscriberOnboardingData != null
                    ? subscriberOnboardingData.getDateOfExpiry()
                    : null;

            if (isOldDocumentExpired(oldExpireDate)) {
                return processExpiredDocument(obRequestDTO, subscriberOnboardingData);
            }

            return processValidDocument(obRequestDTO, subscriberOnboardingData, differenceInDays);

        } catch (Exception e) {
            logger.error(UNEXPECTED_EXCEPTION, e);
            if (obRequestDTO != null) {
                sentryClientExceptions.captureTags(
                        obRequestDTO.getSuID(),
                        null,
                        "reOnboardAddSubscriberObData",
                        SUBSCRIBER_CONTROLLER);
            } else {
                sentryClientExceptions.captureTags(
                        null,
                        null,
                        "reOnboardAddSubscriberObData",
                        SUBSCRIBER_CONTROLLER);
            }

            sentryClientExceptions.captureExceptions(e);
            return exceptionHandlerUtil.handleException(e);
        }
    }

private ApiResponse validateGenderAndDOB(SubscriberObData subscriberObData,
                                              OnbSubscriberOnboardingData subscriberOnboardingData,
                                              OnbSubscriber subscriberData) throws ParseException {

        if (checkGender) {
            String gender1 = normalizeGender(subscriberObData.getGender());
            String gender2 = normalizeGender(subscriberOnboardingData.getGender());

            if (!gender1.equals(gender2)) {
                return exceptionHandlerUtil.createErrorResponse("api.error.gender.must.be.same");
            }
        }

        String dob = AppUtil.removeTimeStamp(subscriberData.getDateOfBirth());
        String reOnboardDOB = AppUtil.removeTimeStamp(subscriberObData.getDateOfBirth());

    if (checkDateOfBirth && !reOnboardDOB.equals(dob)) {
                return exceptionHandlerUtil.createErrorResponse("api.error.date.of.birth.must.be.same");
            }


        return null;
    }
    private ApiResponse validateDocumentNumber(SubscriberObRequestDTO obRequestDTO,
                                               SubscriberObData subscriberObData) {

        if (subscriberObData.getDocumentNumber() == null) {
            return exceptionHandlerUtil
                    .createErrorResponse("api.error.id.document.number.cant.be.null");
        }

        OnbSubscriber subscriber2 =
                subscriberRepoIface.findbyDocumentNumber(subscriberObData.getDocumentNumber());

        if (subscriber2 != null && !subscriber2.getSubscriberUid().equals(obRequestDTO.getSuID()) ) {
            return exceptionHandlerUtil.createErrorResponse("api.error.this.document.is.already.onboarded");

        }

        return null;
    }private boolean isOldDocumentExpired(String oldExpireDate) {

        String latest = AppUtil.getDate();
        return oldExpireDate.compareTo(latest) < 0;

    }private ApiResponse processExpiredDocument(SubscriberObRequestDTO obRequestDTO,
                                                OnbSubscriberOnboardingData subscriberOnboardingData) throws Exception {

        ApiResponse expiryValidation = validateExpiryDate(obRequestDTO);
        if (expiryValidation != null) {
            return expiryValidation;
        }

        ApiResponse loaResponse =
                validateLoaLevel(subscriberOnboardingData, obRequestDTO);
        if (loaResponse != null) {
            return loaResponse;
        }

        return addSubscriberObData(obRequestDTO);
    }private ApiResponse processValidDocument(SubscriberObRequestDTO obRequestDTO,
                                              OnbSubscriberOnboardingData subscriberOnboardingData,
                                              long differenceInDays) throws Exception {

        if (differenceInDays < expiryDays &&
                !obRequestDTO.getSubscriberData().getDocumentNumber()
                        .equals(subscriberOnboardingData.getIdDocNumber())) {

            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                    "We can't processed. it's seem your last updation of your id document is less than "
                            + expiryDays + " days.",
                    null);
        }

        ApiResponse expiryValidation = validateExpiryDate(obRequestDTO);
        if (expiryValidation != null) {
            return expiryValidation;
        }

        ApiResponse loaResponse =
                validateLoaLevel(subscriberOnboardingData, obRequestDTO);
        if (loaResponse != null) {
            return loaResponse;
        }

        return addSubscriberObData(obRequestDTO);
    }private ApiResponse validateExpiryDate(SubscriberObRequestDTO obRequestDTO) {

        SubscriberObData subscriberObData = obRequestDTO.getSubscriberData();

        LocalDateTime newExpiryDate =
                AppUtil.getLocalDateTime(subscriberObData.getDateOfExpiry());

        LocalDateTime currentDateTime =
                AppUtil.getLocalDateTime(AppUtil.getDate());

        if (!"NIN".equalsIgnoreCase(obRequestDTO.getOnboardingMethod())) {

            if (newExpiryDate.isAfter(currentDateTime)) {

                long daysBetween = Duration.between(currentDateTime, newExpiryDate).toDays();
                logger.info("Days: {}", daysBetween);

                if (daysBetween <= 1) {
                    return exceptionHandlerUtil.createErrorResponse(
                            "api.error.you.cant.do.reonboard.because.your.document.date.of.expiry.is.less.then.days");
                }

            } else if (newExpiryDate.isBefore(currentDateTime)) {

                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.the.expiry.date.with.time.is.before.the.current.date.with.time");

            } else {

                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.the.expiry.date.with.time.is.the.same.as.the.current.date.with.time");
            }
        }

        return null;
    }private ApiResponse validateLoaLevel(OnbSubscriberOnboardingData subscriberOnboardingData,
                                          SubscriberObRequestDTO obRequestDTO) {

        String loa = subscriberOnboardingData.getLevelOfAssurance();

       if (loa.equals(Constant.LOA2)) {

            if (obRequestDTO.getOnboardingMethod().equals(Constant.UNID)) {
                return exceptionHandlerUtil
                        .createErrorResponse(API_ERROR_YOU_ARE_USING_LOW_LEVEL_ASSURANCE);
            }

        } else if (loa.equals(Constant.LOA3)) {

            if (obRequestDTO.getOnboardingMethod().equals(Constant.UNID)) {
                return exceptionHandlerUtil
                        .createErrorResponse(API_ERROR_YOU_ARE_USING_LOW_LEVEL_ASSURANCE);
            }

            if (obRequestDTO.getOnboardingMethod().equals(Constant.PASSPORT)) {
                return exceptionHandlerUtil
                        .createErrorResponse(API_ERROR_YOU_ARE_USING_LOW_LEVEL_ASSURANCE);
            }
        }

        return null;
    }
	private String normalizeGender(String gender) {
		if (gender == null)
			return "";
		gender = gender.trim().toLowerCase();
		if (gender.equals("m") || gender.equals("male")) {
			return "male";
		} else if (gender.equals("f") || gender.equals("female")) {
			return "female";
		}
		return gender; // fallback if value is unexpected
	}

	@Override
	public ApiResponse deleteRecord(String mobileNo, String email) {
		try {
			if (!mobileNo.isEmpty()) {
				Optional<OnbSubscriber> subscriber = Optional
						.ofNullable(subscriberRepoIface.findBymobileNumber("+" + mobileNo));
				if (subscriber.isPresent()) {
					String suid = subscriber.get().getSubscriberUid();
					subscriberDeletionRepository.deleteSubscriberRecord(suid);
					int a = 1;
					if (a == 1) {
						return exceptionHandlerUtil
								.successResponse(API_RESPONSE_SUBSCRIBER_RECORD_DELETED_SUCCESSFULLY);
					} else {
						return exceptionHandlerUtil
								.createErrorResponse("api.error.subscriber.record.not.deleted.successfully");
					}
				}
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
			} else {
				Optional<OnbSubscriber> subscriber = Optional.ofNullable(subscriberRepoIface.findByemailId(email));
				if (subscriber.isPresent()) {
					String suid = subscriber.get().getSubscriberUid();
					subscriberDeletionRepository.deleteSubscriberRecord(suid);
					int a = 1;
					if (a == 1) {
						return exceptionHandlerUtil
								.successResponse(API_RESPONSE_SUBSCRIBER_RECORD_DELETED_SUCCESSFULLY);
					} else {
						return exceptionHandlerUtil
								.createErrorResponse("api.error.subscriber.record.not.deleted.successfully");
					}
				}
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
			}
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);

		}
	}

    @Override
    public ApiResponse getDeviceStatus(HttpServletRequest httpServletRequest) {
        try {

            String deviceId = httpServletRequest.getHeader(DEVICEID);

            if (deviceId == null || deviceId.isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.deviceid.not.coming.please.send.deviceid");
            }

            List<OnbSubscriberDevice> subscriberDeviceList =
                    deviceRepoIface.findBydeviceUid(deviceId);

            OnbSubscriberDevice subscriberDevices = getLatest(subscriberDeviceList);

            logger.info("latestttttttttttt:::::::::::::: {} ", subscriberDevices);

            DeviceStatusDto deviceStatusDto = new DeviceStatusDto();

            if (subscriberDevices != null) {
                return handleExistingDevice(subscriberDevices, deviceStatusDto);
            }

            return handleDeviceHistory(deviceId, deviceStatusDto);

        } catch (Exception e) {
            logger.error(UNEXPECTED_EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
    private ApiResponse handleExistingDevice(
            OnbSubscriberDevice subscriberDevices,
            DeviceStatusDto deviceStatusDto) {

        OnbSubscriberFcmToken subscriberFcmToken =
                fcmTokenRepoIface.findBysubscriberUid(
                        subscriberDevices.getSubscriberUid());

        deviceStatusDto.setFcmToken(subscriberFcmToken.getFcmToken());
        deviceStatusDto.setDeviceStatus(subscriberDevices.getDeviceStatus());

        if (subscriberDevices.getDeviceStatus().equals(Constant.DEVICE_STATUS_DISABLED)) {

            deviceStatusDto.setConsentRequired(false);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.device.status.is.disabled",
                    deviceStatusDto);
        }

        return handleActiveDevice(subscriberDevices, deviceStatusDto);
    }
    private ApiResponse handleActiveDevice(
            OnbSubscriberDevice subscriberDevices,
            DeviceStatusDto deviceStatusDto) {

        OnbSubscriberStatus subscriberStatus =
                statusRepoIface.findBysubscriberUid(
                        subscriberDevices.getSubscriberUid());

        if (signRequired) {

            if (subscriberStatus.getSubscriberStatus().equals(ACTIVE)) {

                processConsent(subscriberDevices, deviceStatusDto);

            } else {

                deviceStatusDto.setConsentRequired(false);
            }

        } else {

            processConsent(subscriberDevices, deviceStatusDto);
        }

        return exceptionHandlerUtil.createSuccessResponse(
                "api.response.device.status",
                deviceStatusDto);
    }
    private void processConsent(
            OnbSubscriberDevice subscriberDevices,
            DeviceStatusDto deviceStatusDto) {

        List<OnbConsentHistory> latestConsentList =
                consentHistoryRepo.findLatestConsent();

        OnbConsentHistory consentHistory =
                latestConsentList.isEmpty() ? null : latestConsentList.get(0);

        if (consentHistory == null) {

            deviceStatusDto.setConsentRequired(false);
            return;
        }

        OnbSubscriberConsents subscriberConsents =
                subscriberConsentsRepo.findSubscriberConsentBySuidAndConsentId(
                        subscriberDevices.getSubscriberUid(),
                        consentHistory.getId());

        deviceStatusDto.setConsentRequired(subscriberConsents == null);
    }
    private ApiResponse handleDeviceHistory(
            String deviceId,
            DeviceStatusDto deviceStatusDto) {

        List<OnbSubscriberDeviceHistory> historyList =
                subscriberDeviceHistoryRepoIface.findBydeviceUid(deviceId);

        OnbSubscriberDeviceHistory latest =
                historyList.isEmpty() ? null : historyList.get(0);

        Optional<OnbSubscriberDeviceHistory> subscriberDeviceHistory =
                Optional.ofNullable(latest);

        if (subscriberDeviceHistory.isPresent()) {

            deviceStatusDto.setDeviceStatus(
                    subscriberDeviceHistory.get().getDeviceStatus());

            deviceStatusDto.setConsentRequired(false);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.device.status.is.disabled",
                    deviceStatusDto);

        } else {

            deviceStatusDto.setConsentRequired(false);
            deviceStatusDto.setDeviceStatus(Constant.NEW_DEVICE);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.device.status",
                    deviceStatusDto);
        }
    }


	public OnbSubscriberDevice getLatest(List<OnbSubscriberDevice> list) {


		return list.stream().min(Comparator.comparing(sd -> parseDate(sd.getUpdatedDate()), Comparator.reverseOrder())).orElse(null);
	}

	private LocalDateTime parseDate(String date) {
		if (date.contains("T")) {
			return LocalDateTime.parse(date); // ISO format
		} else {
			return LocalDateTime.parse(date.replace(" ", "T"));
		}
	}

	@Override
	public ApiResponse getSubscriberDetailsBySerachType(String searchType, String searchValue) {
		try {
			logger.info(CLASS + GET_SUBSCRIBER_REQUEST, searchType,
					searchValue);

			if (searchType == null || searchType.isEmpty() || searchValue == null || searchValue.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_BAD_REQUEST);
			}

			OnbSubscriber subscriber = null;
			SubscriberDeviceUpdateDto subscriberDeviceUpdateDto = new SubscriberDeviceUpdateDto();
			switch (searchType) {
			case EMAILID:
				subscriber = subscriberRepoIface.findByemailId(searchValue);
				break;
			case MOBILE_NUMBER:
				subscriber = subscriberRepoIface.findBymobileNumber(searchValue);
				break;
			default:
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_BAD_REQUEST);
			}
			if (subscriber == null) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_DETAIS_NOT_FOUND);
			} else {

				OnbSubscriberStatus subscriberStatus = statusRepoIface.findBysubscriberUid(subscriber.getSubscriberUid());

				OnbSubscriberDevice subscriberDevice = deviceRepoIface.getSubscriber(subscriber.getSubscriberUid());
				subscriberDeviceUpdateDto.setSubscriberUid(subscriber.getSubscriberUid());
				subscriberDeviceUpdateDto.setFullName(subscriber.getFullName());
				subscriberDeviceUpdateDto.setDateOfBirth(subscriber.getDateOfBirth());
				subscriberDeviceUpdateDto.setIdDocType(subscriber.getIdDocType());
				subscriberDeviceUpdateDto.setIdDocNumber(subscriber.getIdDocNumber());
				subscriberDeviceUpdateDto.seteMail(subscriber.getEmailId());
				subscriberDeviceUpdateDto.setMobileNumber(subscriber.getMobileNumber());
				subscriberDeviceUpdateDto.setOsName(subscriber.getOsName());
				subscriberDeviceUpdateDto.setAppVersion(subscriber.getAppVersion());
				subscriberDeviceUpdateDto.setOsVersion(subscriber.getOsVersion());
				subscriberDeviceUpdateDto.setDeviceInfo(subscriber.getDeviceInfo());

				subscriberDeviceUpdateDto.setCreatedDate(subscriber.getCreatedDate());
				subscriberDeviceUpdateDto.setUpdatedDate(subscriber.getUpdatedDate());

				subscriberDeviceUpdateDto.setSubscriberStatus(subscriberStatus.getSubscriberStatus());

				subscriberDeviceUpdateDto.setDeviceUid(subscriberDevice.getDeviceUid());
				subscriberDeviceUpdateDto.setDeviceStatus(subscriberDevice.getDeviceStatus());


				return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_SUBSCIBER_DETAILS,
						subscriberDeviceUpdateDto);
			}

		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " getSubscriberDetailsBySerachType Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse updateSusbcriberDeviceStatus(String suid) {
		try {
			logger.info(CLASS + "updateSusbcriberDeviceStatus request suid {}", suid);
			if (suid == null || "".equals(suid.trim())) {
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.unique.id.cant.be.null");
			} else {
				OnbSubscriberDevice subscriberDevice = (OnbSubscriberDevice) deviceRepoIface.getSubscriberDeviceStatus(suid);
				if (subscriberDevice == null) {
					return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_DETAIS_NOT_FOUND);
				} else {
					subscriberDevice.setDeviceStatus(Constant.DEVICE_STATUS_DISABLED);
					subscriberDevice.setUpdatedDate(AppUtil.getDate());
					deviceRepoIface.save(subscriberDevice);
					return exceptionHandlerUtil.successResponse("api.response.subscriber.device.status.updated");
				}
			}

		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " updateSusbcriberDeviceStatus Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse getSubscriberListBySerachType(String searchType, String searchValue) {
		try {
			logger.info(CLASS + GET_SUBSCRIBER_REQUEST, searchType,
					searchValue);
			if (searchType == null || searchType.isEmpty() || searchValue == null || searchValue.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_BAD_REQUEST);
			}

			List<String> subscriberList = null;
			switch (searchType) {
			case EMAILID:
				subscriberList = subscriberRepoIface.getSubscriberListByEmailId(searchValue);
				break;
			case MOBILE_NUMBER:
				subscriberList = subscriberRepoIface.getSubscriberListByMobileNo(searchValue);
				break;
			default:
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_BAD_REQUEST);
			}
			if (subscriberList == null) {

				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_DETAIS_NOT_FOUND);
			} else {

				String jsonToString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(subscriberList);
				return AppUtil.createApiResponse(true, API_RESPONSE_SUBSCIBER_DETAILS, jsonToString);
			}

		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " getSubscriberDetailsBySerachType Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse updateFcmTokenDetails(String suid, String fcmToken) {
		try {
			logger.info("{}{} - Received request to update FCM token for suid: {} with fcmToken: {}", CLASS,
					Utility.getMethodName(), suid, fcmToken);
			Date startTime = new Date();
            if (suid != null && !suid.isEmpty()) {
                if (fcmToken != null && !fcmToken.isEmpty()) {
					OnbSubscriberFcmToken subscriberFcmToken = fcmTokenRepoIface.findBysubscriberUid(suid);
					if (subscriberFcmToken != null) {
						String message = "OLD FCMTOKEN | " + subscriberFcmToken.getFcmToken() + " NEW FCMTOKEN |"
								+ fcmToken;
						logger.info("{}{} - message {}", CLASS, Utility.getMethodName(), message);
						subscriberFcmToken.setFcmToken(fcmToken);
						subscriberFcmToken.setCreatedDate(AppUtil.getDate());
						fcmTokenRepoIface.save(subscriberFcmToken);
						Date endTime = new Date();
						logModelServiceImpl.setLogModelFCMToken(true, suid, null, "OTHER", null, message, startTime,
								endTime, null);
						return exceptionHandlerUtil.createSuccessResponse("api.response.fcmtoken.updated.successfully",
								subscriberFcmToken);
					} else {
						return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
					}
				} else {
					return exceptionHandlerUtil.createErrorResponse("api.error.fcmtoken.cant.be.null.or.empty");
				}
			} else {
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.suid.cantbe.null.or.empty");
			}
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " updateFcmTokenDetails Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}


    @Override
    public ApiResponse getSubDetailsBySerachType(HttpServletRequest httpServletRequest, String searchType,
                                                 String searchValue) {
        try {
            logger.info(CLASS + "getSubscriberDetailsBySerachType request searchType and searchValue {},{}", searchType,
                    searchValue);
            if (searchType == null || searchType.isEmpty() || searchValue == null || searchValue.isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse("api.error.bad.request");
            }
            MobileOTPDto mobileOTPDto = new MobileOTPDto();
            DeviceInfo deviceInfo = new DeviceInfo();
            OnbSubscriber subscriber = null;
            switch (searchType) {
                case "emailId":
                    subscriber = subscriberRepoIface.findByemailId(searchValue);
                    break;
                case "mobileNumber":
                    subscriber = subscriberRepoIface.findBymobileNumber(searchValue);
                    break;
                case "idDocNumber":
                    subscriber = subscriberRepoIface.findByIdDocNumber(searchValue);
                    break;
                case "nationalId":
                    subscriber = subscriberRepoIface.findByNationalId(searchValue);
                    break;
                default:
                    return exceptionHandlerUtil.createErrorResponse("api.error.bad.request");
            }

            if (subscriber == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.details.not.found");
            } else {
                // For android
                if (subscriber.getMobileNumber().equalsIgnoreCase(testAndroidOtp)
                        && subscriber.getEmailId().equalsIgnoreCase(testAndroidEmail)) {
                    OnbSubscriberDevice subscriberDevice = (OnbSubscriberDevice) deviceRepoIface
                            .findBysubscriberUid(subscriber.getSubscriberUid());
                    subscriberDevice.setDeviceUid(httpServletRequest.getHeader("deviceId"));
                    deviceRepoIface.save(subscriberDevice);
                }
                // same for IOS
                if (subscriber.getMobileNumber().equalsIgnoreCase(testIosOtp)
                        && subscriber.getEmailId().equalsIgnoreCase(testIosEmail)) {
                    OnbSubscriberDevice subscriberDevice = (OnbSubscriberDevice) deviceRepoIface
                            .findBysubscriberUid(subscriber.getSubscriberUid());

                    subscriberDevice.setDeviceUid(httpServletRequest.getHeader("deviceId"));
                    deviceRepoIface.save(subscriberDevice);
                }

                deviceInfo.setDeviceId(httpServletRequest.getHeader("deviceId"));
                deviceInfo.setAppVersion(httpServletRequest.getHeader("appVersion"));
                deviceInfo.setOsVersion(httpServletRequest.getHeader("osVersion"));

                mobileOTPDto.setSubscriberEmail(subscriber.getEmailId());
                mobileOTPDto.setSubscriberMobileNumber(subscriber.getMobileNumber());
                ApiResponse apiResponse = deviceUpdateIface.validateSubscriberAndDevice(deviceInfo, mobileOTPDto);
                if (apiResponse.isSuccess()) {
                    return exceptionHandlerUtil.createSuccessResponse("api.response.subscriber.details",
                            apiResponse.getResult());
                } else {
                    return apiResponse;
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            logger.error(CLASS + " getSubDetailsBySerachType Exception {}", e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public void deviceUpdatedSendEmail(String emailId) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            String language = locale.getLanguage();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(emailId);
            String subject;
            String body;

            if ("ar".equalsIgnoreCase(language)) {

                subject = "تم تغيير الجهاز بنجاح";

                body = """
        <html dir="rtl">
        <body>
        <p style="color:black; font-family:Arial, sans-serif;">
        عزيزي العميل،<br><br>
        
        تم تغيير جهازك بنجاح.<br>
        يمكنك الآن الوصول إلى حسابك باستخدام جهازك الجديد بكل سهولة.<br><br>
        
        شكرًا لك،<br>
        فريق النظام
        </p>
        <br>
        <img src='cid:logoImage' width='150' height='51'/>
        </body>
        </html>
        """;

            } else {

                subject = "Device Changed Successfully";

                body = """
        <html>
        <body>
        <p style="color:black; font-family:Arial, sans-serif;">
        Dear Customer,<br><br>
        
        Your device has been successfully updated.<br>
        You can now access your account using your new device.<br><br>
        
        Regards,<br>
        System Team
        </p>
        <br>
        <img src='cid:logoImage' width='150' height='51'/>
        </body>
        </html>
        """;
            }
            helper.setSubject(subject);
            helper.setText(body, true);
            ClassPathResource image = new ClassPathResource("UAEID.png");
            helper.addInline("logoImage", image);
            mailSender.send(message);
        } catch (Exception e) {
            logger.info("{} deviceUpdatedSendEmail :: {}",CLASS ,e.getMessage());
        }
    }

	@Override
	public ApiResponse getSusbcriberDeviceHistory(String suid) {
		try {
			logger.info("{}{} - Reuest for SusbcriberDeviceHistory suid {}", CLASS, Utility.getMethodName(), suid);
			if (!StringUtils.hasText(suid)) {
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.id.can.be.null.or.empty");
			}

			OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(suid);
			if (subscriber == null) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_DETAIS_NOT_FOUND);
			} else {
				SubscriberDeviceHistoryDetails subscriberDeviceHistoryDetails = new SubscriberDeviceHistoryDetails();



				List<OnbSubscriberDevice> devices = deviceRepoIface.findBysubscriberUid(suid);
				OnbSubscriberDevice subscriberDevice = null;
				if (!devices.isEmpty()) {
					subscriberDevice = devices.getFirst(); // or handle multiple results
				}

				List<OnbSubscriberDeviceHistory> subscriberDeviceHistory = subscriberDeviceHistoryRepoIface
						.findSubscriberDeviceHistory(suid);

				List<HashMap<String, String>> listOfMaps = subscriberDeviceHistory.stream().map(s -> {
					HashMap<String, String> strMap = new HashMap<>();
					strMap.put("device_uid", s.getDeviceUid());
					strMap.put("created_date", s.getCreatedDate());
					return strMap;
				}).toList();

				subscriberDeviceHistoryDetails.setSubscriber(subscriber);
				subscriberDeviceHistoryDetails.setSubscriberDevice(subscriberDevice);
				subscriberDeviceHistoryDetails.setSubscriberDeviceHistory(listOfMaps);
				return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_SUBSCIBER_DETAILS,
						subscriberDeviceHistoryDetails);
			}
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " getSusbcriberDeviceHistory Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse getTotp(TotpDto totpDto) {
		ResponseEntity<ApiResponse> res = null;
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<TotpDto> requestEntity = new HttpEntity<>(totpDto, headers);
			res = restTemplate.exchange(dtportal, HttpMethod.POST, requestEntity, ApiResponse.class);
            int status = res.getStatusCode().value();

            if (status == 400 || status == 401 || status == 403
                    || status == 404 || status == 415
                    || status == 500 || status == 501
                    || status == 503) {

                return exceptionHandlerUtil
                        .createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);

            } else if (status == 200 || status == 201) {

                ApiResponse responseBody = res.getBody();

                if (responseBody == null) {
                    throw new ApplicationException("Response body is null");
                }

                return exceptionHandlerUtil
                        .createSuccessResponseWithCustomMessage("", responseBody.getResult());
            }
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
		return exceptionHandlerUtil.createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);

	}

	@Override
	public ApiResponse getFCMToken(String subscriberUid) {
		try {
			if (!StringUtils.hasText(subscriberUid)) {
				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.id.can.be.null.or.empty");
			} else {
				OnbSubscriberFcmToken subscriberFcmToken = fcmTokenRepoIface.findBysubscriberUid(subscriberUid);
				if (subscriberFcmToken != null) {
					return exceptionHandlerUtil.createSuccessResponse(
							"api.response.subscriber.fcm.token.found.successfully", subscriberFcmToken);
				} else {
					return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.fcm.token.not.found");
				}
			}
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse deleteRecordBySuid(String subscriberUid) {
		try {
			OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(subscriberUid);
			if (subscriber != null) {
				subscriberDeletionRepository.deleteSubscriberRecord(subscriberUid);
				return exceptionHandlerUtil.successResponse(API_RESPONSE_SUBSCRIBER_RECORD_DELETED_SUCCESSFULLY);
			} else
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_NOT_FOUND);
		} catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);
		}
	}

	@Override
	public ApiResponse getAllSubscribersDataFromView() {
		try {
            logger.info(" inside getAllSubscribersDataFromView implimentation");
			List<OnbSubscriberCompleteDetail> subscriberCompleteDetailsList = subscriberCompleteDetailRepoIface
					.getAllActiveSubscribersDetails(Constant.ACTIVE);
			if (subscriberCompleteDetailsList == null) {
				return exceptionHandlerUtil.createErrorResponse(API_ERROR_NO_DATA_FOUND);
			}
			List<SubscriberDetailsDto> subscriberDetailsDtoList = new ArrayList<>();
			for (OnbSubscriberCompleteDetail details : subscriberCompleteDetailsList) {
				SubscriberDetailsDto subscriberDetailsDto = new SubscriberDetailsDto();
				subscriberDetailsDto.setEmail(details.getEmailId());
				subscriberDetailsDto.setPhoneNo(details.getMobileNumber());
				subscriberDetailsDto.setFullName(details.getFullName());
				subscriberDetailsDto.setSubscriberStatus(details.getSubscriberStatus());
				subscriberDetailsDtoList.add(subscriberDetailsDto);
			}
			return exceptionHandlerUtil.createSuccessResponse(API_RESPONSE_SUBSCIBER_DETAILS,subscriberDetailsDtoList);

		} catch (Exception e) {

				logger.error(UNEXPECTED_EXCEPTION, e);
			return exceptionHandlerUtil.createErrorResponse(API_ERROR_SOMETHING_WENT_WRONG);

		}

	}


    @Override
    public ApiResponse getSubscriberDetails(DocumentRequestDTO requestDTO) {

        ApiResponse response = new ApiResponse();

        try {
            OnbSubscriber subscriber = null;

            if ("SUID".equalsIgnoreCase(requestDTO.getDocumentType())) {
                subscriber = subscriberRepoIface.findBysubscriberUid(requestDTO.getDocumentNumber());
            }
            // Case 2: Normal document search
            else {
                subscriber = subscriberRepoIface.findByDocument(requestDTO.getDocumentNumber());
            }

            // If subscriber not found
            if (subscriber == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
            }

            // Fetch FCM Token
            String fcmToken =
                    subscriberFcmTokenRepoIface.getFcmTokenBySubscriberUid(subscriber.getSubscriberUid());

            // Build Response DTO
            SubscriberResponseDTO dto = new SubscriberResponseDTO();
            dto.setSubscriberUid(subscriber.getSubscriberUid());
            dto.setFullName(subscriber.getFullName());
            dto.setPassportNumber(subscriber.getPassportNumber());
            dto.setNationalIdNumber(subscriber.getNationalIdNumber());
            dto.setMobileNumber(subscriber.getMobileNumber());
            dto.setFcmToken(fcmToken);

            // Success response
            response.setSuccess(true);
            response.setMessage("Subscriber fetched successfully");
            response.setResult(dto);

            return response;

        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.something.went.wrong.please.try.after.sometime"
            );
        }
    }

}
