/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2024, 
 * All rights reserved.
 */
package ug.daes.ra.request.entity;

/**
* The Class SetPinModelDto.
*/
public class SetPinModelDto {
	
	/** The geo location. */


	/** The Auth cert type. */

	
	/** The reset PIN. */

	
	/** The sign cert type. */

	/** The signing password. */
	private String signingPassword;
	
	/** The signing password. */
	private String authPassword;

	/** The subscriber unique id. */
	private String subscriberUniqueId;
	
	/** The wrapped key. */

	
	/** The wrapped key. */

	
	public String getSigningPassword() {
		return signingPassword;
	}

	public void setSigningPassword(String signingPassword) {
		this.signingPassword = signingPassword;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}

	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}

	@Override
	public String toString() {
		return "SetPinModelDto [signingPassword=" + signingPassword + ", authPassword=" + authPassword
				+ ", subscriberUniqueId=" + subscriberUniqueId + "]";
	}

	/**
	 * Gets the sign setting pin.
	 *
	 * @return the setting pin
	 */

	
	/**
	 * Gets the setting pin.
	 *
	 * @return the setting pin
	 */

	
	
}
