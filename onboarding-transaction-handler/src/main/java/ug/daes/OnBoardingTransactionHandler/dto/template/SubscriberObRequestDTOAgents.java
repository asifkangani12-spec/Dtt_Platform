package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;
import java.util.List;


public class SubscriberObRequestDTOAgents implements Serializable {
	private static final long serialVersionUID = 1L;

	private String suID;

	private String deviceId;
	private String subscriberMobileNumber;
	private String subscriberEmail;
	private String fcmToken;
	private Boolean otpStatus;
	private String image ;
	private String osName;
	private String osVersion;
	private String appVersion;
	private String deviceInfo;
    
    private String onboardingMethod;
    
    private SubscriberObData subscriberData;
    
    private int templateId;
    
    private int consentId;
    
    private String acknowledgement;
    
    private String subscriberType;
    
    private String onboardingApprovalStatus;
    
    private String certStatus;
    
    private String onboardingPaymentStatus;
    
    private String levelOfAssurance;
    
    private List<SubscriberDocuments> subscriberDocuments;

	private String agentSuid;

	public String getSuID() {
		return suID;
	}

	public void setSuID(String suID) {
		this.suID = suID;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSubscriberMobileNumber() {
		return subscriberMobileNumber;
	}

	public void setSubscriberMobileNumber(String subscriberMobileNumber) {
		this.subscriberMobileNumber = subscriberMobileNumber;
	}

	public String getSubscriberEmail() {
		return subscriberEmail;
	}

	public void setSubscriberEmail(String subscriberEmail) {
		this.subscriberEmail = subscriberEmail;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public Boolean getOtpStatus() {
		return otpStatus;
	}

	public void setOtpStatus(Boolean otpStatus) {
		this.otpStatus = otpStatus;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getOnboardingMethod() {
		return onboardingMethod;
	}

	public void setOnboardingMethod(String onboardingMethod) {
		this.onboardingMethod = onboardingMethod;
	}

	public SubscriberObData getSubscriberData() {
		return subscriberData;
	}

	public void setSubscriberData(SubscriberObData subscriberData) {
		this.subscriberData = subscriberData;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getConsentId() {
		return consentId;
	}

	public void setConsentId(int consentId) {
		this.consentId = consentId;
	}

	public String getAcknowledgement() {
		return acknowledgement;
	}

	public void setAcknowledgement(String acknowledgement) {
		this.acknowledgement = acknowledgement;
	}

	public String getSubscriberType() {
		return subscriberType;
	}

	public void setSubscriberType(String subscriberType) {
		this.subscriberType = subscriberType;
	}

	public String getOnboardingApprovalStatus() {
		return onboardingApprovalStatus;
	}

	public void setOnboardingApprovalStatus(String onboardingApprovalStatus) {
		this.onboardingApprovalStatus = onboardingApprovalStatus;
	}

	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}

	public String getOnboardingPaymentStatus() {
		return onboardingPaymentStatus;
	}

	public void setOnboardingPaymentStatus(String onboardingPaymentStatus) {
		this.onboardingPaymentStatus = onboardingPaymentStatus;
	}

	public String getLevelOfAssurance() {
		return levelOfAssurance;
	}

	public void setLevelOfAssurance(String levelOfAssurance) {
		this.levelOfAssurance = levelOfAssurance;
	}

	public List<SubscriberDocuments> getSubscriberDocuments() {
		return subscriberDocuments;
	}

	public void setSubscriberDocuments(List<SubscriberDocuments> subscriberDocuments) {
		this.subscriberDocuments = subscriberDocuments;
	}

	public String getAgentSuid() {
		return agentSuid;
	}

	public void setAgentSuid(String agentSuid) {
		this.agentSuid = agentSuid;
	}

	@Override
	public String toString() {
		return "SubscriberObRequestDTOAgents [suID=" + suID + ", deviceId=" + deviceId + ", subscriberMobileNumber="
				+ subscriberMobileNumber + ", subscriberEmail=" + subscriberEmail + ", fcmToken=" + fcmToken
				+ ", otpStatus=" + otpStatus + ", image=" + image + ", osName=" + osName + ", osVersion=" + osVersion
				+ ", appVersion=" + appVersion + ", deviceInfo=" + deviceInfo + ", onboardingMethod=" + onboardingMethod
				+ ", subscriberData=" + subscriberData + ", templateId=" + templateId + ", consentId=" + consentId
				+ ", acknowledgement=" + acknowledgement + ", subscriberType=" + subscriberType
				+ ", onboardingApprovalStatus=" + onboardingApprovalStatus + ", certStatus=" + certStatus
				+ ", onboardingPaymentStatus=" + onboardingPaymentStatus + ", levelOfAssurance=" + levelOfAssurance
				+ ", subscriberDocuments=" + subscriberDocuments + ", agentSuid=" + agentSuid + "]";
	}
	
}
