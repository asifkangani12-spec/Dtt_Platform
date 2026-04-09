/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class IntegrationURLs implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int integrationUrlId;
	
	private String urlName;

	private String url;
	
	private int templateId;
	
	private String creationDate;

	public int getIntegrationUrlId() {
		return integrationUrlId;
	}

	public void setIntegrationUrlId(int integrationUrlId) {
		this.integrationUrlId = integrationUrlId;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "IntegrationURLs [integrationUrlId=" + integrationUrlId + ", urlName=" + urlName + ", url=" + url
				+ ", templateId=" + templateId + ", creationDate=" + creationDate + "]";
	}	
}
