package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class ServiceFeeReqDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int serviceId;
    private String stakeHolder;
    private int priceSlabId;
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public String getStakeHolder() {
		return stakeHolder;
	}
	public void setStakeHolder(String stakeHolder) {
		this.stakeHolder = stakeHolder;
	}
	public int getPriceSlabId() {
		return priceSlabId;
	}
	public void setPriceSlabId(int priceSlabId) {
		this.priceSlabId = priceSlabId;
	}
	@Override
	public String toString() {
		return "ServiceFeeReqDto [serviceId=" + serviceId + ", stakeHolder=" + stakeHolder + ", priceSlabId="
				+ priceSlabId + "]";
	}
}
