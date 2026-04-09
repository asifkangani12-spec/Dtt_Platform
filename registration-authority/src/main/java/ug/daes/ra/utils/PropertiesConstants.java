/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021,
 * All rights reserved.
 */
package ug.daes.ra.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * The Class PropertiesConstants.
 */
@Component
public class PropertiesConstants implements CommandLineRunner {

	private final PropertiesConfiguration propertiesConfiguration;

	// Use volatile to ensure visibility across threads
	private static volatile String pkiUrl;
	private static volatile String issueCertificateCallbackUrl;
	private static volatile String notification;
	private static volatile String status;

	public PropertiesConstants(PropertiesConfiguration propertiesConfiguration) {
		this.propertiesConfiguration = propertiesConfiguration;
	}

	// Static Getters
	public static String getPkiUrl() { return pkiUrl; }
	public static String getIssueCertificateCallbackUrl() { return issueCertificateCallbackUrl; }
	public static String getNotification() { return notification; }
	public static String getStatus() { return status; }

	@Override
	public void run(String... args) {
		// Call the static method to perform the assignment
		initializeStaticFields(propertiesConfiguration);
	}

	/**
	 * Making this method static satisfies Sonar Rule S2696.
	 * Making it synchronized ensures thread safety during startup.
	 */
	private static synchronized void initializeStaticFields(PropertiesConfiguration config) {
		status = config.getStatus();
		pkiUrl = config.getPki();
		issueCertificateCallbackUrl = config.getIssuecertificatecallbackurl();
		notification = config.getNotification();
	}
}