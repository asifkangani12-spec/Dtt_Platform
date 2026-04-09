package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class OrgUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int orgContactsEmailId;

	private String employeeEmail;

	private boolean signatory ;

	private boolean eSealSignatory;

	private boolean eSealPrepatory;
	
	private boolean template;
	
	private boolean bulksign;
	
	private String designation;
	
	private String signaturePhoto;

	private String ugpassEmail;


	private String passportNumber;


	private String nationalIdNumber;


	private String mobileNumber;


	private boolean ugpassUserLinkApproved;


	private String subscriberUid;


	private String status;

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public boolean isSignatory() {
		return signatory;
	}

	public void setSignatory(boolean signatory) {
		this.signatory = signatory;
	}

	public boolean iseSealSignatory() {
		return eSealSignatory;
	}

	public void seteSealSignatory(boolean eSealSignatory) {
		this.eSealSignatory = eSealSignatory;
	}

	public boolean iseSealPrepatory() {
		return eSealPrepatory;
	}

	public void seteSealPrepatory(boolean eSealPrepatory) {
		this.eSealPrepatory = eSealPrepatory;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getSignaturePhoto() {
		return signaturePhoto;
	}

	public void setSignaturePhoto(String signaturePhoto) {
		this.signaturePhoto = signaturePhoto;
	}

	public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public boolean isBulksign() {
		return bulksign;
	}

	public void setBulksign(boolean bulksign) {
		this.bulksign = bulksign;
	}

	public String getUgpassEmail() {
		return ugpassEmail;
	}

	public void setUgpassEmail(String ugpassEmail) {
		this.ugpassEmail = ugpassEmail;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getNationalIdNumber() {
		return nationalIdNumber;
	}

	public void setNationalIdNumber(String nationalIdNumber) {
		this.nationalIdNumber = nationalIdNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public boolean isUgpassUserLinkApproved() {
		return ugpassUserLinkApproved;
	}

	public void setUgpassUserLinkApproved(boolean ugpassUserLinkApproved) {
		this.ugpassUserLinkApproved = ugpassUserLinkApproved;
	}

	public String getSubscriberUid() {
		return subscriberUid;
	}

	public void setSubscriberUid(String subscriberUid) {
		this.subscriberUid = subscriberUid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOrgContactsEmailId() {
		return orgContactsEmailId;
	}

	public void setOrgContactsEmailId(int orgContactsEmailId) {
		this.orgContactsEmailId = orgContactsEmailId;
	}

	@Override
	public String toString() {
		return "OrgUser{" +
				"email='" + employeeEmail + '\'' +
				", signatory=" + signatory +
				", eSealSignatory=" + eSealSignatory +
				", eSealPrepatory=" + eSealPrepatory +
				", template=" + template +
				", bulksign=" + bulksign +
				", designation='" + designation + '\'' +
				", signaturePhoto='" + signaturePhoto + '\'' +
				", ugpassEmail='" + ugpassEmail + '\'' +
				", passportNumber='" + passportNumber + '\'' +
				", nationalIdNumber='" + nationalIdNumber + '\'' +
				", mobileNumber='" + mobileNumber + '\'' +
				", ugpassUserLinkApproved=" + ugpassUserLinkApproved +
				", subscriberUid='" + subscriberUid + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
