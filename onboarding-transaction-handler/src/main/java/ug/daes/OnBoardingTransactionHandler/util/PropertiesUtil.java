/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ug.daes.OnBoardingTransactionHandler.conf.TxHandlerPropertiesConfiguration;

/**
 * @author Raxit Dubey
 *
 */
@Component
public class PropertiesUtil implements CommandLineRunner {

	private String baseUrl;

	private String raBaseUrl;

	private String signBaseUrl;

	private String appConfigUrl;

	private String paymentBaseUrl;

	private String notificationBaseUrl;

	private String organizationBaseUrl;

	private String pricemodelBaseUrl;

	private String assistedOnboardingUrl;

	private String agentsNotificationBaseUrl;
	
	private String simulatedBorderControlBaseUrl;
	
	private String verificationChannelBaseUrl;
	
	private String verificationService;
	private String mosipBaseUrl;

	private String idpTokenValidation;

	private String languageHeader;

	private final TxHandlerPropertiesConfiguration propertiesConfiguration;

	public PropertiesUtil(TxHandlerPropertiesConfiguration propertiesConfiguration) {
		this.propertiesConfiguration = propertiesConfiguration;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getRaBaseUrl() {
		return raBaseUrl;
	}

	public void setRaBaseUrl(String raBaseUrl) {
		this.raBaseUrl = raBaseUrl;
	}

	public String getSignBaseUrl() {
		return signBaseUrl;
	}

	public void setSignBaseUrl(String signBaseUrl) {
		this.signBaseUrl = signBaseUrl;
	}

	public String getAppConfigUrl() {
		return appConfigUrl;
	}

	public void setAppConfigUrl(String appConfigUrl) {
		this.appConfigUrl = appConfigUrl;
	}

	public String getPaymentBaseUrl() {
		return paymentBaseUrl;
	}

	public void setPaymentBaseUrl(String paymentBaseUrl) {
		this.paymentBaseUrl = paymentBaseUrl;
	}

	public String getNotificationBaseUrl() {
		return notificationBaseUrl;
	}

	public void setNotificationBaseUrl(String notificationBaseUrl) {
		this.notificationBaseUrl = notificationBaseUrl;
	}

	public String getOrganizationBaseUrl() {
		return organizationBaseUrl;
	}

	public void setOrganizationBaseUrl(String organizationBaseUrl) {
		this.organizationBaseUrl = organizationBaseUrl;
	}

	public String getPricemodelBaseUrl() {
		return pricemodelBaseUrl;
	}

	public void setPricemodelBaseUrl(String pricemodelBaseUrl) {
		this.pricemodelBaseUrl = pricemodelBaseUrl;
	}

	public String getAssistedOnboardingUrl() {
		return assistedOnboardingUrl;
	}

	public void setAssistedOnboardingUrl(String assistedOnboardingUrl) {
		this.assistedOnboardingUrl = assistedOnboardingUrl;
	}

	public String getAgentsNotificationBaseUrl() {
		return agentsNotificationBaseUrl;
	}

	public void setAgentsNotificationBaseUrl(String agentsNotificationBaseUrl) {
		this.agentsNotificationBaseUrl = agentsNotificationBaseUrl;
	}

	public String getSimulatedBorderControlBaseUrl() {
		return simulatedBorderControlBaseUrl;
	}

	public void setSimulatedBorderControlBaseUrl(String simulatedBorderControlBaseUrl) {
		this.simulatedBorderControlBaseUrl = simulatedBorderControlBaseUrl;
	}

	public String getVerificationService() {
		return verificationService;
	}

	public void setVerificationService(String verificationService) {
		this.verificationService = verificationService;
	}

	public String getMosipBaseUrl() {
		return mosipBaseUrl;
	}

	public void setMosipBaseUrl(String mosipBaseUrl) {
		this.mosipBaseUrl = mosipBaseUrl;
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

	public String getVerificationChannelBaseUrl() {
		return verificationChannelBaseUrl;
	}

	public void setVerificationChannelBaseUrl(String verificationChannelBaseUrl) {
		this.verificationChannelBaseUrl = verificationChannelBaseUrl;
	}

	@Override
	public void run(String... args) throws Exception {
		baseUrl = propertiesConfiguration.getOnboarding();
		raBaseUrl = propertiesConfiguration.getRaauthority();
		signBaseUrl = propertiesConfiguration.getSigning();
		appConfigUrl = propertiesConfiguration.getAppConfig();
		paymentBaseUrl = propertiesConfiguration.getPayment();
		notificationBaseUrl = propertiesConfiguration.getNotification();
		organizationBaseUrl = propertiesConfiguration.getOrganization();
		pricemodelBaseUrl = propertiesConfiguration.getPricemodel();
		
		assistedOnboardingUrl = propertiesConfiguration.getAssistedOnboarding();
		agentsNotificationBaseUrl = propertiesConfiguration.getAgentsNotification();
		simulatedBorderControlBaseUrl = propertiesConfiguration.getSimulatedBorderControl();
		mosipBaseUrl = propertiesConfiguration.getMosipBaseUrl();
		verificationChannelBaseUrl = propertiesConfiguration.getVerificationChannel();
		verificationService = propertiesConfiguration.getVerificationService();
		idpTokenValidation = propertiesConfiguration.getIdpTokenValidation();
		languageHeader = propertiesConfiguration.getLanguageHeader();

	}
}
