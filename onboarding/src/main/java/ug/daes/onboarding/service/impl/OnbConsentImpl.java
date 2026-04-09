package ug.daes.onboarding.service.impl;

import java.util.Base64;
import java.util.List;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ug.daes.onboarding.dto.SignedDataDto;
import ug.daes.onboarding.exceptions.OnboardingExceptionHandlerUtil;
import ug.daes.onboarding.model.OnbConsentHistory;
import ug.daes.onboarding.model.OnbSubscriber;
import ug.daes.onboarding.model.OnbSubscriberConsents;
import ug.daes.onboarding.repository.ConsentHistoryRepo;
import ug.daes.onboarding.repository.SubscriberConsentsRepo;
import ug.daes.onboarding.repository.SubscriberRepoIface;
import ug.daes.onboarding.service.iface.ConsentIface;

@Service
public class OnbConsentImpl implements ConsentIface {

    private static final Logger logger = LoggerFactory.getLogger(OnbConsentImpl.class);
    private static final String API_ERROR_GENERIC = "api.error.generic";

    private final RestTemplate restTemplate;
    private final SubscriberRepoIface subscriberRepoIface;
    private final ConsentHistoryRepo consentHistoryRepo;
    private final SubscriberConsentsRepo subscriberConsentsRepo;

    private final OnboardingExceptionHandlerUtil exceptionHandlerUtil;

    public OnbConsentImpl(
            RestTemplate restTemplate,
            SubscriberRepoIface subscriberRepoIface,
            ConsentHistoryRepo consentHistoryRepo,
            SubscriberConsentsRepo subscriberConsentsRepo,

            OnboardingExceptionHandlerUtil exceptionHandlerUtil) {


        this.restTemplate = restTemplate;
        this.subscriberRepoIface = subscriberRepoIface;
        this.consentHistoryRepo = consentHistoryRepo;
        this.subscriberConsentsRepo = subscriberConsentsRepo;

        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    @Value("${signed.required.by.user}")
    private boolean signRequired;
    @Value("${signed.data.url}")
    String signedURL;

    public ApiResponse signData(HttpHeaders httpHeaders) {
        try {

            String subscriberMail = httpHeaders.getFirst("adminugpassemail");
            logger.info(subscriberMail);
            String consentData = "I agreed to above Terms and conditions and Data privacy terms";

            if (signRequired) {
                return handleSignRequired(subscriberMail, consentData);
            } else {
                return handleSignNotRequired(subscriberMail, consentData);
            }

        } catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
                 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
            logger.error("Unexpected exception", ex);
            return exceptionHandlerUtil.createErrorResponse("api.error.database");

        } catch (Exception e) {
            logger.error("Unexpected exception occurred ", e);
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_GENERIC);
        }
    }

    private ResponseEntity<ApiResponse> callSigningAPI(String subscriberMail, String consentData) {

        String url = signedURL;

        SignedDataDto signedDataDto = new SignedDataDto();

        String base64 = Base64.getEncoder().encodeToString(consentData.getBytes());

        signedDataDto.setDocumentType("CADES");
        signedDataDto.setSubscriberUniqueId(subscriberMail);
        signedDataDto.setDocData(base64);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> reqEntity1 = new HttpEntity<>(signedDataDto, headers);

        return restTemplate.exchange(url, HttpMethod.POST, reqEntity1, ApiResponse.class);
    }

    private ApiResponse handleSignNotRequired(String subscriberMail, String consentData) {

        if (subscriberMail == null) {
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.subscriber.not.found.with.given.email");
        }

        OnbSubscriber subscriber = subscriberRepoIface.findByemailId(subscriberMail);

        List<OnbConsentHistory> latestConsentList = consentHistoryRepo.findLatestConsent();
        OnbConsentHistory consentHistory =
                latestConsentList.isEmpty() ? null : latestConsentList.get(0);
        if (consentHistory == null) {
            logger.error("Consent history is null");
            return exceptionHandlerUtil.createErrorResponse("api.error.consent.history.null");
        }
        OnbSubscriberConsents consents =
                subscriberConsentsRepo.findSubscriberConsentBySuidAndConsentId(
                        subscriber.getSubscriberUid(), consentHistory.getId());

        if (consents != null) {
            return exceptionHandlerUtil.successResponse("api.response.consent.saved");
        }

        OnbSubscriberConsents subscriberConsents = new OnbSubscriberConsents();
        subscriberConsents.setConsentData(consentData);
        subscriberConsents.setConsentId(consentHistory.getId());
        subscriberConsents.setSuid(subscriber.getSubscriberUid());
        subscriberConsents.setCreatedOn(AppUtil.getDate());

        subscriberConsentsRepo.save(subscriberConsents);
        return null;
    }

    private ApiResponse handleSignRequired(String subscriberMail, String consentData) {

        if (subscriberMail == null) {
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.email.cant.should.be.null.or.empty");
        }

        ResponseEntity<ApiResponse> res1 =
                callSigningAPI(subscriberMail, consentData);

        ApiResponse body = res1.getBody();

        if (body == null || !body.isSuccess()) {
            String message = (body != null)
                    ? body.getMessage()
                    : "Response body is null";

            logger.info(message);

            return exceptionHandlerUtil.createErrorResponseWithResult(
                    API_ERROR_GENERIC, message);
        }

        if (body.getResult() == null) {
            logger.error("Invalid response from signing API");
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_GENERIC);
        }

        OnbSubscriber subscriber =
                subscriberRepoIface.findByemailId(subscriberMail);

        if (subscriber == null) {
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.subscriber.not.found.with.given.email");
        }

        List<OnbConsentHistory> latestConsentList =
                consentHistoryRepo.findLatestConsent();

        if (latestConsentList.isEmpty()) {
            logger.error("Consent history is null");
            return exceptionHandlerUtil.createErrorResponse(
                    "api.error.consent.history.null");
        }

        OnbConsentHistory consentHistory = latestConsentList.get(0);

        OnbSubscriberConsents subscriberConsents = new OnbSubscriberConsents();
        subscriberConsents.setConsentData(consentData);
        subscriberConsents.setSignedConsentData(body.getResult().toString());
        subscriberConsents.setConsentId(consentHistory.getId());
        subscriberConsents.setSuid(subscriber.getSubscriberUid());
        subscriberConsents.setCreatedOn(AppUtil.getDate());

        subscriberConsentsRepo.save(subscriberConsents);

        return exceptionHandlerUtil.successResponse(
                "api.response.consent.signed");
    }
}
