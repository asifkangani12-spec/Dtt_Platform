package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class CountryDetailsDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String country_Name;
	private String flag;
	private String country_Code;
	private String max_Mobile_Digits;
	private String support_NationalityID;
	public String getCountry_Name() {
		return country_Name;
	}
	public void setCountry_Name(String country_Name) {
		this.country_Name = country_Name;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getCountry_Code() {
		return country_Code;
	}
	public void setCountry_Code(String country_Code) {
		this.country_Code = country_Code;
	}
	public String getMax_Mobile_Digits() {
		return max_Mobile_Digits;
	}
	public void setMax_Mobile_Digits(String max_Mobile_Digits) {
		this.max_Mobile_Digits = max_Mobile_Digits;
	}
	public String getSupport_NationalityID() {
		return support_NationalityID;
	}
	public void setSupport_NationalityID(String support_NationalityID) {
		this.support_NationalityID = support_NationalityID;
	}
	@Override
	public String toString() {
		return "CountryDetailsReqDTO [country_Name=" + country_Name + ", flag=" + flag + ", country_Code="
				+ country_Code + ", max_Mobile_Digits=" + max_Mobile_Digits + ", support_NationalityID="
				+ support_NationalityID + "]";
	}
	
}
