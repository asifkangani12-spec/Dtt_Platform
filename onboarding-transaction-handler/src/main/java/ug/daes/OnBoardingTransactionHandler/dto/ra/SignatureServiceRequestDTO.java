package ug.daes.OnBoardingTransactionHandler.dto.ra;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class SignatureServiceRequestDTO implements Serializable{

	private String geoLocation;
	// Yahan tak common rahega

	/** The cert type. */
	private int certType;

	/** The reset PIN. */
	private boolean resetPIN;

	/** The change pin. */
	private boolean changePin;

	/** The signing password. */
	private String signingPassword;

	/** The old signing password. */
	private String oldSigningPassword;

	/** The subscriber unique id. */
	private String subscriberUniqueId;

	/** The other key ID. */
	// From internal
	private String otherKeyID;

	/** The key id. */
	private String keyId;

	/**
	 * Gets the geo location.
	 *
	 * @return the geo location
	 */
	public String getGeoLocation() {
		return geoLocation;
	}

	/**
	 * Sets the geo location.
	 *
	 * @param geoLocation
	 *            the new geo location
	 */
	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	/**
	 * Gets the cert type.
	 *
	 * @return the cert type
	 */
	public int getCertType() {
		return certType;
	}

	/**
	 * Sets the cert type.
	 *
	 * @param certType
	 *            the new cert type
	 */
	public void setCertType(int certType) {
		this.certType = certType;
	}

	/**
	 * Checks if is reset PIN.
	 *
	 * @return true, if is reset PIN
	 */
	public boolean isResetPIN() {
		return resetPIN;
	}

	/**
	 * Sets the reset PIN.
	 *
	 * @param resetPIN
	 *            the new reset PIN
	 */
	public void setResetPIN(boolean resetPIN) {
		this.resetPIN = resetPIN;
	}

	/**
	 * Gets the signing password.
	 *
	 * @return the signing password
	 */
	public String getSigningPassword() {
		return signingPassword;
	}

	/**
	 * Sets the signing password.
	 *
	 * @param signingPassword
	 *            the new signing password
	 */
	public void setSigningPassword(String signingPassword) {
		this.signingPassword = signingPassword;
	}

	/**
	 * Gets the subscriber unique id.
	 *
	 * @return the subscriber unique id
	 */
	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}

	/**
	 * Sets the subscriber unique id.
	 *
	 * @param subscriberUniqueId
	 *            the new subscriber unique id
	 */
	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}

	/**
	 * Gets the other key ID.
	 *
	 * @return the other key ID
	 */
	public String getOtherKeyID() {
		return otherKeyID;
	}

	/**
	 * Sets the other key ID.
	 *
	 * @param otherKeyID
	 *            the new other key ID
	 */
	public void setOtherKeyID(String otherKeyID) {
		this.otherKeyID = otherKeyID;
	}

	/**
	 * Gets the key id.
	 *
	 * @return the key id
	 */
	public String getKeyId() {
		return keyId;
	}

	/**
	 * Sets the key id.
	 *
	 * @param keyId
	 *            the new key id
	 */
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	/**
	 * Checks if is change pin.
	 *
	 * @return true, if is change pin
	 */
	public boolean isChangePin() {
		return changePin;
	}

	/**
	 * Sets the change pin.
	 *
	 * @param changePin
	 *            the new change pin
	 */
	public void setChangePin(boolean changePin) {
		this.changePin = changePin;
	}

	/**
	 * Gets the old signing password.
	 *
	 * @return the old signing password
	 */
	public String getOldSigningPassword() {
		return oldSigningPassword;
	}

	/**
	 * Sets the old signing password.
	 *
	 * @param oldSigningPassword
	 *            the new old signing password
	 */
	public void setOldSigningPassword(String oldSigningPassword) {
		this.oldSigningPassword = oldSigningPassword;
	}

	/**
	 * Gets the setting pin.
	 *
	 * @return the setting pin
	 */
	public String getSettingPin() {
		return "{" + "\"subscriberDigitalID\"" + ":" + "\"" + subscriberUniqueId + "\"," + "\"keyID\"" + ":" + "\""
				+ keyId + "\"," + "\"otherKeyID\"" + ":" + "\"" + otherKeyID + "\"," + "\"resetPIN\"" + ":" + resetPIN
				+ "," + "\"signingPassword\"" + ":" + "\"" + signingPassword + "\"" + "}";
	}

}
