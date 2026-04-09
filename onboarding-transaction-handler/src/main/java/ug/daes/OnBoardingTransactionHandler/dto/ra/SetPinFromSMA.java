/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.ra;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class SetPinFromSMA implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String subscriberUniqueId;
	
	private String signingPin;
	
	private String authPin;
	
	private boolean signHash;

	private String userSelfie;

	private String signType;

	public String getUserSelfie() {
		return userSelfie;
	}

	public void setUserSelfie(String userSelfie) {
		this.userSelfie = userSelfie;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}

	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}

	public String getSigningPin() {
		return signingPin;
	}

	public void setSigningPin(String signingPin) {
		this.signingPin = signingPin;
	}

	public boolean isSignHash() {
		return signHash;
	}

	public void setSignHash(boolean signHash) {
		this.signHash = signHash;
	}

	
	public String getAuthPin() {
		return authPin;
	}

	public void setAuthPin(String authPin) {
		this.authPin = authPin;
	}

	@Override
	public String toString() {
		return "SetPinFromSMA{" +
				"subscriberUniqueId='" + subscriberUniqueId + '\'' +
				", signingPin='" + signingPin + '\'' +
				", authPin='" + authPin + '\'' +
				", signHash=" + signHash +
				", userSelfie='" + userSelfie + '\'' +
				", signType='" + signType + '\'' +
				'}';
	}
}
