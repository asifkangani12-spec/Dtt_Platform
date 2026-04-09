package ug.daes.OnBoardingTransactionHandler.dto.mosip;



/**
 * The Class IdentityInfoDTO.
 * 
 * @author Sachin mali
 */
public class IdentityInfoDTO {

	/** Variable to hold language */
	private String language;

	/** Variable to hold value */
	private String value;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "IdentityInfoDTO [language=" + language + ", value=" + value + "]";
	}
	
}
