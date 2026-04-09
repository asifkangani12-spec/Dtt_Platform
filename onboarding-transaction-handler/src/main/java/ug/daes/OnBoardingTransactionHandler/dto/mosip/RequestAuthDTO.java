package ug.daes.OnBoardingTransactionHandler.dto.mosip;

import java.util.List;

/**
 * The Class RequestAuthDTO.
 * @author Sachin mali
 */
public class RequestAuthDTO {

	/** variable to hold otp value */
	private String otp;

	/** variable to hold timestamp value */
	private String timestamp;

	/** variable to hold identity value */
	private IdentityDTO demographics;

	/** List of biometric identity info */
	private List<BioIdentityInfoDTO> biometrics;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public IdentityDTO getDemographics() {
		return demographics;
	}

	public void setDemographics(IdentityDTO demographics) {
		this.demographics = demographics;
	}

	public List<BioIdentityInfoDTO> getBiometrics() {
		return biometrics;
	}

	public void setBiometrics(List<BioIdentityInfoDTO> biometrics) {
		this.biometrics = biometrics;
	}

	@Override
	public String toString() {
		return "RequestAuthDTO [otp=" + otp + ", timestamp=" + timestamp + ", demographics=" + demographics
				+ ", biometrics=" + biometrics + "]";
	}

}
