package ug.daes.OnBoardingTransactionHandler.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequestDTO {

	private String category;

	private String currency;

	private String walletId;

	private String externalId;

	private String payer;

	private double amount;

	private String payerNote;

	private String payeeNote;

	private String payee;

	private String subscriberUniqueId;
	
	private Object paymentInfo;
	
	private String paymentCategory;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPayerNote() {
		return payerNote;
	}

	public void setPayerNote(String payerNote) {
		this.payerNote = payerNote;
	}

	public String getPayeeNote() {
		return payeeNote;
	}

	public void setPayeeNote(String payeeNote) {
		this.payeeNote = payeeNote;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}

	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}


	public Object getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(Object paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	
	
	public String getPaymentCategory() {
		return paymentCategory;
	}

	public void setPaymentCategory(String paymentCategory) {
		this.paymentCategory = paymentCategory;
	}

	@Override
	public String toString() {
		return "PaymentRequestDTO [category=" + category + ", currency=" + currency + ", walletId=" + walletId
				+ ", externalId=" + externalId + ", payer=" + payer + ", amount=" + amount + ", payerNote=" + payerNote
				+ ", payeeNote=" + payeeNote + ", payee=" + payee + ", subscriberUniqueId=" + subscriberUniqueId
				+ ", paymentInfo=" + paymentInfo + ", paymentCategory=" + paymentCategory + "]";
	}

}
