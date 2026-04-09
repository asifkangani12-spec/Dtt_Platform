package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class AppConfigDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int Id;
	private String osVersion;
	private String latestVersion;
	private String minimumVersion;
	private String updateLink;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getMinimumVersion() {
		return minimumVersion;
	}

	public void setMinimumVersion(String minimumVersion) {
		this.minimumVersion = minimumVersion;
	}

	public String getUpdateLink() {
		return updateLink;
	}

	public void setUpdateLink(String updateLink) {
		this.updateLink = updateLink;
	}

	@Override
	public String toString() {
		return "AppConfigDTO [Id=" + Id + ", osVersion=" + osVersion + ", latestVersion=" + latestVersion
				+ ", minimumVersion=" + minimumVersion + ", updateLink=" + updateLink + "]";
	}

}
