package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class PricingSlabDefinitions implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
    private int serviceId;
    private int discount;
    private int volumeRangeFrom;
    private int volumeRangeTo;
    private String stakeHolder;
    private int ratePerQty;
    private int tax;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public int getVolumeRangeFrom() {
		return volumeRangeFrom;
	}
	public void setVolumeRangeFrom(int volumeRangeFrom) {
		this.volumeRangeFrom = volumeRangeFrom;
	}
	public int getVolumeRangeTo() {
		return volumeRangeTo;
	}
	public void setVolumeRangeTo(int volumeRangeTo) {
		this.volumeRangeTo = volumeRangeTo;
	}
	public String getStakeHolder() {
		return stakeHolder;
	}
	public void setStakeHolder(String stakeHolder) {
		this.stakeHolder = stakeHolder;
	}
	public int getRatePerQty() {
		return ratePerQty;
	}
	public void setRatePerQty(int ratePerQty) {
		this.ratePerQty = ratePerQty;
	}
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
	}
	@Override
	public String toString() {
		return "PricingSlabDefinitions [id=" + id + ", serviceId=" + serviceId + ", discount=" + discount
				+ ", volumeRangeFrom=" + volumeRangeFrom + ", volumeRangeTo=" + volumeRangeTo + ", stakeHolder="
				+ stakeHolder + ", ratePerQty=" + ratePerQty + ", tax=" + tax + "]";
	}
    
}
