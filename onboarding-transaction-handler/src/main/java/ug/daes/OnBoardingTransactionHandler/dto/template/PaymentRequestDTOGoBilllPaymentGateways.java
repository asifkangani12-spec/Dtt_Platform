package ug.daes.OnBoardingTransactionHandler.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequestDTOGoBilllPaymentGateways {
	private String subscriberUniqueId;
	private String email;
	private Object paymentInfo;
	private String paymentCategory;
	private double amount;
	private String currency;
	private String name;
	private String mobileNumber; // msisdns // Only include for MOBILE_MONEY
	private String merchantReference; // merchant_reference Transaction id
	private String transactionMethod; // transaction_method = CARD or MOBILE_MONEY
	private String providerCode; // provider_code = card_ug or mtn_momo_ug
	private String description; // Test Collection
	private Boolean chargeCustomer;// charge_customer = false always
	private Boolean requireConfirmation; // require_confirmation = false always
	private String redirectUrl;
	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}
	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getMerchantReference() {
		return merchantReference;
	}
	public void setMerchantReference(String merchantReference) {
		this.merchantReference = merchantReference;
	}
	public String getTransactionMethod() {
		return transactionMethod;
	}
	public void setTransactionMethod(String transactionMethod) {
		this.transactionMethod = transactionMethod;
	}
	public String getProviderCode() {
		return providerCode;
	}
	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getChargeCustomer() {
		return chargeCustomer;
	}
	public void setChargeCustomer(Boolean chargeCustomer) {
		this.chargeCustomer = chargeCustomer;
	}
	public Boolean getRequireConfirmation() {
		return requireConfirmation;
	}
	public void setRequireConfirmation(Boolean requireConfirmation) {
		this.requireConfirmation = requireConfirmation;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	@Override
	public String toString() {
		return "PaymentRequestDTO [subscriberUniqueId=" + subscriberUniqueId + ", email=" + email + ", paymentInfo="
				+ paymentInfo + ", paymentCategory=" + paymentCategory + ", amount=" + amount + ", currency=" + currency
				+ ", name=" + name + ", mobileNumber=" + mobileNumber + ", merchantReference=" + merchantReference
				+ ", transactionMethod=" + transactionMethod + ", providerCode=" + providerCode + ", description="
				+ description + ", chargeCustomer=" + chargeCustomer + ", requireConfirmation=" + requireConfirmation
				+ ", redirectUrl=" + redirectUrl + "]";
	}
}
