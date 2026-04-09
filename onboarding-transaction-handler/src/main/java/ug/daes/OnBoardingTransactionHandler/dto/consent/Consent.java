/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.consent;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class Consent implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Integer consentId;
	
	
	private String consent;
	
	
	private String createdOn;
	
	
	private String updatedOn;
	
	
	private String consentType;
	
	
	private String status;
	
	private String privacyConsent;

	public Integer getConsentId() {
		return consentId;
	}

	public void setConsentId(Integer consentId) {
		this.consentId = consentId;
	}

	public String getConsent() {
		return consent;
	}

	public void setConsent(String consent) {
		this.consent = consent;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getConsentType() {
		return consentType;
	}

	public void setConsentType(String consentType) {
		this.consentType = consentType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrivacyConsent() {
		return privacyConsent;
	}

	public void setPrivacyConsent(String privacyConsent) {
		this.privacyConsent = privacyConsent;
	}

	@Override
	public String toString() {
		return "Consent [consentId=" + consentId + ", consent=" + consent + ", createdOn=" + createdOn + ", updatedOn="
				+ updatedOn + ", consentType=" + consentType + ", status=" + status + ", privacyConsent="
				+ privacyConsent + "]";
	}
}
