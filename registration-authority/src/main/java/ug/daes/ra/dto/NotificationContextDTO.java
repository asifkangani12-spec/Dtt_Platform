/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import ug.daes.ra.enums.CertificateStatus;
import ug.daes.ra.enums.OnboardingApprovalStatus;

/**
 * The Class NotificationContextDTO.
 */
public class NotificationContextDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The p RE F ONBOARDIN G STATUS. */
	@JsonProperty("pREF_ONBOARDING_STATUS")
	private boolean prefOnboardingStatus;

	/** The p RE F ONBOARDIN G APPROVA L STATUS. */
	@JsonProperty("pREF_ONBOARDING_APPROVAL_STATUS")
	private OnboardingApprovalStatus prefOnboardingApprovalStatus;

	/** The p RE F CERTIFICAT E STATUS. */
	@JsonProperty("pREF_CERTIFICATE_STATUS")
	private CertificateStatus prefCertificateStatus;

	/** The p RE F CERTIFICAT E REVOK E STATUS. */
	@JsonProperty("pREF_CERTIFICATE_REVOKE_STATUS")
	private boolean prefCertificateRevokeStatus;

	/** The p ROMOTIONA L NOTIFICATION. */
	@JsonProperty("pROMOTIONAL_NOTIFICATION")
	private String promotionalNotification;

	/**
	 * Instantiates a new notification context DTO.
	 */
	public NotificationContextDTO() {
		throw new UnsupportedOperationException("Not supported");
	}


	public boolean getPrefOnboardingStatus() {
		return prefOnboardingStatus;
	}

	public OnboardingApprovalStatus getPrefOnboardingApprovalStatus() {
		return prefOnboardingApprovalStatus;
	}

	public void setPrefOnboardingApprovalStatus(OnboardingApprovalStatus prefOnboardingApprovalStatus) {
		this.prefOnboardingApprovalStatus = prefOnboardingApprovalStatus;
	}

	public void setPrefOnboardingStatus(boolean prefOnboardingStatus) {
		this.prefOnboardingStatus = prefOnboardingStatus;
	}

	public CertificateStatus getPrefCertificateStatus() {
		return prefCertificateStatus;
	}

	public void setPrefCertificateStatus(CertificateStatus prefCertificateStatus) {
		this.prefCertificateStatus = prefCertificateStatus;
	}

	public boolean isPrefCertificateRevokeStatus() {
		return prefCertificateRevokeStatus;
	}

	public void setPrefCertificateRevokeStatus(boolean prefCertificateRevokeStatus) {
		this.prefCertificateRevokeStatus = prefCertificateRevokeStatus;
	}

	public String getPromotionalNotification() {
		return promotionalNotification;
	}

	public void setPromotionalNotification(String promotionalNotification) {
		this.promotionalNotification = promotionalNotification;
	}
}
