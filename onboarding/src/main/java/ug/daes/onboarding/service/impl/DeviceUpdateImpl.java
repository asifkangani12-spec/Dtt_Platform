package ug.daes.onboarding.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
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

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ug.daes.onboarding.constant.Constant;
import ug.daes.onboarding.constant.DeviceUpdatePolicy;
import ug.daes.onboarding.dto.*;
import ug.daes.onboarding.model.*;
import ug.daes.onboarding.repository.*;
import ug.daes.onboarding.service.iface.DeviceUpdateIface;
import ug.daes.onboarding.service.iface.PolicyIface;
import ug.daes.onboarding.service.iface.SubscriberServiceIface;
import ug.daes.onboarding.service.iface.TemplateServiceIface;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ug.daes.onboarding.service.impl.SubscriberServiceImpl.findLatestOnboardedSub;

@Service
public class DeviceUpdateImpl implements DeviceUpdateIface {

	private static final Logger logger = LoggerFactory.getLogger(DeviceUpdateImpl.class);

	private static final String CLASS = "DeviceUpdateImpl";
	private static final String EXCEPTION = "Unexpected exception";
	private static final String API_ERROR_ACCESS_BLOCKED = "api.error.device.access.blocked";
	private static final String NEW_DEVICE_ALREADY_USED = "api.response.new.device.is.ready.to.be.used";
	private static final String DEVICE_CHANGE = " activateNewDevice device change mongo {} ";
	private static final String UPDATE_SUBSCRIBER = "updateSubscriberDeviceAndHistory Exception {}";
	private static final String DEVICE_UPDATE_PREFIX = "DEVICE_UPDATE | ";
	private static final String OTHER = "OTHER";
	private static final String MOBILE_ALREDY_USED = "api.error.this.mobile.number.is.already.used.with.differenet.email.id";
	private static final String WELCOME_BACK = "api.response.services.now.accessible.on.this.device.welcome.back";

	private final SubscriberRepoIface subscriberRepoIface;
	private final SubscriberDeviceRepoIface deviceRepoIface;

	private final LogModelServiceImpl logModelServiceImpl;
	private final SubscriberOnboardingDataRepoIface onboardingDataRepoIface;
	private final TemplateServiceIface templateServiceIface;
	private final SubscriberStatusRepoIface statusRepoIface;
	private final SubscriberCertificatesRepoIface subscriberCertificatesRepoIface;
	private final SubscriberCertPinHistoryRepoIface subscriberCertPinHistoryRepoIface;
	private final SubscriberFcmTokenRepoIface fcmTokenRepoIface;
	private final SubscriberDeviceHistoryRepoIface subscriberDeviceHistoryRepoIface;
	private final PolicyIface policyIface;
	private final SubscriberServiceIface subscriberServiceIface;
	private final DevicePolicyRepository devicePolicyRepository;
    private final ExceptionHandlerUtil exceptionHandlerUtil;


	public DeviceUpdateImpl(
			@Lazy SubscriberServiceIface subscriberServiceIface,
			SubscriberRepoIface subscriberRepoIface,
			SubscriberDeviceRepoIface deviceRepoIface,

			LogModelServiceImpl logModelServiceImpl,
			SubscriberOnboardingDataRepoIface onboardingDataRepoIface,
			TemplateServiceIface templateServiceIface,
			SubscriberStatusRepoIface statusRepoIface,
			SubscriberCertificatesRepoIface subscriberCertificatesRepoIface,
			SubscriberCertPinHistoryRepoIface subscriberCertPinHistoryRepoIface,
			SubscriberFcmTokenRepoIface fcmTokenRepoIface,
			SubscriberDeviceHistoryRepoIface subscriberDeviceHistoryRepoIface,
			PolicyIface policyIface,
			DevicePolicyRepository devicePolicyRepository,
			ExceptionHandlerUtil exceptionHandlerUtil
	) {
		this.subscriberServiceIface = subscriberServiceIface;
		this.subscriberRepoIface = subscriberRepoIface;
		this.deviceRepoIface = deviceRepoIface;

		this.logModelServiceImpl = logModelServiceImpl;
		this.onboardingDataRepoIface = onboardingDataRepoIface;
		this.templateServiceIface = templateServiceIface;
		this.statusRepoIface = statusRepoIface;
		this.subscriberCertificatesRepoIface = subscriberCertificatesRepoIface;
		this.subscriberCertPinHistoryRepoIface = subscriberCertPinHistoryRepoIface;
		this.fcmTokenRepoIface = fcmTokenRepoIface;
		this.subscriberDeviceHistoryRepoIface = subscriberDeviceHistoryRepoIface;
		this.policyIface = policyIface;
		this.devicePolicyRepository = devicePolicyRepository;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}



