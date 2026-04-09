/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class OnboardingSteps implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int onboardingStepId;
	
	private String onboardingStep;
	
	private String onboardingStepDisplayName;
	
	private String integrationUrl;
	
	private String onboardingStepThreshold;
	
	private String andriodTFliteThreshold;
	
	private String andriodDTTThreshold;
	
	private String iosTFliteThreshold;
	
	private String iosDTTThreshold;
	
	

	public int getOnboardingStepId() {
		return onboardingStepId;
	}

	public void setOnboardingStepId(int onboardingStepId) {
		this.onboardingStepId = onboardingStepId;
	}

	public String getOnboardingStep() {
		return onboardingStep;
	}

	public void setOnboardingStep(String onboardingStep) {
		this.onboardingStep = onboardingStep;
	}

	public String getOnboardingStepDisplayName() {
		return onboardingStepDisplayName;
	}

	public void setOnboardingStepDisplayName(String onboardingStepDisplayName) {
		this.onboardingStepDisplayName = onboardingStepDisplayName;
	}

	public String getIntegrationUrl() {
		return integrationUrl;
	}

	public void setIntegrationUrl(String integrationUrl) {
		this.integrationUrl = integrationUrl;
	}

	public String getOnboardingStepThreshold() {
		return onboardingStepThreshold;
	}

	public void setOnboardingStepThreshold(String onboardingStepThreshold) {
		this.onboardingStepThreshold = onboardingStepThreshold;
	}
	
	
	public String getAndriodTFliteThreshold() {
		return andriodTFliteThreshold;
	}

	public void setAndriodTFliteThreshold(String andriodTFliteThreshold) {
		this.andriodTFliteThreshold = andriodTFliteThreshold;
	}

	public String getAndriodDTTThreshold() {
		return andriodDTTThreshold;
	}

	public void setAndriodDTTThreshold(String andriodDTTThreshold) {
		this.andriodDTTThreshold = andriodDTTThreshold;
	}

	public String getIosTFliteThreshold() {
		return iosTFliteThreshold;
	}

	public void setIosTFliteThreshold(String iosTFliteThreshold) {
		this.iosTFliteThreshold = iosTFliteThreshold;
	}

	public String getIosDTTThreshold() {
		return iosDTTThreshold;
	}

	public void setIosDTTThreshold(String iosDTTThreshold) {
		this.iosDTTThreshold = iosDTTThreshold;
	}

	@Override
	public String toString() {
		return "OnboardingSteps [onboardingStepId=" + onboardingStepId + ", onboardingStep=" + onboardingStep
				+ ", onboardingStepDisplayName=" + onboardingStepDisplayName + ", integrationUrl=" + integrationUrl
				+ ", onboardingStepThreshold=" + onboardingStepThreshold + ", andriodTFliteThreshold="
				+ andriodTFliteThreshold + ", andriodDTTThreshold=" + andriodDTTThreshold + ", iosTFliteThreshold="
				+ iosTFliteThreshold + ", iosDTTThreshold=" + iosDTTThreshold + "]";
	}

	
}
