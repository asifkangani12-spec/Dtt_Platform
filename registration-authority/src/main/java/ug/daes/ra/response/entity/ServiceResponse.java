/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.response.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ServiceResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceResponse {

	/** The status. */
	private String status;

	/** The signature. */
	private String certificate;

	/** The wrapped key. */
	private String wrappedKey;

	/** The certificate serial number. */
	@JsonProperty("certificate_serial_number")
	private String certificateSerialNumber;

	/** The error code. */
	@JsonProperty("error_code")
	private String errorCode;

	/** The error message. */
	@JsonProperty("error_message")
	private String errorMessage;

	/** The certificate status. */
	@JsonProperty("certificate_status")
	private String certificateStatus;

	/** The revocation reason. */
	@JsonProperty("revocation_reason")
	private String revocationReason;

	/** The issue date. */
	private String issueDate;

	/** The expiry date. */
	private String expiryDate;

	/** The signature. */
	private String signature;

	private boolean faceMatchResult;

	private double faceMatchScore;

	private String faceData;

	public double getFaceMatchScore() {
		return faceMatchScore;
	}

	public void setFaceMatchScore(double faceMatchScore) {
		this.faceMatchScore = faceMatchScore;
	}

	public boolean isFaceMatchResult() {
		return faceMatchResult;
	}

	public void setFaceMatchResult(boolean faceMatchResult) {
		this.faceMatchResult = faceMatchResult;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the certificate.
	 *
	 * @return the certificate
	 */
	public String getCertificate() {
		return certificate;
	}

	/**
	 * Sets the certificate.
	 *
	 * @param certificate
	 *            the new certificate
	 */
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	/**
	 * Gets the certificate serial number.
	 *
	 * @return the certificate serial number
	 */
	public String getCertificateSerialNumber() {
		return certificateSerialNumber;
	}

	public void setCertificateSerialNumber(String certificateSerialNumber) {
		this.certificateSerialNumber = certificateSerialNumber;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCertificateStatus() {
		return certificateStatus;
	}

	public void setCertificateStatus(String certificateStatus) {
		this.certificateStatus = certificateStatus;
	}

	public String getRevocationReason() {
		return revocationReason;
	}

	public void setRevocationReason(String revocationReason) {
		this.revocationReason = revocationReason;
	}

	/**
	 * Gets the issue date.
	 *
	 * @return the issue date
	 */
	public String getIssueDate() {
		return issueDate;
	}

	/**
	 * Sets the issue date.
	 *
	 * @param issueDate
	 *            the new issue date
	 */
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	/**
	 * Gets the expiry date.
	 *
	 * @return the expiry date
	 */
	public String getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Sets the expiry date.
	 *
	 * @param expiryDate
	 *            the new expiry date
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Gets the signature.
	 *
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Sets the signature.
	 *
	 * @param signature
	 *            the new signature
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Gets the wrapped key.
	 *
	 * @return the wrapped key
	 */
	public String getWrappedKey() {
		return wrappedKey;
	}

	/**
	 * Sets the wrapped key.
	 *
	 * @param wrappedKey
	 *            the new wrapped key
	 */
	public void setWrappedKey(String wrappedKey) {
		this.wrappedKey = wrappedKey;
	}

	public String getFaceData() {
		return faceData;
	}

	public void setFaceData(String faceData) {
		this.faceData = faceData;
	}


	@Override
	public String toString() {
		return "ServiceResponse{" +
				"status='" + status + '\'' +
				", certificate='" + certificate + '\'' +
				", wrappedKey='" + wrappedKey + '\'' +
				", certificateSerialNumber='" + certificateSerialNumber + '\'' +
				", errorCode='" + errorCode + '\'' +
				", errorMessage='" + errorMessage + '\'' +
				", certificateStatus='" + certificateStatus + '\'' +
				", revocationReason='" + revocationReason + '\'' +
				", issueDate='" + issueDate + '\'' +
				", expiryDate='" + expiryDate + '\'' +
				", signature='" + signature + '\'' +
				", faceMatchResult=" + faceMatchResult +
				", faceMatchScore=" + faceMatchScore +
				", faceData='" + faceData + '\'' +
				'}';
	}
}