	@Value("${device.update.min.policy}")
	private long minhour;
	
	@Value("${is.onboarding.fee}")
	private boolean selfieRequired;

	@Value("${device.update.max.policy}")
	private long maxhour;



	@Override
	public void updateSubscriberDeviceAndHistory(OnbSubscriberDevice oldDevice, String newDeviceUid) {
		// save to subscriber device history
		try {
			logger.info(CLASS + "updateSubscriberDeviceAndHistory oldDevice and newDeviceUid {}, {}", oldDevice,
					newDeviceUid);

			OnbSubscriberDeviceHistory subscriberDeviceHistory = new OnbSubscriberDeviceHistory();
			subscriberDeviceHistory.setDeviceUid(oldDevice.getDeviceUid());
			subscriberDeviceHistory.setDeviceStatus(Constant.DEVICE_STATUS_DISABLED);
			subscriberDeviceHistory.setSubscriberUid(oldDevice.getSubscriberUid());
			subscriberDeviceHistory.setCreatedDate(AppUtil.getDate());
			subscriberDeviceHistory.setUpdatedDate(AppUtil.getDate());
			subscriberDeviceHistoryRepoIface.save(subscriberDeviceHistory);

			oldDevice.setDeviceUid(newDeviceUid);
			oldDevice.setDeviceStatus(Constant.DEVICE_STATUS_ACTIVE);

			oldDevice.setUpdatedDate(AppUtil.getDate());
			deviceRepoIface.save(oldDevice);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			logger.error(EXCEPTION, ex);
			logger.error(CLASS + UPDATE_SUBSCRIBER, ex.getMessage());
		} catch (Exception e) {
			logger.error(EXCEPTION, e);
			logger.error(CLASS + UPDATE_SUBSCRIBER, e.getMessage());
		}

	}

