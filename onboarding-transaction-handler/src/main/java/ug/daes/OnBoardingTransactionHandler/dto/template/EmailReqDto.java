package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class EmailReqDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String emailOtp;
	
	private int ttl;
	
	private String emailId;
	

	public String getEmailOtp() {
		return emailOtp;
	}

	public void setEmailOtp(String emailOtp) {
		this.emailOtp = emailOtp;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}


	@Override
	public String toString() {
		return "EmailReqDto [emailOtp=" + emailOtp + ", ttl=" + ttl + ", emailId=" + emailId + "]";
	}

}
