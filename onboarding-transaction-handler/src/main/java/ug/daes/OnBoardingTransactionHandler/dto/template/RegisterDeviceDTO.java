package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class RegisterDeviceDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String identifier;
	
	private String referenceIdentifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getReferenceIdentifier() {
		return referenceIdentifier;
	}

	public void setReferenceIdentifier(String referenceIdentifier) {
		this.referenceIdentifier = referenceIdentifier;
	}

	@Override
	public String toString() {
		return "RegisterDeviceDTO [identifier=" + identifier + ", referenceIdentifier=" + referenceIdentifier + "]";
	}
	
	
}
