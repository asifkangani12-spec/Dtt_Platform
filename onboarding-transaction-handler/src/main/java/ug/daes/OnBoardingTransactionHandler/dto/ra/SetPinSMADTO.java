/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.ra;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class SetPinSMADTO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String signPin;
	
	private String authPin;
	
	private String suid;
	
	private boolean signHash;

	private String userSelfie;

	private String signType;


	public String getSignPin() {
		return signPin;
	}

	public void setSignPin(String signPin) {
		this.signPin = signPin;
	}

	public String getAuthPin() {
		return authPin;
	}

	public void setAuthPin(String authPin) {
		this.authPin = authPin;
	}

	public String getSuid() {
		return suid;
	}

	public void setSuid(String suid) {
		this.suid = suid;
	}

	public boolean isSignHash() {
		return signHash;
	}

	public void setSignHash(boolean signHash) {
		this.signHash = signHash;
	}

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

	@Override
	public String toString() {
		return "SetPinSMADTO{" +
				"signPin='" + signPin + '\'' +
				", authPin='" + authPin + '\'' +
				", suid='" + suid + '\'' +
				", signHash=" + signHash +
				", userSelfie='" + userSelfie + '\'' +
				", signType='" + signType + '\'' +
				'}';
	}
}