    @Override
    public ApiResponse activateNewDevice(DeviceInfo deviceInfo, MobileOTPDto mobileOTPDto) {
        try {

            if (Objects.isNull(deviceInfo) && Objects.isNull(mobileOTPDto)) {
                return exceptionHandlerUtil.createErrorResponse("api.error.device.info.and.mobile.otp.dtos.cant.null");
            }

            if (Objects.isNull(deviceInfo)) {
                return exceptionHandlerUtil.createErrorResponse("api.error.device.info.cant.null");
            }
            int countMobile = subscriberRepoIface.countSubscriberMobile(mobileOTPDto.getSubscriberMobileNumber());
            int countEmail = subscriberRepoIface.countSubscriberEmailId(
                    mobileOTPDto.getSubscriberEmail().toLowerCase());

            OnbSubscriber subscriber = subscriberRepoIface.getSubscriberUidByEmailAndMobile(
                    mobileOTPDto.getSubscriberEmail(),
                    mobileOTPDto.getSubscriberMobileNumber());

            Date startTime = new Date();

            if (countEmail == 1 && countMobile == 1) {

                OnbSubscriberDevice device = deviceRepoIface.getSubscriber(subscriber.getSubscriberUid());

                if (device.getDeviceStatus().equals(Constant.DEVICE_STATUS_ACTIVE)
                        || device.getDeviceStatus().equals(Constant.DEVICE_STATUS_DISABLED)) {

                        handleDeviceUpdate(device, deviceInfo, mobileOTPDto, subscriber, startTime);

                        return exceptionHandlerUtil.successResponse(WELCOME_BACK);
                    }
                }


            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.the.device.is.already.registered.with.either.same.or.different.email.id.and.mobile.number");

        } catch (Exception e) {
            logger.error(EXCEPTION, e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
    private void handleDeviceUpdate(OnbSubscriberDevice device,
                                    DeviceInfo deviceInfo,
                                    MobileOTPDto mobileOTPDto,
                                    OnbSubscriber subscriber,
                                    Date startTime) throws ParseException {

        updateSubscriberDeviceAndHistory(device, deviceInfo.getDeviceId());

        OnbSubscriber subscriber2 = subscriberRepoIface.findBysubscriberUid(
                subscriber.getSubscriberUid());

        Date endTime = new Date();

        String deviceIinfo = mobileOTPDto.getOsName() + " | "
                + deviceInfo.getOsVersion() + " | "
                + deviceInfo.getAppVersion() + " | "
                + mobileOTPDto.getDeviceInfo();

        String message = DEVICE_UPDATE_PREFIX
                + deviceInfo.getDeviceId() + "|"
                + device.getDeviceUid() + "|"
                + AppUtil.getDate() + "|"
                + deviceIinfo;

        logger.info(CLASS + DEVICE_CHANGE, message);

        logModelServiceImpl.setLogModelDTO(
                true,
                device.getSubscriberUid(),
                null,
                OTHER,
                null,
                message,
                startTime,
                endTime,
                null
        );

        logger.info("Subscriber details ::{}",subscriber);

            subscriber.setAppVersion(deviceInfo.getAppVersion());
            subscriber.setOsVersion(deviceInfo.getOsVersion());
            subscriber.setOsName(mobileOTPDto.getOsName());
            subscriber.setDeviceInfo(mobileOTPDto.getDeviceInfo());
            subscriberRepoIface.save(subscriber2);

        subscriberServiceIface.deviceUpdatedSendEmail(subscriber.getEmailId());
        updateFcmToken(subscriber.getSubscriberUid(), mobileOTPDto.getFcmToken());
    }
	private void updateFcmToken(String suid, String fcmToken) {
		try {
			logger.info(CLASS + "updateFcmToken suid and fcmToken {}, {}", suid, fcmToken);
			OnbSubscriberFcmToken subscriberFcmToken = fcmTokenRepoIface.findBysubscriberUid(suid);
			subscriberFcmToken.setFcmToken(fcmToken);
			subscriberFcmToken.setCreatedDate(AppUtil.getDate());
			fcmTokenRepoIface.save(subscriberFcmToken);
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			logger.error(EXCEPTION, ex);
			logger.error(CLASS + "updateFcmToken Exception {}", ex.getMessage());
		} catch (Exception e) {
			logger.error(EXCEPTION, e);
			logger.error(CLASS + "updateFcmToken Exception {}", e.getMessage());
		}

	}

    private NewDeviceDTO setNewDeviceResponse(OnbSubscriber subscriber) {

        NewDeviceDTO newDeviceDTO = new NewDeviceDTO();
        try {

            if (subscriber == null)
                return newDeviceDTO;

            SubscriberDetailsReponseDTO responseDTO = new SubscriberDetailsReponseDTO();

            responseDTO.setSuID(subscriber.getSubscriberUid());
            newDeviceDTO.setEmail(subscriber.getEmailId());
            newDeviceDTO.setMobileNumber(subscriber.getMobileNumber());

            OnbSubscriberStatus status =
                    statusRepoIface.findBysubscriberUid(subscriber.getSubscriberUid());

            responseDTO.setSubscriberStatus(
                    status != null ? status.getSubscriberStatus() : null);

            SubscriberDetails subscriberDetails =
                    buildOnboardingDetails(subscriber, newDeviceDTO);

            responseDTO.setSubscriberDetails(subscriberDetails);
            newDeviceDTO.setSubscriberStatusDetails(responseDTO);
            newDeviceDTO.setNewDevice(true);

            return newDeviceDTO;

        } catch (JDBCConnectionException | ConstraintViolationException | DataException
                 | LockAcquisitionException | PessimisticLockException
                 | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {

            logger.error(CLASS + "setNewDeviceResponse Exception {}", ex.getMessage());
            return newDeviceDTO;

        } catch (Exception e) {
            logger.error(EXCEPTION, e);
            logger.error(CLASS + "setNewDeviceResponse Exception {}", e.getMessage());
            return newDeviceDTO;
        }
    }
    private SubscriberDetails buildOnboardingDetails(OnbSubscriber subscriber,
                                                     NewDeviceDTO newDeviceDTO) {

        List<OnbSubscriberOnboardingData> onboardingDataList =
                onboardingDataRepoIface.getBySubUid(subscriber.getSubscriberUid());

        OnbSubscriberOnboardingData onboardingData = null;

        if (onboardingDataList != null) {
            if (onboardingDataList.size() > 1) {
                onboardingData = findLatestOnboardedSub(onboardingDataList);
            } else if (onboardingDataList.size() == 1) {
                onboardingData = onboardingDataList.get(0);
            }
        }

        SubscriberDetails subscriberDetails = new SubscriberDetails();

        if (onboardingData == null) {
            newDeviceDTO.setIdDocNumber(null);
            newDeviceDTO.setSelfieUri(null);
            return subscriberDetails;
        }

        newDeviceDTO.setIdDocNumber(onboardingData.getIdDocNumber());

        if (selfieRequired) {
            ApiResponse response =
                    subscriberServiceIface.getSubscriberSelfie(onboardingData.getSelfieUri());

            if (response.isSuccess())
                newDeviceDTO.setSelfieUri((String) response.getResult());
            else
                newDeviceDTO.setSelfieUri("");
        } else {
            newDeviceDTO.setSelfieUri(onboardingData.getSelfie());
        }

        return buildSubscriberDetails(subscriber, onboardingData, subscriberDetails);
    }
    private SubscriberDetails buildSubscriberDetails(OnbSubscriber subscriber,
                                                     OnbSubscriberOnboardingData onboardingData,
                                                     SubscriberDetails subscriberDetails) {

        String method = onboardingData.getOnboardingMethod();

        ApiResponse editTemplateDTORes =
                templateServiceIface.getTemplateLatestById(onboardingData.getTemplateId());

        if (!editTemplateDTORes.isSuccess())
            return null;

        EditTemplateDTO editTemplateDTO =
                (EditTemplateDTO) editTemplateDTORes.getResult();

        List<String> statuses =
                subscriberCertificatesRepoIface.getSubscriberCertificateStatus(
                        subscriber.getSubscriberUid(), Constant.SIGN, Constant.ACTIVE);

        String certStatus = statuses.isEmpty() ? null : statuses.get(0);

        subscriberDetails.setSubscriberName(subscriber.getFullName());
        subscriberDetails.setOnboardingMethod(method);
        subscriberDetails.setTemplateDetails(editTemplateDTO);
        subscriberDetails.setCertificateStatus(
                certStatus != null ? certStatus : Constant.PENDING);

        buildPinStatus(subscriber, certStatus, subscriberDetails);

        return subscriberDetails;
    }
    private void buildPinStatus(OnbSubscriber subscriber,
                                String certStatus,
                                SubscriberDetails subscriberDetails) {

        PinStatus pinStatus = new PinStatus();

        if (certStatus != null && certStatus.equals(Constant.ACTIVE)) {

            OnbSubscriberCertificatePinHistory certificatePinHistory =
                    subscriberCertPinHistoryRepoIface
                            .findBysubscriberUid(subscriber.getSubscriberUid());

            if (certificatePinHistory != null) {

                if (certificatePinHistory.getAuthPinList() != null)
                    pinStatus.setAuthPinSet(true);

                if (certificatePinHistory.getSignPinList() != null)
                    pinStatus.setSignPinSet(true);
            }
        }

        subscriberDetails.setPinStatus(pinStatus);


    }

    @Override
    public ApiResponse validateSubscriberAndDevice(DeviceInfo deviceInfo, MobileOTPDto mobileOTPDto) {
        try {
            int countDevice = subscriberRepoIface.countSubscriberDevice(deviceInfo.getDeviceId());
            int countMobile = subscriberRepoIface.countSubscriberMobile(mobileOTPDto.getSubscriberMobileNumber());
            int countEmail = subscriberRepoIface
                    .countSubscriberEmailId(mobileOTPDto.getSubscriberEmail().toLowerCase());

            logger.info("{} validateSubscriberAndDeviceNew ::: {} mobileOTPDto :: {}",CLASS, deviceInfo ,mobileOTPDto);
            System.out.println("countDevice :: " + countDevice + " countMobile :: " + countMobile + " countEmail :: "
                    + countEmail);

            System.out.println("Device Id:: " + deviceInfo.getDeviceId() + " Mobile :: " + mobileOTPDto.getSubscriberMobileNumber() + " Email :: "
                    + mobileOTPDto.getSubscriberEmail().toLowerCase());

            OnbSubscriber subscriber = subscriberRepoIface.getSubscriberDetailsByEmailAndMobile(
                    mobileOTPDto.getSubscriberEmail(), mobileOTPDto.getSubscriberMobileNumber());

            NewDeviceDTO newDeviceDTO = setNewDeviceResponse(subscriber);

            String date = null;

            if (countEmail >= 1 && countMobile >= 1 && subscriber == null) {
                return exceptionHandlerUtil
                        .createErrorResponse("api.error.this.mobile.no.is.already.register.with.different.email.id");
            } else if (countDevice == 0 && countEmail == 0 && countMobile == 0) {
                newDeviceDTO.setNewDevice(false);

                return exceptionHandlerUtil.createSuccessResponse("api.response.first.time.registering.onboarding",
                        newDeviceDTO);

            } else if (countDevice == 0 && countEmail == 1 && countMobile == 0) {
                return exceptionHandlerUtil
                        .createErrorResponse("api.error.this.email.id.is.already.used.with.differenet.mobile.no");
            } else if (countDevice == 0 && countEmail == 0 && countMobile == 1) {
                return exceptionHandlerUtil
                        .createErrorResponse("api.error.this.mobile.number.is.already.used.with.differenet.email.id");

            } else if (countDevice == 0 && countEmail == 1 && countMobile == 1) {

                OnbSubscriberDevice subdevice = deviceRepoIface.getSubscriber(subscriber.getSubscriberUid());

                if (subdevice.getDeviceStatus().equals(Constant.DEVICE_STATUS_ACTIVE)) {

                    date = Objects.equals(subdevice.getCreatedDate(), subdevice.getUpdatedDate())
                            ? subdevice.getCreatedDate()
                            : subdevice.getUpdatedDate();
                    Optional<OnbDevicePolicyModel> devicePolicyModel = Optional
                            .ofNullable(devicePolicyRepository.getDevicePolicyHour());
                    long devicePolicyHour = 0;
                    if (devicePolicyModel.isPresent()) {
                        devicePolicyHour = devicePolicyModel.get().getDevicePolicyHour();
                        if (devicePolicyHour <= minhour) {
                            devicePolicyHour = minhour;
                        } else if (devicePolicyHour >= maxhour) {
                            devicePolicyHour = maxhour;
                        }
                    } else {
                        devicePolicyHour = minhour;
                    }
                    ApiResponse policyResponse = policyIface.checkPolicyRange(date, DeviceUpdatePolicy.PATTERN,
                            devicePolicyHour);

                    if (!policyResponse.isSuccess()) {

                        return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                                "api.error.device.access.blocked",
                                devicePolicyHour);

                    }
                    return exceptionHandlerUtil.createSuccessResponse("api.response.new.device.is.ready.to.be.used",
                            newDeviceDTO);

                }

                return exceptionHandlerUtil.createSuccessResponse("api.response.new.device.is.ready.to.be.used",
                        newDeviceDTO);
            } else {
                OnbSubscriberDevice subDevice = deviceRepoIface.findBydeviceUidAndStatus(deviceInfo.getDeviceId(),
                        Constant.DEVICE_STATUS_ACTIVE);

                if (subDevice == null && subscriber == null) {
                    newDeviceDTO.setNewDevice(false);
                    return exceptionHandlerUtil.createSuccessResponse("api.response.first.time.registering.onboarding",
                            newDeviceDTO);

                } else if (subDevice != null) {
                    if (subDevice != null && subscriber == null) {

                        return exceptionHandlerUtil.createErrorResponseWithResult(
                                "api.error.this.mobile.number.is.already.used.with.differenet.email.id", newDeviceDTO);

                    } else if (subDevice.getDeviceStatus().equalsIgnoreCase(Constant.DEVICE_STATUS_ACTIVE)
                            && !subDevice.getSubscriberUid().equals(subscriber.getSubscriberUid())) {

                        return exceptionHandlerUtil.createErrorResponse(
                                "api.error.this.mobile.number.is.already.used.with.differenet.email.id");

                    } else {
                        OnbSubscriberDevice subscriberDevice = deviceRepoIface
                                .getSubscriber(subscriber.getSubscriberUid());
                        if (subscriber.getSubscriberUid().equals(subDevice.getSubscriberUid())) {
                            newDeviceDTO.setNewDevice(false);

                            return exceptionHandlerUtil.createSuccessResponse("api.response.app.is.re.installed",
                                    newDeviceDTO);
                        }

                        if (subscriberDevice.getDeviceStatus().equals(Constant.DEVICE_STATUS_ACTIVE)) {

                            date = Objects.equals(subscriberDevice.getCreatedDate(), subscriberDevice.getUpdatedDate())
                                    ? subscriberDevice.getCreatedDate()
                                    : subscriberDevice.getUpdatedDate();
                            Optional<OnbDevicePolicyModel> devicePolicyModel = Optional
                                    .ofNullable(devicePolicyRepository.getDevicePolicyHour());
                            long devicePolicyHour = 0;
                            if (devicePolicyModel.isPresent()) {
                                devicePolicyHour = devicePolicyModel.get().getDevicePolicyHour();
                                if (devicePolicyHour <= minhour) {
                                    devicePolicyHour = minhour;
                                } else if (devicePolicyHour >= maxhour) {
                                    devicePolicyHour = maxhour;
                                }
                            } else {
                                devicePolicyHour = minhour;
                            }

                            ApiResponse policyResponse = policyIface.checkPolicyRange(date, DeviceUpdatePolicy.PATTERN,
                                    devicePolicyHour);
                            if (!policyResponse.isSuccess()) {

                                return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                                        "api.error.device.access.blocked",
                                        devicePolicyHour);
                            }

                            return exceptionHandlerUtil
                                    .createSuccessResponse("api.response.new.device.is.ready.to.be.used", newDeviceDTO);

                        } else {

                            return exceptionHandlerUtil
                                    .createSuccessResponse("api.response.new.device.is.ready.to.be.used", newDeviceDTO);
                        }
                    }
                } else {
                    OnbSubscriberDevice subscriberDevice = deviceRepoIface.getSubscriber(subscriber.getSubscriberUid());

                    System.out.println(" subscriberDevice   ::::::::::status "+subscriberDevice.getDeviceStatus()+" device id ::: "+subscriberDevice.getDeviceUid());
                    if (subscriberDevice.getDeviceStatus().equalsIgnoreCase(Constant.DEVICE_STATUS_ACTIVE)) {

                        System.out.println(" subscriberDevice   :::::inside if condition :::::status "+subscriberDevice.getDeviceStatus()+" device id ::: "+subscriberDevice.getDeviceUid());
                        date = Objects.equals(subscriberDevice.getCreatedDate(), subscriberDevice.getUpdatedDate())
                                ? subscriberDevice.getCreatedDate()
                                : subscriberDevice.getUpdatedDate();
                        Optional<OnbDevicePolicyModel> devicePolicyModel = Optional
                                .ofNullable(devicePolicyRepository.getDevicePolicyHour());
                        long devicePolicyHour = 0;
                        if (devicePolicyModel.isPresent()) {
                            devicePolicyHour = devicePolicyModel.get().getDevicePolicyHour();
                            if (devicePolicyHour <= minhour) {
                                devicePolicyHour = minhour;
                            } else if (devicePolicyHour >= maxhour) {
                                devicePolicyHour = maxhour;
                            }
                        } else {
                            devicePolicyHour = minhour;
                        }

                        ApiResponse policyResponse = policyIface.checkPolicyRange(date, DeviceUpdatePolicy.PATTERN,
                                devicePolicyHour);
                        if (!policyResponse.isSuccess()) {

                            return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
                                    "api.error.device.access.blocked",
                                    devicePolicyHour);

                        }

                        return exceptionHandlerUtil.createSuccessResponse("api.response.new.device.is.ready.to.be.used",
                                newDeviceDTO);
                    } else {

                        if(subscriberDevice.getDeviceUid().equalsIgnoreCase(deviceInfo.getDeviceId()) && subscriberDevice.getDeviceStatus().equalsIgnoreCase(Constant.DEVICE_STATUS_DISABLED) && subscriberDevice.getSubscriberUid().equalsIgnoreCase(subscriber.getSubscriberUid())) {
                            newDeviceDTO.setNewDevice(false);
                            return exceptionHandlerUtil.createSuccessResponse("api.response.new.device.is.ready.to.be.used",
                                    newDeviceDTO);
                        }
                        return exceptionHandlerUtil.createSuccessResponse("api.response.new.device.is.ready.to.be.used",
                                newDeviceDTO);

                    }

                }
            }
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            logger.error(CLASS + "validateSubscriberAndDevice Exception {}", e.getMessage());
            return exceptionHandlerUtil.handleException(e);

        }
    }

    private ApiResponse processValidation(DeviceInfo deviceInfo,
                                          int countDevice,
                                          int countMobile,
                                          int countEmail,
                                          OnbSubscriber subscriber,
                                          NewDeviceDTO newDeviceDTO) {


        if (countEmail >= 1 && countMobile >= 1 && subscriber == null) {
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.this.mobile.no.is.already.register.with.different.email.id");
        }

        if (countDevice == 0 && countEmail == 0 && countMobile == 0) {

            newDeviceDTO.setNewDevice(false);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.first.time.registering.onboarding",
                    newDeviceDTO);
        }

        if (countDevice == 0 && countEmail == 1 && countMobile == 0) {
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.this.email.id.is.already.used.with.differenet.mobile.no");
        }

        if (countDevice == 0 && countEmail == 0 && countMobile == 1) {
            return exceptionHandlerUtil.createErrorResponse(MOBILE_ALREDY_USED);
        }

        if (countDevice == 0 && countEmail == 1 && countMobile == 1) {
            return handleEmailMobileCase(subscriber, newDeviceDTO);
        }

        return handleFinalCase(deviceInfo, subscriber, newDeviceDTO);
    }
    private ApiResponse handleEmailMobileCase(OnbSubscriber subscriber,
                                              NewDeviceDTO newDeviceDTO) {

        OnbSubscriberDevice subdevice =
                deviceRepoIface.getSubscriber(subscriber.getSubscriberUid());

        if (subdevice.getDeviceStatus()
                .equals(Constant.DEVICE_STATUS_ACTIVE)) {

            String date = resolveDate(subdevice);

            long devicePolicyHour = getDevicePolicyHour();

            ApiResponse policyResponse =
                    policyIface.checkPolicyRange(
                            date,
                            DeviceUpdatePolicy.PATTERN,
                            devicePolicyHour);

            if (!policyResponse.isSuccess()) {

                return exceptionHandlerUtil
                        .createFailedResponseWithCustomMessage(
                                API_ERROR_ACCESS_BLOCKED,
                                devicePolicyHour);
            }
        }

        return exceptionHandlerUtil.createSuccessResponse(
                NEW_DEVICE_ALREADY_USED,
                newDeviceDTO);
    }
    private ApiResponse handleFinalCase(DeviceInfo deviceInfo,
                                        OnbSubscriber subscriber,
                                        NewDeviceDTO newDeviceDTO) {

        OnbSubscriberDevice subDevice =
                deviceRepoIface.findBydeviceUidAndStatus(
                        deviceInfo.getDeviceId(),
                        Constant.DEVICE_STATUS_ACTIVE);

        if (subDevice == null && subscriber == null) {

            newDeviceDTO.setNewDevice(false);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.first.time.registering.onboarding",
                    newDeviceDTO);
        }

