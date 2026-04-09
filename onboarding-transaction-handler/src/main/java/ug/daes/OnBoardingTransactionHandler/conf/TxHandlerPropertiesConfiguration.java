/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Raxit Dubey
 *
 */

@Component
@ConfigurationProperties(prefix = "url")
public class TxHandlerPropertiesConfiguration {

	private String onboarding;

	private String signing;

	private String raauthority;

	private String appConfig;

	private String payment;

	private String notification;

	private String organization;

	private String pricemodel;

	private String assistedOnboarding;

	private String agentsNotification;
	
	private String SimulatedBorderControl;

	private String VerificationChannel;

	private String MosipBaseUrl;
	
	private String verificationService;

	private String idpTokenValidation;

	private String languageHeader;

	public String getPricemodel() {
		return pricemodel;
	}

	public void setPricemodel(String pricemodel) {
		this.pricemodel = pricemodel;
	}

	public String getOnboarding() {
		return onboarding;
	}

	public void setOnboarding(String onboarding) {
		this.onboarding = onboarding;
	}

	public String getSigning() {
		return signing;
	}

	public void setSigning(String signing) {
		this.signing = signing;
	}

	public String getRaauthority() {
		return raauthority;
	}

	public void setRaauthority(String raauthority) {
		this.raauthority = raauthority;
	}

	public String getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(String appConfig) {
		this.appConfig = appConfig;
	}

	public String getAssistedOnboarding() {
		return assistedOnboarding;
	}

	public void setAssistedOnboarding(String assistedOnboarding) {
		this.assistedOnboarding = assistedOnboarding;
	}

	public String getAgentsNotification() {
		return agentsNotification;
	}

	public void setAgentsNotification(String agentsNotification) {
		this.agentsNotification = agentsNotification;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getSimulatedBorderControl() {
		return SimulatedBorderControl;
	}

	public void setSimulatedBorderControl(String simulatedBorderControl) {
		SimulatedBorderControl = simulatedBorderControl;
	}

	public String getVerificationChannel() {
		return VerificationChannel;
	}

	public void setVerificationChannel(String verificationChannel) {
		VerificationChannel = verificationChannel;
	}

	public String getMosipBaseUrl() {
		return MosipBaseUrl;
	}

	public void setMosipBaseUrl(String mosipBaseUrl) {
		MosipBaseUrl = mosipBaseUrl;
	}

	public String getVerificationService() {
		return verificationService;
	}

	public void setVerificationService(String verificationService) {
		this.verificationService = verificationService;
	}

	public String getIdpTokenValidation() {
		return idpTokenValidation;
	}

	public void setIdpTokenValidation(String idpTokenValidation) {
		this.idpTokenValidation = idpTokenValidation;
	}

	public String getLanguageHeader() {
		return languageHeader;
	}

	public void setLanguageHeader(String languageHeader) {
		this.languageHeader = languageHeader;
	}
}
