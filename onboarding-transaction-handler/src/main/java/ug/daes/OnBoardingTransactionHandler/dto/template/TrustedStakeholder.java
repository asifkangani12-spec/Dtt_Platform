package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;
import java.util.Date;


public class TrustedStakeholder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;

	private String name;
	
	private String spocUgpassEmail;
	
	private String referenceId;
	
	private String organizationUid;
	
	private String referredBy;
	private boolean status;
	
	//@Temporal(TemporalType.TIMESTAMP)
	private Date onboardingTime;
	
	//@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;
	
	private String stakeholderType;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpocUgpassEmail() {
		return spocUgpassEmail;
	}

	public void setSpocUgpassEmail(String spocUgpassEmail) {
		this.spocUgpassEmail = spocUgpassEmail;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}
	

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Date getOnboardingTime() {
		return onboardingTime;
	}

	public void setOnboardingTime(Date onboardingTime) {
		this.onboardingTime = onboardingTime;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getStakeholderType() {
		return stakeholderType;
	}

	public void setStakeholderType(String stakeholderType) {
		this.stakeholderType = stakeholderType;
	}

	@Override
	public String toString() {
		return "TrustedStakeholder [id=" + id + ", name=" + name + ", spocUgpassEmail=" + spocUgpassEmail
				+ ", referenceId=" + referenceId + ", organizationUid=" + organizationUid + ", referredBy=" + referredBy
				+ ", status=" + status + ", onboardingTime=" + onboardingTime + ", creationTime=" + creationTime
				+ ", stakeholderType=" + stakeholderType + "]";
	}

}