        if (subDevice != null) {

            if (subscriber == null) {
                return exceptionHandlerUtil
                        .createErrorResponseWithResult(
                                MOBILE_ALREDY_USED,
                                newDeviceDTO);
            }

            if (subDevice.getDeviceStatus()
                    .equalsIgnoreCase(Constant.DEVICE_STATUS_ACTIVE)
                    && !subDevice.getSubscriberUid()
                    .equals(subscriber.getSubscriberUid())) {

                return exceptionHandlerUtil
                        .createErrorResponse(MOBILE_ALREDY_USED);
            }

            return exceptionHandlerUtil.createSuccessResponse(
                    NEW_DEVICE_ALREADY_USED,
                    newDeviceDTO);
        }

        OnbSubscriberDevice subscriberDevice =
                deviceRepoIface.getSubscriber(subscriber.getSubscriberUid());

        if (subscriberDevice.getDeviceStatus()
                .equalsIgnoreCase(Constant.DEVICE_STATUS_ACTIVE)) {

            String date = resolveDate(subscriberDevice);

            long devicePolicyHour = getDevicePolicyHour();

            ApiResponse policyResponse =
                    policyIface.checkPolicyRange(
                            date,
                            DeviceUpdatePolicy.PATTERN,
                            devicePolicyHour);

            if (!policyResponse.isSuccess()) {

                return exceptionHandlerUtil
                        .createFailedResponseWithCustomMessage(
                                API_ERROR_ACCESS_BLOCKED,
                                devicePolicyHour);
            }

            return exceptionHandlerUtil.createSuccessResponse(
                    NEW_DEVICE_ALREADY_USED,
                    newDeviceDTO);
        }

