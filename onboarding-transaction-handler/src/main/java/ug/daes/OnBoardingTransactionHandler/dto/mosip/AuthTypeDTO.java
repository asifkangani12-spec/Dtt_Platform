package ug.daes.OnBoardingTransactionHandler.dto.mosip;


/**
 * The Class AuthTypeDTO.
 * @author Sachin mali
 */
public class AuthTypeDTO {

	/** For demo Authentication */
	private boolean demo;

	/** For biometric Authentication */
	private boolean bio;

	/** For otp Authentication */
	private boolean otp;

	/** For pin Authentication */
	private boolean pin;

	public boolean isDemo() {
		return demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public boolean isBio() {
		return bio;
	}

	public void setBio(boolean bio) {
		this.bio = bio;
	}

	public boolean isOtp() {
		return otp;
	}

	public void setOtp(boolean otp) {
		this.otp = otp;
	}

	public boolean isPin() {
		return pin;
	}

	public void setPin(boolean pin) {
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "AuthTypeDTO [demo=" + demo + ", bio=" + bio + ", otp=" + otp + ", pin=" + pin + "]";
	}
	
}
