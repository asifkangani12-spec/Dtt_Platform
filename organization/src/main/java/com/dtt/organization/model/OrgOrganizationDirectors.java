package com.dtt.organization.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "organization_directors")
public class OrgOrganizationDirectors implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_directors_id", nullable = false, unique = true)
	private int organizationDirectorsId;

	
	@Column(name="organization_uid")
	private String organizationUid;
	
	@Column(name="directors_emails")
	private String directorsEmailList;

	public int getOrganizationDirectorsId() {
		return organizationDirectorsId;
	}

	public void setOrganizationDirectorsId(int organizationDirectorsId) {
		this.organizationDirectorsId = organizationDirectorsId;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

	public String getDirectorsEmailList() {
		return directorsEmailList;
	}

	public void setDirectorsEmailList(String directorsEmailList) {
		this.directorsEmailList = directorsEmailList;
	}

	@Override
	public String toString() {
		return "OrganizationDirectors [organizationDirectorsId=" + organizationDirectorsId + ", organizationUid="
				+ organizationUid + ", directorsEmailList=" + directorsEmailList + "]";
	}


}
