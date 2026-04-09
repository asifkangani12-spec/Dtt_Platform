/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.ra;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class VerifyCertPinDTO implements Serializable {

	private String authPin;
	
	private String signingPin;
	
	private String subscriberUId;

	public String getAuthPin() {
		return authPin;
	}

	public void setAuthPin(String authPin) {
		this.authPin = authPin;
	}

	public String getSigningPin() {
		return signingPin;
	}

	public void setSigningPin(String signingPin) {
		this.signingPin = signingPin;
	}

	public String getSubscriberUId() {
		return subscriberUId;
	}

	public void setSubscriberUId(String subscriberUId) {
		this.subscriberUId = subscriberUId;
	}

	@Override
	public String toString() {
		return "VerifyCertPinDTO [authPin=" + authPin + ", signingPin=" + signingPin + ", subscriberUId="
				+ subscriberUId + "]";
	}

	
}
