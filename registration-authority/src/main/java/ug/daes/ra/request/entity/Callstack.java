/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.request.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Callstack {


	private String subscriberDigitalID;

	private String username;


	private String password;


	private String keyID;


	private int certificateID;


	@JsonProperty("serial_number")
	private String serialNumber;


	private String reason;


	private String certificate;


	private String commonName;


	private String countryName;


	@JsonProperty("token_sign")
	private String tokenSign;

	private String hash;


	public String getSubscriberDigitalID() {
		return subscriberDigitalID;
	}


	public void setSubscriberDigitalID(String subscriberDigitalID) {
		this.subscriberDigitalID = subscriberDigitalID;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getKeyID() {
		return keyID;
	}


	public void setKeyID(String keyID) {
		this.keyID = keyID;
	}


	public int getCertificateID() {
		return certificateID;
	}


	public void setCertificateID(int certificateID) {
		this.certificateID = certificateID;
	}


	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getTokenSign() {
		return tokenSign;
	}

	public void setTokenSign(String tokenSign) {
		this.tokenSign = tokenSign;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
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
	 * Gets the common name.
	 *
	 * @return the common name
	 */
	public String getCommonName() {
		return commonName;
	}

	/**
	 * Sets the common name.
	 *
	 * @param commonName
	 *            the new common name
	 */
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	/**
	 * Gets the country name.
	 *
	 * @return the country name
	 */
	public String getCountryName() {
		return countryName;
	}

	/**
	 * Sets the country name.
	 *
	 * @param countryName
	 *            the new country name
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}


	public String getHash() {
		return hash;
	}

	/**
	 * Sets the hash.
	 *
	 * @param hash
	 *            the new hash
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */


	/**
	 * Gets the issue cert callstack.
	 *
	 * @return the issue cert callstack
	 */
	public String getIssueCertCallstack1() {
		return "{" + "\"subscriberDigitalID\"" + ":" + "\"" + subscriberDigitalID + "\"," + "\"keyID\"" + ":" + "\""
				+ keyID + "\"," + "\"commonName\"" + ":" + "\"" + commonName + "\"," + "\"countryName\"" + ":" + "\""
				+ countryName + "\"" + "}";
	}

	/**
	 * Gets the revoke cert callstack.
	 *
	 * @return the revoke cert callstack
	 */
	public String getRevokeCertCallstack() {
		return "{" + "\"serial_number\"" + ":" + "\"" + serialNumber + "\"," + "\"reason\"" + ":" + "\"" + reason
				+ "\"" + "}";
	}
	/**
	 * Gets the certificate status call stack.
	 *
	 * @return the certificate status call stack
	 */
	public String getCertificateStatusCallStack() {
		return "{" + "\"serial_number\"" + ":" + "\"" + serialNumber + "\"," + "\"certificate\"" + ":" + "\""
				+ certificate + "\"" + "}";
	}
}
