package ug.daes.OnBoardingTransactionHandler.constant;

import java.util.HashMap;
import java.util.Map;

import ug.daes.OnBoardingTransactionHandler.dto.MethodNature;

public class MethodConstant {
    private MethodConstant() {
        throw new IllegalStateException("Utility class");
    }

    private static final String CLASS_NAME_MOSIP = "MosipService";
    private static final String CLASS_NAME_ORGANIZATION = "OrganizationService";
    private static final String CLASS_NAME_PRICEMODEL = "PriceModelService";
    private static final String CLASS_NAME_ASSISTEDONBOARDING = "AssistedOnboardingService";
    private static final String CLASS_NAME_APPCONFIG = "AppConfigService";
    private static final String NATURE_NAME = "Synchronous";
    private static final String CLASS_NAME_CONSENT_SERVICE = "ConsentService";
    private static final String CLASS_NAME_PROPOSED_FLOW = "ProposedOnboardingService";
    private static final String CLASS_NAME_OTP = "OTPService";
    private static final String CLASS_NAME_RA_SERVICE = "RaService";
    private static final String CLASS_NAME_PAYMENT_SERVICE = "PaymentService";
    private static final String CLASS_NAME_TEMPLATE = "TemplatesService";
    private static final String CLASS_NAME_UPDATE_SUBSCRIBRER = "UpdateSubscriberService";
    private static final String  GET_SUBSCRIBER_PROFILES="getSubscriberProfiles";
    private static final Map<String, MethodNature> map = new HashMap<>();

    public static Map<String, MethodNature> getMap() {
        return map;
    }

