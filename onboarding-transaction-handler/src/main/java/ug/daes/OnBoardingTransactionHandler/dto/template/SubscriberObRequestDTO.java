/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Raxit Dubey
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberObRequestDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String suID;
    
    private String onboardingMethod;
    
    private SubscriberObData subscriberData;
    
    private int templateId;
    
    private int consentId;
    
    private String acknowledgement;

    private String subscriberType;
    
    private String onboardingPaymentStatus;
    
    private String niraResponse;
    
	public String getSuID() {
		return suID;
	}

	public void setSuID(String suID) {
		this.suID = suID;
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

	
	
	public String getOnboardingPaymentStatus() {
		return onboardingPaymentStatus;
	}

	public void setOnboardingPaymentStatus(String onboardingPaymentStatus) {
		this.onboardingPaymentStatus = onboardingPaymentStatus;
	}

	public String getNiraResponse() {
		return niraResponse;
	}

	public void setNiraResponse(String niraResponse) {
		this.niraResponse = niraResponse;
	}

	@Override
	public String toString() {
		return "SubscriberObRequestDTO [suID=" + suID + ", onboardingMethod=" + onboardingMethod + ", subscriberData="
				+ subscriberData + ", templateId=" + templateId + ", consentId=" + consentId + ", acknowledgement="
				+ acknowledgement + ", subscriberType=" + subscriberType + ", onboardingPaymentStatus="
				+ onboardingPaymentStatus + ", niraResponse=" + niraResponse + "]";
	}

}