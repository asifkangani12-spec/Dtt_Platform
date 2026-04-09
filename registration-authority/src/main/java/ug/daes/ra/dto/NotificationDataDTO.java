/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.dto;

import java.io.Serializable;

import ug.daes.ra.enums.CertificateStatus;

/**
 * The Class NotificationDataDTO.
 */
public class NotificationDataDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The body. */
	private String body;

	/** The title. */
	private String title;

	/** The notification context. */
	private NotificationContextDTO notificationContext;
	private static final String TITLE_KEY = "\"title\":\"";
	/**
	 * Instantiates a new notification data DTO.
	 */
	public NotificationDataDTO() {
		/*
		 * Default constructor is required for:
		 * - JSON deserialization (Jackson)
		 * - Serialization frameworks
		 * - Proxy creation
		 * This constructor intentionally remains empty.
		 */
	}
	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 *
	 * @param body
	 *            the new body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the notification context.
	 *
	 * @return the notification context
	 */
	public NotificationContextDTO getNotificationContext() {
		return notificationContext;
	}

	/**
	 * Sets the notification context.
	 *
	 * @param notificationContext
	 *            the new notification context
	 */
	public void setNotificationContext(NotificationContextDTO notificationContext) {
		this.notificationContext = notificationContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NotificationDataDTO{" + "body='" + body + '\'' + ", title='" + title + '\'' + ", notificationContext="
				+ notificationContext + '}';
	}

	/**
	 * Gets the issue cert notification data.
	 *
	 * @return the issue cert notification data
	 */
	public String getIssueCertNotificationData() {
		return "{" + TITLE_KEY + title + "\"," + "\"body\":\"" + body + "\"," + "\"notificationContext\":" + "{"
				+ "\"pREF_CERTIFICATE_STATUS\":\"" + CertificateStatus.ACTIVE + "\"" + "}" + "}";
	}

	/**
	 * Gets the revoke cert notification data.
	 *
	 * @return the revoke cert notification data
	 */
	public String getRevokeCertNotificationData() {
		return "{" + TITLE_KEY + title + "\"," + "\"body\":\"" + body + "\"," + "\"notificationContext\":" + "{"
				+ "\"pREF_CERTIFICATE_STATUS\":\"" + CertificateStatus.REVOKED + "\"" + "}" + "}";
	}

	/**
	 * Gets the expired cert notification data.
	 *
	 * @return the expired cert notification data
	 */
	public String getExpiredCertNotificationData() {
		return "{" + TITLE_KEY + title + "\"," + "\"body\":\"" + body + "\"," + "\"notificationContext\":" + "{"
				+ "\"pREF_CERTIFICATE_STATUS\":\"" + CertificateStatus.EXPIRED + "\"" + "}" + "}";
	}
	
	
	public String getFailedCertNotificationData() {
		return "{" + TITLE_KEY + title + "\"," + "\"body\":\"" + body + "\"," + "\"notificationContext\":" + "{"
				+ "\"pREF_CERTIFICATE_STATUS\":\"" + CertificateStatus.FAILED + "\"" + "}" + "}";
	}
}