        if (subscriberDevice.getDeviceUid()
                .equalsIgnoreCase(deviceInfo.getDeviceId())
                && subscriberDevice.getDeviceStatus()
                .equalsIgnoreCase(Constant.DEVICE_STATUS_DISABLED)
                && subscriberDevice.getSubscriberUid()
                .equalsIgnoreCase(subscriber.getSubscriberUid())) {

            newDeviceDTO.setNewDevice(false);

            return exceptionHandlerUtil.createSuccessResponse(
                    NEW_DEVICE_ALREADY_USED,
                    newDeviceDTO);
        }

        return exceptionHandlerUtil.createSuccessResponse(
                NEW_DEVICE_ALREADY_USED,
                newDeviceDTO);
    }
    private String resolveDate(OnbSubscriberDevice device) {

        return Objects.equals(device.getCreatedDate(), device.getUpdatedDate())
                ? device.getCreatedDate()
                : device.getUpdatedDate();
    }
    private long getDevicePolicyHour() {

        Optional<OnbDevicePolicyModel> devicePolicyModel =
                Optional.ofNullable(
                        devicePolicyRepository.getDevicePolicyHour());

        long devicePolicyHour = minhour;

        if (devicePolicyModel.isPresent()) {

            devicePolicyHour =
                    devicePolicyModel.get().getDevicePolicyHour();

            if (devicePolicyHour <= minhour) {
                devicePolicyHour = minhour;
            } else if (devicePolicyHour >= maxhour) {
                devicePolicyHour = maxhour;
            }
        }

        return devicePolicyHour;
    }

	public void updateSubscriberDeviceHistory(OnbSubscriberDevice oldDevice, String newDeviceUid) {
		// save to subscriber device history
		try {
			logger.error(CLASS + "updateSubscriberDeviceAndHistory oldDevice and newDeviceUid {}, {}", oldDevice,
					newDeviceUid);

			OnbSubscriberDeviceHistory subscriberDeviceHistory = new OnbSubscriberDeviceHistory();
			subscriberDeviceHistory.setDeviceUid(oldDevice.getDeviceUid());
			subscriberDeviceHistory.setDeviceStatus(Constant.DEVICE_STATUS_DISABLED);
			subscriberDeviceHistory.setSubscriberUid(oldDevice.getSubscriberUid());
			subscriberDeviceHistory.setCreatedDate(AppUtil.getDate());
			subscriberDeviceHistory.setUpdatedDate(AppUtil.getDate());
			subscriberDeviceHistoryRepoIface.save(subscriberDeviceHistory);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			logger.error(EXCEPTION, ex);
			logger.error(CLASS + UPDATE_SUBSCRIBER, ex.getMessage());
		} catch (Exception e) {
				logger.error(EXCEPTION, e);
			logger.error(CLASS + UPDATE_SUBSCRIBER, e.getMessage());
		}

	}
}
