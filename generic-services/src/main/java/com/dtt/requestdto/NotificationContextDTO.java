package com.dtt.requestdto;

import java.io.Serializable;
import java.util.Map;

import com.dtt.enums.CertificateStatus;
import com.dtt.enums.OnboardingApprovalStatus;

public class NotificationContextDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private boolean prefOnboardingStatus;
	private OnboardingApprovalStatus prefOnboardingApprovalStatus;
	private CertificateStatus prefCertificateStatus;
	private boolean prefCertificateRevokeStatus;
	private String promotionalNotification;
	private boolean signingAuth;
	private boolean isSignHash;
	private boolean isBothPins;
	private boolean isSignPins;

	private Map<String, String> prefDocumentSigned;
	private Map<String, String> prefPaymentStatus;
	private Map<String, String> prefTransactionId;
	private Map<String, String> prefOrgLink;
	private Map<String, String> prefBeneficiaryLink;
	private Map<String, String> prefOdooNotification;
	private Map<String, String> prefVisaAuthority;
	private Map<String, String> prefImmigrationAuthority;

	public boolean isPrefOnboardingStatus() {
		return prefOnboardingStatus;
	}

	public void setPrefOnboardingStatus(boolean prefOnboardingStatus) {
		this.prefOnboardingStatus = prefOnboardingStatus;
	}

	public OnboardingApprovalStatus getPrefOnboardingApprovalStatus() {
		return prefOnboardingApprovalStatus;
	}

	public void setPrefOnboardingApprovalStatus(OnboardingApprovalStatus prefOnboardingApprovalStatus) {
		this.prefOnboardingApprovalStatus = prefOnboardingApprovalStatus;
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

	public boolean isSigningAuth() {
		return signingAuth;
	}

	public void setSigningAuth(boolean signingAuth) {
		this.signingAuth = signingAuth;
	}

	public boolean isSignHash() {
		return isSignHash;
	}

	public void setSignHash(boolean signHash) {
		isSignHash = signHash;
	}

	public boolean isBothPins() {
		return isBothPins;
	}

	public void setBothPins(boolean bothPins) {
		isBothPins = bothPins;
	}

	public boolean isSignPins() {
		return isSignPins;
	}

	public void setSignPins(boolean signPins) {
		isSignPins = signPins;
	}

	public Map<String, String> getPrefDocumentSigned() {
		return prefDocumentSigned;
	}

	public void setPrefDocumentSigned(Map<String, String> prefDocumentSigned) {
		this.prefDocumentSigned = prefDocumentSigned;
	}

	public Map<String, String> getPrefPaymentStatus() {
		return prefPaymentStatus;
	}

	public void setPrefPaymentStatus(Map<String, String> prefPaymentStatus) {
		this.prefPaymentStatus = prefPaymentStatus;
	}

	public Map<String, String> getPrefTransactionId() {
		return prefTransactionId;
	}

	public void setPrefTransactionId(Map<String, String> prefTransactionId) {
		this.prefTransactionId = prefTransactionId;
	}

	public Map<String, String> getPrefOrgLink() {
		return prefOrgLink;
	}

	public void setPrefOrgLink(Map<String, String> prefOrgLink) {
		this.prefOrgLink = prefOrgLink;
	}

	public Map<String, String> getPrefBeneficiaryLink() {
		return prefBeneficiaryLink;
	}

	public void setPrefBeneficiaryLink(Map<String, String> prefBeneficiaryLink) {
		this.prefBeneficiaryLink = prefBeneficiaryLink;
	}

	public Map<String, String> getPrefOdooNotification() {
		return prefOdooNotification;
	}

	public void setPrefOdooNotification(Map<String, String> prefOdooNotification) {
		this.prefOdooNotification = prefOdooNotification;
	}

	public Map<String, String> getPrefVisaAuthority() {
		return prefVisaAuthority;
	}

	public void setPrefVisaAuthority(Map<String, String> prefVisaAuthority) {
		this.prefVisaAuthority = prefVisaAuthority;
	}

	public Map<String, String> getPrefImmigrationAuthority() {
		return prefImmigrationAuthority;
	}

	public void setPrefImmigrationAuthority(Map<String, String> prefImmigrationAuthority) {
		this.prefImmigrationAuthority = prefImmigrationAuthority;
	}

	@Override
	public String toString() {
		return "NotificationContextDTO{" +
				"prefOnboardingStatus=" + prefOnboardingStatus +
				", prefOnboardingApprovalStatus=" + prefOnboardingApprovalStatus +
				", prefCertificateStatus=" + prefCertificateStatus +
				", prefCertificateRevokeStatus=" + prefCertificateRevokeStatus +
				", promotionalNotification='" + promotionalNotification + '\'' +
				", signingAuth=" + signingAuth +
				", isSignHash=" + isSignHash +
				", isBothPins=" + isBothPins +
				", isSignPins=" + isSignPins +
				", prefDocumentSigned=" + prefDocumentSigned +
				", prefPaymentStatus=" + prefPaymentStatus +
				", prefTransactionId=" + prefTransactionId +
				", prefOrgLink=" + prefOrgLink +
				", prefBeneficiaryLink=" + prefBeneficiaryLink +
				", prefOdooNotification=" + prefOdooNotification +
				", prefVisaAuthority=" + prefVisaAuthority +
				", prefImmigrationAuthority=" + prefImmigrationAuthority +
				'}';
	}
}
