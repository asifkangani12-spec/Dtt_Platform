/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.ra;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class RevokeCertDTO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int reasonId;
	
	private String serialNumber;
	
	private String subscriberUniqueId;
	
	private String geoLocation;
	
	private String remarks;

	public int getReasonId() {
		return reasonId;
	}

	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}

	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "RevokeCertDTO [reasonId=" + reasonId + ", serialNumber=" + serialNumber + ", subscriberUniqueId="
				+ subscriberUniqueId + ", geoLocation=" + geoLocation + ", remarks=" + remarks + "]";
	}
}
