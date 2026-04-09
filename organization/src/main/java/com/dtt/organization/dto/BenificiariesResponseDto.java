package com.dtt.organization.dto;
import java.util.List;

import com.dtt.organization.model.OrgBeneficiaryValidity;
import com.dtt.organization.model.OrgBenificiaries;

public class BenificiariesResponseDto  {

	/**
	 *
	 */


	OrgBenificiaries benificiaries;

	List<OrgBeneficiaryValidity> beneficiaryValidity;



	public void setBenificiaries(OrgBenificiaries benificiaries) {
		this.benificiaries = benificiaries;
	}

	public List<OrgBeneficiaryValidity> getBeneficiaryValidity() {
		return beneficiaryValidity;
	}

	public void setBeneficiaryValidity(List<OrgBeneficiaryValidity> beneficiaryValidity) {
		this.beneficiaryValidity = beneficiaryValidity;
	}

	@Override
	public String toString() {
		return "BenificiariesResponseDto [benificiaries=" + benificiaries + ", beneficiaryValidity="
				+ beneficiaryValidity + "]";
	}

}