    static {

        //----------------------------------------Templates-------------------------------------------------------
        map.put("addTemplate", new MethodNature("addTemplate", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("isTemplateExist", new MethodNature("isTemplateExist", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("addTemplateApproval", new MethodNature("addTemplateApproval", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("getActiveTemplate", new MethodNature("getActiveTemplate", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("getTemplates", new MethodNature("getTemplate", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 0, "", false, "All"));
        map.put("getTemplateByID", new MethodNature("getTemplateByID", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("deleteTemplateByID", new MethodNature("deleteTemplateByID", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("publishTemplateByID", new MethodNature("publishTemplateByID", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("unPublishTemplateByID", new MethodNature("unPublishTemplateByID", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 1, "", false, "All"));
        map.put("getSteps", new MethodNature("getSteps", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 0, "", false, "All"));
        map.put("getMethods", new MethodNature("getMethod", NATURE_NAME, "", CLASS_NAME_TEMPLATE, 0, "", false, "All"));

        //----------------------------------------Consent-------------------------------------------------------
        map.put("addConsent", new MethodNature("addConsent", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
        map.put("getConsentList", new MethodNature("getConsentList", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 0, "", false, "All"));
        map.put("getConsentById", new MethodNature("getConsentById", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
        map.put("getActiveConsent", new MethodNature("getActiveConsent", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
        map.put("updateConsentActive", new MethodNature("updateConsentActive", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
        map.put("updateConsentInActive", new MethodNature("updateConsentInActive", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
        map.put("saveNiraApiLog", new MethodNature("saveNiraApiLog", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
        map.put("signConsentData", new MethodNature("signConsentData", NATURE_NAME, "", CLASS_NAME_CONSENT_SERVICE, 1, "", false, "All"));
//        -------------------------------------------OTP-----------------------------------------------

        map.put("deviceRegistration", new MethodNature("deviceRegistration", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));

        map.put("sendMobileOTP", new MethodNature("sendMobileOTP", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("saveSubscriberDetails", new MethodNature("saveSubscriberDetails", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("verifyNewDevice", new MethodNature("verifyNewDevice", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("activateNewDevice", new MethodNature("activateNewDevice", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("getSubscriberImage", new MethodNature("getSubscriberImage", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("saveSubscriberOnboarding", new MethodNature("saveSubscriberOnboarding", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("getSubscriberOnboarding", new MethodNature("getSubscriberOnboarding", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("resetPin", new MethodNature("resetPin", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("getLivelinessVideo", new MethodNature("getLivelinessVideo", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));

        map.put("saveSubscriberReOnboarding", new MethodNature("saveSubscriberReOnboarding", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));

        map.put("getDeviceStatus", new MethodNature("getDeviceStatus", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("updateSubscriberFcmToken", new MethodNature("updateSubscriberFcmToken", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));

        map.put(GET_SUBSCRIBER_PROFILES, new MethodNature(GET_SUBSCRIBER_PROFILES, NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));
        map.put("getVerificationChannelResponse", new MethodNature("getVerificationChannelResponse", NATURE_NAME, "", CLASS_NAME_OTP, 1, "", false, "All"));

//        ------------------------- ---------------------RaService------------------------------------------------
        map.put("getCertificateDetailsBySubscriberUniqueId", new MethodNature("getCertificateDetailsBySubscriberUniqueId", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
        map.put("revokeCertificate", new MethodNature("revokeCertificate", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
        map.put("setPin", new MethodNature("setPin", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
        map.put("setPinFromSMA", new MethodNature("setPinFromSMA", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
        map.put("cancelSetPinFromSMA", new MethodNature("cancelSetPinFromSMA", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
        map.put("verifyCertPin", new MethodNature("verifyCertPin", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
        map.put("genrateFailedCertificate", new MethodNature("genrateFailedCertificate", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));

        map.put("setPins", new MethodNature("setPins", NATURE_NAME, "", CLASS_NAME_RA_SERVICE, 1, "", false, "All"));
//        ---------------------------------------------SendNotification----------------------------------------------
        map.put("setSendNotification", new MethodNature("setSendNotification", NATURE_NAME, "", "SendNotification", 1, "", false, "All"));
        map.put("deleteRecordBySuid", new MethodNature("deleteRecordBySuid", NATURE_NAME, "", "DeleteService", 1, "", false, "All"));


        //------------------------updateApp-------------------------------

        map.put("checkUpdate", new MethodNature("checkUpdate", NATURE_NAME, "", CLASS_NAME_APPCONFIG, 1, "", false, "All"));
        map.put("addAppConfig", new MethodNature("addAppConfig", NATURE_NAME, "", CLASS_NAME_APPCONFIG, 1, "", false, "All"));
        map.put("getAppConfigList", new MethodNature("getAppConfigList", NATURE_NAME, "", CLASS_NAME_APPCONFIG, 0, "", false, "All"));

        map.put("appConfigurationList", new MethodNature("appConfigurationList", NATURE_NAME, "", CLASS_NAME_APPCONFIG, 0, "", false, "All"));
        map.put("appConfigurationListForAssistedOnboarding", new MethodNature("appConfigurationListForAssistedOnboarding", NATURE_NAME, "", CLASS_NAME_APPCONFIG, 0, "", false, "All"));

        //-------------------paymentMethods--------------------

        map.put("payment", new MethodNature("payment", NATURE_NAME, "", CLASS_NAME_PAYMENT_SERVICE, 1, "", false, "All"));
        map.put("paymentSlabPrice", new MethodNature("paymentSlabPrice", NATURE_NAME, "", CLASS_NAME_PAYMENT_SERVICE, 1, "", false, "All"));
        map.put("activateUserSubscriptionBySponsorId", new MethodNature("activateUserSubscriptionBySponsorId", NATURE_NAME, "", CLASS_NAME_PAYMENT_SERVICE, 1, "", false, "All"));
        map.put("getAggregatorFee", new MethodNature("getAggregatorFee", NATURE_NAME, "", CLASS_NAME_PAYMENT_SERVICE, 0, "", false, "All"));
        //new changes------------updateSubscriber---------------
        map.put("updateSubscriberDetails", new MethodNature("updateSubscriberDetails", NATURE_NAME, "", CLASS_NAME_UPDATE_SUBSCRIBRER, 1, "", false, "All"));
        map.put("getOtp", new MethodNature("getOtp", NATURE_NAME, "", CLASS_NAME_UPDATE_SUBSCRIBRER, 1, "", false, "All"));
        map.put("linkEmail", new MethodNature("linkEmail", NATURE_NAME, "", CLASS_NAME_UPDATE_SUBSCRIBRER, 1, "", false, "All"));
        map.put("sendEmailLinkOtp", new MethodNature("sendEmailLinkOtp", NATURE_NAME, "", CLASS_NAME_UPDATE_SUBSCRIBRER, 1, "", false, "All"));
        map.put("getOrgList", new MethodNature("getOrgList", NATURE_NAME, "", CLASS_NAME_UPDATE_SUBSCRIBRER, 1, "", false, "All"));


        //--------------------organization------------------
        map.put("getCertificateDetails", new MethodNature("getCertificateDetails", NATURE_NAME, "", CLASS_NAME_ORGANIZATION, 1, "", false, "All"));
        map.put("getSponsorListBySuidForUserSubscription", new MethodNature("getSponsorListBySuidForUserSubscription", NATURE_NAME, "", CLASS_NAME_ORGANIZATION, 1, "", false, "All"));
        map.put("linkSponsorByBeneficiarySuid", new MethodNature("linkSponsorByBeneficiarySuid", NATURE_NAME, "", CLASS_NAME_ORGANIZATION, 1, "", false, "All"));
        map.put("getSponsorsBySuid", new MethodNature("getSponsorsBySuid", NATURE_NAME, "", CLASS_NAME_ORGANIZATION, 1, "", false, "All"));
        map.put("getAllCertificateDetails", new MethodNature("getAllCertificateDetails", NATURE_NAME, "", CLASS_NAME_ORGANIZATION, 1, "", false, "All"));

        //----------pricemodel methods---------------------


        map.put("getRemCredits", new MethodNature("getRemCredits", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getServices", new MethodNature("getServices", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getPriceSlabByServiceAndStakeHolder", new MethodNature("getPriceSlabByServiceAndStakeHolder", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getPriceSlabOrg", new MethodNature("getPriceSlabOrg", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getPayHistory", new MethodNature("getPayHistory", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getSubscriptionFee", new MethodNature("getSubscriptionFee", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getOrganizationRemainingCredits", new MethodNature("getOrganizationRemainingCredits", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));
        map.put("getServiceFee", new MethodNature("getServiceFee", NATURE_NAME, "", CLASS_NAME_PRICEMODEL, 1, "", false, "All"));


        // Temporary Table

        map.put("saveDataInTemporaryTable", new MethodNature("saveDataInTemporaryTable", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));

        map.put("checkMobileAndEmail", new MethodNature("checkMobileAndEmail", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));

        map.put("submitOnboardingData", new MethodNature("submitOnboardingData", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));
        map.put("updateRecordInTemporaryTable", new MethodNature("updateRecordInTemporaryTable", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));
        map.put("deleteRecordInTemporaryTable", new MethodNature("deleteRecordInTemporaryTable", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));

        //-------------------------assistedOnboarding----------------------------------------
        map.put("checkAgent", new MethodNature("checkAgent", NATURE_NAME, "", CLASS_NAME_ASSISTEDONBOARDING, 1, "", false, "All"));
        map.put("assistedOnboardSubscriber", new MethodNature("assistedOnboardSubscriber", NATURE_NAME, "", CLASS_NAME_ASSISTEDONBOARDING, 1, "", false, "All"));
        map.put("getOnboardPrivilegedSubscriber", new MethodNature("getOnboardPrivilegedSubscriber", NATURE_NAME, "", CLASS_NAME_ASSISTEDONBOARDING, 1, "", false, "All"));
        map.put("getPrivilegedSubscriberList", new MethodNature("getPrivilegedSubscriberList", NATURE_NAME, "", CLASS_NAME_ASSISTEDONBOARDING, 1, "", false, "All"));
        map.put("generateFailedCertificateByAgent", new MethodNature("generateFailedCertificateByAgent", NATURE_NAME, "", CLASS_NAME_ASSISTEDONBOARDING, 1, "", false, "All"));

        //-----------------------Agents Notification----------------------------------------------------------------------
        map.put("setAgentsSendNotification", new MethodNature("setAgentsSendNotification", NATURE_NAME, "", "SendNotification", 1, "", false, "All"));


        //---------------------Simulated Border Control------------------------------------------------------------
        map.put("getDetailsFromSimulatedBorder", new MethodNature("getDetailsFromSimulatedBorder", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));


        map.put("getMosipEmailOtp", new MethodNature("getMosipEmailOtp", NATURE_NAME, "", CLASS_NAME_MOSIP, 1, "", false, "All"));

        map.put("getProfileByOTP", new MethodNature("getProfileByOTP", NATURE_NAME, "", CLASS_NAME_MOSIP, 1, "", false, "All"));

        map.put("getProfileByFingerPrint", new MethodNature("getProfileByFingerPrint", NATURE_NAME, "", CLASS_NAME_MOSIP, 1, "", false, "All"));

        map.put("getDetailsFromVc", new MethodNature("getDetailsFromVc", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));
        map.put("verificationService", new MethodNature("verificationService", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));


        map.put("getPreferredTitles", new MethodNature("getPreferredTitles", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 0, "", false, "All"));

        map.put("addUpdateTitle", new MethodNature("addUpdateTitle", NATURE_NAME, "", CLASS_NAME_PROPOSED_FLOW, 1, "", false, "All"));


        //-----------------------------preferences----------------------------------//
        map.put("getPreferenceBySuid",new MethodNature("getPreferenceBySuid",NATURE_NAME,"",CLASS_NAME_APPCONFIG,1,"",false,"All"));
        map.put("updateLanguagePreference", new MethodNature("updateLanguagePreference", NATURE_NAME, "", CLASS_NAME_APPCONFIG, 1, "", false, "All"));



    }


}
