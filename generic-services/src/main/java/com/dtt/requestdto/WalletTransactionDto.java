package com.dtt.requestdto;

import java.io.Serializable;

public class WalletTransactionDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String passportNumber;
	private String type;
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPassportNumber() {
		return passportNumber;
	}
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "WalletTransactionDto [passportNumber=" + passportNumber + ", type=" + type + "]";
	}
	
}

