package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class AppConfigReqDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String osVersion;
	private String appVersion;

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Override
	public String toString() {
		return "AppConfigReqDTO [osVersion=" + osVersion + ", appVersion=" + appVersion + "]";
	}

}
