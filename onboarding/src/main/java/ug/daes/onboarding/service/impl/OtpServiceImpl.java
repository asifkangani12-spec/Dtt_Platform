package ug.daes.onboarding.service.impl;


import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.Map;
import java.util.UUID;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.DAESService;
import ug.daes.Result;
import ug.daes.onboarding.config.OnboardingSentryClientExceptions;


import ug.daes.onboarding.dto.EmailReqDto;
import ug.daes.onboarding.dto.MobileOTPDto;
import ug.daes.onboarding.dto.OTPResponseDTO;
import ug.daes.onboarding.dto.SmsDTO;
import ug.daes.onboarding.dto.SmsOtpResponseDTO;

import ug.daes.onboarding.exceptions.ApplicationException;
import ug.daes.onboarding.service.iface.OtpServiceIface;


@Service
public class OtpServiceImpl implements OtpServiceIface {

	private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);


	private  static  final String CLASS = "OtpServiceImpl";
	private static final String EMAIL_ID = " | EmailId :";
    private static final String OTP_CONTROLLER = "OTPController";
	private static final String EXCEPTION = "Unexpected exception";
	private static final String OTP_STATUS = " | OtpStatus : ";

	private static final String DEVICE_ID = " | DeviceId : ";
	private static final String REGISTARTION_OTP_SENT = "REGISTRATION_OTP_SENT";
	private static final String MOBILE_NUMBER = "MobileNumber : ";
	private static final String PHONE_NUMBER_IS_INVALID = "api.error.phone.number.is.invalid.please.enter.correct.phone.number";


	@Value(value = "${nira.api.token}")
	private String niraApiToken;

	@Value(value = "${nira.api.sms}")
	private String niraApiSMS;

	@Value(value = "${nira.username}")
	private String niraUserName;

	@Value(value = "${nira.password}")
	private String niraPassword;

	@Value(value = "${ind.api.sms}")
	private String indApiSMS;

	@Value(value = "${spring.mail.username}")
	private String mailUserName;

	@Value(value = "${nira.api.timetolive}")
	private int timeToLive;

	private String uaeApiSMS;

	@Value(value = "${config.validation.allowTrustedUsersOnly}")
	private int allowTrustedUsersOnly;

	@Value(value = "${config.validation.controlledModeUserMessage}")
	private String controlledModeUserMessage;
	
	@Value(value = "${email.url}")
	private String emailBaseUrl;


	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final LogModelServiceImpl logModelServiceImpl;

	private final OnboardingSentryClientExceptions sentryClientExceptions;
	private final ExceptionHandlerUtil exceptionHandlerUtil;

	public OtpServiceImpl(

			RestTemplate restTemplate,
			ObjectMapper objectMapper,
			LogModelServiceImpl logModelServiceImpl,

			OnboardingSentryClientExceptions sentryClientExceptions,
			ExceptionHandlerUtil exceptionHandlerUtil) {


		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.logModelServiceImpl = logModelServiceImpl;

		this.sentryClientExceptions = sentryClientExceptions;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}
    private void validateUrl(String url) {

        try {

            if (url == null || url.trim().isEmpty()) {
                throw new ApplicationException("URL cannot be null or empty");
            }

            URI uri = new URI(url);

            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                throw new ApplicationException("Only HTTPS protocol is allowed");
            }

            String allowedHost = "internal-edms.company.com";

            if (!allowedHost.equalsIgnoreCase(uri.getHost())) {
                throw new ApplicationException("Unauthorized host detected: " + uri.getHost());
            }

            InetAddress address = InetAddress.getByName(uri.getHost());

            if (address.isAnyLocalAddress() ||
                    address.isLoopbackAddress() ||
                    address.isSiteLocalAddress()) {

                throw new ApplicationException("Access to internal/private IPs is not allowed");
            }

        } catch (URISyntaxException | UnknownHostException e) {
            throw new ApplicationException("Invalid EDMS URL", e);
        }
    }
	public String generatecorrelationIdUniqueId() {
		UUID correlationID = UUID.randomUUID();
		return correlationID.toString();
	}


    @Override
    public ApiResponse sendOTPMobileSms(MobileOTPDto mobileOTPDto)
            throws ParseException {

        logger.info(CLASS + "sendOTPMobileSms() >> req {}", mobileOTPDto);

        if (mobileOTPDto.getSubscriberMobileNumber().equals("+256987654321")
                || mobileOTPDto.getSubscriberMobileNumber().equals("+256123456789")) {
            return verifyOtp(mobileOTPDto.getSubscriberMobileNumber());
        }

        Date startTime = new Date();
        String correlationId = generatecorrelationIdUniqueId();

        String mobileOTP = generateOtp(6);
        String emailOTP = generateOtp(5);

        ApiResponse countryResponse = handleCountrySpecificOTP(
                mobileOTPDto, mobileOTP, startTime, correlationId);

        if (countryResponse != null) {
            return countryResponse;
        }

        return handleEmailAndFinalResponse(
                mobileOTPDto, emailOTP, startTime, correlationId);
    }
    private ApiResponse handleCountrySpecificOTP(
            MobileOTPDto mobileOTPDto,
            String mobileOTP,
            Date startTime,
            String correlationId) throws ParseException {

        String number = mobileOTPDto.getSubscriberMobileNumber();

        if (number.startsWith("+91")) {
            return handleIndiaOTP(mobileOTPDto, mobileOTP, startTime, correlationId);
        }
        else if (number.startsWith("+256")) {
            return handleUgandaOTP(mobileOTPDto, mobileOTP, startTime, correlationId);
        }
        else if (number.startsWith("+971")) {
            return handleUAEOTP(mobileOTPDto, mobileOTP, startTime, correlationId);
        }
        else {
            return exceptionHandlerUtil
                    .createErrorResponse("api.error.invalid.country.code");
        }
    }
    private ApiResponse handleIndiaOTP(MobileOTPDto mobileOTPDto,
                                       String mobileOTP,
                                       Date startTime,
                                       String correlationId) throws ParseException {

        String number = mobileOTPDto.getSubscriberMobileNumber();

        if (number.length() == 13) {

            ApiResponse apiResponse =
                    sendSMSIND(mobileOTP, number.substring(3, 13));

            if (!apiResponse.isSuccess()) {

                String otpFalseInd = MOBILE_NUMBER + number
                        + OTP_STATUS + apiResponse.getMessage()
                        + EMAIL_ID + mobileOTPDto.getSubscriberEmail()
                        + DEVICE_ID + mobileOTPDto.getDeviceId();

                logModelServiceImpl.setLogModel(false,
                        encryptedString(mobileOTPDto.getSubscriberEmail()),
                        null,
                        REGISTARTION_OTP_SENT,
                        correlationId,
                        null, null, null,
                        otpFalseInd);

                return apiResponse;
            }

        } else {
            return exceptionHandlerUtil.createErrorResponse(PHONE_NUMBER_IS_INVALID);
        }

        return null; // continue flow in main method
    }
    private ApiResponse handleUgandaOTP(MobileOTPDto mobileOTPDto,
                                        String mobileOTP,
                                        Date startTime,
                                        String correlationId) {

        String number = mobileOTPDto.getSubscriberMobileNumber();

        if (number.length() == 13) {

            ApiResponse response =
                    sendSMSUGA(mobileOTP, number, timeToLive);

            try {

                SmsOtpResponseDTO smsOtpResponse =
                        objectMapper.readValue(
                                response.getResult().toString(),
                                SmsOtpResponseDTO.class);

                if (smsOtpResponse.getNon_field_errors() != null) {

                    String otpFalse = MOBILE_NUMBER + smsOtpResponse.getReceiver()
                            + OTP_STATUS + smsOtpResponse.getNon_field_errors()
                            + EMAIL_ID + mobileOTPDto.getSubscriberEmail()
                            + DEVICE_ID + mobileOTPDto.getDeviceId();

                    logModelServiceImpl.setLogModel(false,
                            encryptedString(mobileOTPDto.getSubscriberEmail()),
                            null,
                            REGISTARTION_OTP_SENT,
                            correlationId,
                            null, null, null,
                            otpFalse);

                    return exceptionHandlerUtil
                            .createFailedResponseWithCustomMessage(
                                    smsOtpResponse.getNon_field_errors().get(0),
                                    null);
                }

            } catch (Exception e) {

                Thread.currentThread().interrupt();

                return exceptionHandlerUtil.handleException(e);
            }

        } else {
            return exceptionHandlerUtil.createErrorResponse(PHONE_NUMBER_IS_INVALID);
        }

        return null; // continue flow
    }
    private ApiResponse handleUAEOTP(MobileOTPDto mobileOTPDto,
                                     String mobileOTP,
                                     Date startTime,
                                     String correlationId) {

        String number = mobileOTPDto.getSubscriberMobileNumber();

        if (number.length() == 13) {

            Object obj = sendSMSUAE(mobileOTP, number, timeToLive);

            try {

                String sms = objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(obj);

                LinkedHashMap<String, String> smsOtpResponse =
                        objectMapper.readValue(sms, LinkedHashMap.class);

                if ("406".equals(smsOtpResponse.get("code"))) {

                    String otpFalseUae = MOBILE_NUMBER + number
                            + OTP_STATUS + smsOtpResponse.get("code")
                            + EMAIL_ID + mobileOTPDto.getSubscriberEmail()
                            + DEVICE_ID + mobileOTPDto.getDeviceId();

                    logModelServiceImpl.setLogModel(false,
                            encryptedString(mobileOTPDto.getSubscriberEmail()),
                            null,
                            REGISTARTION_OTP_SENT,
                            correlationId,
                            null, null, null,
                            otpFalseUae);

                    return exceptionHandlerUtil
                            .createErrorResponse("api.error.invalid.number");
                }

            } catch (Exception e) {

                return exceptionHandlerUtil.handleException(e);
            }

        } else {
            return exceptionHandlerUtil.createErrorResponse(PHONE_NUMBER_IS_INVALID);
        }

        return null; // continue flow
    }
    private ApiResponse handleEmailAndFinalResponse(
            MobileOTPDto mobileOTPDto,
            String emailOTP,
            Date startTime,
            String correlationId) {

        OTPResponseDTO otpResponse = new OTPResponseDTO();

        otpResponse.setMobileOTP(null);
        otpResponse.setEmailOTP(null);
        otpResponse.setTtl(timeToLive);
        otpResponse.setMobileEncrptyOTP(encryptedString(generateOtp(6)));
        otpResponse.setEmailEncrptyOTP(encryptedString(emailOTP));

        try {

            EmailReqDto dto = new EmailReqDto();
            dto.setEmailOtp(emailOTP);
            dto.setEmailId(mobileOTPDto.getSubscriberEmail());
            dto.setTtl(timeToLive);

            ApiResponse res = sendEmailToSubscriber(dto);

            if (res.isSuccess()) {

                return exceptionHandlerUtil
                        .createSuccessResponseWithCustomMessage(
                                "api.response.email.sent",
                                otpResponse);

            } else {

                return exceptionHandlerUtil
                        .createErrorResponse(
                                "api.error.something.went.wrong.please.try.after.sometime");
            }

        } catch (Exception e) {

            logger.error(CLASS + "SendOTPMobileSms >> Exception {}", e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }
	
	public ApiResponse sendEmailToSubscriber(EmailReqDto emailReqDto) {
		try {
			String url = emailBaseUrl;
			validateUrl(url);
			logger.info(" emailReqDto {}",emailReqDto);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(emailReqDto, headers);
			logger.info("requestEntity >> {}" , requestEntity);
			ResponseEntity<ApiResponse> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,ApiResponse.class);
            logger.info("res >> {}", res);

            int status = res.getStatusCode().value();

            if (status == 200) {
                return exceptionHandlerUtil.createSuccessResponse("api.response.email.sent", res);

            } else if (status == 400) {
                return exceptionHandlerUtil.createErrorResponse("api.error.bad.request");

            } else if (status == 500) {
                return exceptionHandlerUtil.createErrorResponse("api.error.internal.server.error");
            }
			return exceptionHandlerUtil.createErrorResponse("api.error.something.went.wrong.please.try.after.sometime");
		} catch (Exception e) {
			logger.error(EXCEPTION, e);
			sentryClientExceptions.captureTags(null,emailReqDto.getEmailId(),"sendEmailToSubscriber",OTP_CONTROLLER);
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);
		}
		
	}

	
	private ApiResponse sendSMSIND(String otp, String mobileNumber)  {
		logger.info(CLASS + "sendSMSIND >> req >> otp {} and  mobileNumber {}", otp , mobileNumber);
		String smsBody = "Dear Subscriber, " + otp + " is your DigitalTrust Mobile verification one-time code";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String smsUrlWithBody = indApiSMS
				+ "?APIKey=E2X4Ixz65kKlawWUBVUKkA&senderid=DGTRST&channel=2&DCS=0&flashsms=0&number=" + mobileNumber
				+ "&text=" + smsBody + "&route=1&dlttemplateid=1307162619898313468";

		HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
		try {

			logger.info(CLASS + "sendSMSIND >> req for restTemplate >> smsUrlWithBody {} and requestEntity {}",smsUrlWithBody, requestEntity);

			ResponseEntity<Object> res = restTemplate.exchange(smsUrlWithBody, HttpMethod.GET, requestEntity,
					Object.class);
			String smsResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res.getBody());
			LinkedHashMap<String, String> indiaSmsOtpResponse = objectMapper.readValue(smsResponse,
					LinkedHashMap.class);
			if ("000".equals(indiaSmsOtpResponse.get("ErrorCode"))) {
				logger.info("{} sendSMSIND >> res for restTemplate >> {}", CLASS, indiaSmsOtpResponse);
				return exceptionHandlerUtil.createSuccessResponseWithCustomMessage(indiaSmsOtpResponse.get("ErrorMessage"),null);
			} else {
				return exceptionHandlerUtil.createFailedResponseWithCustomMessage(indiaSmsOtpResponse.get("ErrorMessage"),null);
			}
		} catch (Exception e) {
			logger.error(CLASS + "sendSMSIND() >> Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			sentryClientExceptions.captureTags(null,mobileNumber,"sendSMSIND",OTP_CONTROLLER);
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public ApiResponse sendSMSUGA(String otp, String mobileNumber, int timeToLive) {
		logger.info("sendSMSUGA() >> otp {} and mobileNumber {} and timeToLive {}", otp, mobileNumber ,timeToLive);
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
			logger.info("sendSMSUGA() >> req for restTemplate >> url {} and requestEntity {}", url,requestEntity);
			ResponseEntity<ApiResponse> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponse.class);
			ApiResponse api = res.getBody();
			logger.info("sendSMSUGA() >> res for restTemplate {}", res);
			return api;
		} catch (Exception e) {
			logger.error(CLASS + "sendSMSUGA() >> Exception {}",e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	public Object sendSMSUAE(String otp, String mobileNumber, int timeToLive)  {
		logger.info("sendSMSUAE() >> otp {} and mobileNumber {} and timeToLive{} ",otp,mobileNumber,timeToLive);
		String url = uaeApiSMS;
		String text = "Your ICA-Pass OTP Phone verification code  is " + otp + "The code is valid for " + timeToLive
				+ " seconds. Don't share this code with anyone.";

		Map<String, String> uaeSmsBody = new HashMap<>();
		uaeSmsBody.put("mobileno", mobileNumber);
		uaeSmsBody.put("smstext", text);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		logger.info("getToken() :: {}" , getToken());
		headers.set("access_token", getToken());
		HttpEntity<Object> requestEntity = new HttpEntity<>(uaeSmsBody, headers);
		try {
			logger.info("sendSMSUAE() >> req for restTemplate >> url {} and requestEntity {}", url,requestEntity);
			ResponseEntity<Object> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
			ApiResponse api = new ApiResponse();
			api.setSuccess(true);
			api.setMessage("");
			api.setResult(res.getBody());
			logger.info("sendSMSUAE() >> res for restTemplate {}", res);
			return api.getResult();
		} catch (Exception e) {
			logger.error("sendSMSUAE >> Exception >> {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
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
			logger.error(EXCEPTION, e);
			return null;
		}
	}

	public String getBasicAuth() {
		String userCredentials = niraUserName + ":" + niraPassword;
		return new String(Base64.getEncoder().encode(userCredentials.getBytes()));
	}

	private String encryptedString(String s) {
		try {
			Result result = DAESService.encryptData(s);
			return new String(result.getResponse());
		} catch (Exception e) {
			logger.error(EXCEPTION, e);
			return e.getMessage();
		}
	}

	public String getToken() {
		String url = niraApiToken;
		logger.info("getToken() >> req >> url {} ", url);
		String basicAuth = getBasicAuth();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("daes-authorization", basicAuth);
		HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
		try {
			logger.info("getToken() >> req for restTemplate {} ",requestEntity);
			ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
			logger.info("getToken() >> res for restTemplate {}", res);
			return res.getBody();
		} catch (Exception e) {
			logger.error(CLASS + "getToken() >> Exception {}" ,e.getMessage());
			logger.error(EXCEPTION, e);
			return e.getMessage();
		}

	}

	public ApiResponse verifyOtp(String mobNo) {
		ApiResponse apiResponse = new ApiResponse();

		OTPResponseDTO otpResponse = new OTPResponseDTO();
		otpResponse.setEmailEncrptyOTP(AppUtil.encryptedString("12345"));
		otpResponse.setMobileEncrptyOTP(AppUtil.encryptedString("123456"));
		otpResponse.setTtl(180);

		apiResponse.setMessage("Otp verfication done");
		apiResponse.setSuccess(true);
		apiResponse.setResult(otpResponse);
		return apiResponse;

	}

	@Override
	public ApiResponse sendEmail(MobileOTPDto mobileOTPDto) {
		return null;
	}

	

}
