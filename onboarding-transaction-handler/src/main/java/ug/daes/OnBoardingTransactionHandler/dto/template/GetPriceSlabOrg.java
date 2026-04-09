package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class GetPriceSlabOrg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int serviceId;
	private String orgId;

	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	@Override
	public String toString() {
		return "GetPriceSlabOrg [serviceId=" + serviceId + ", orgId=" + orgId + "]";
	}
	
}
