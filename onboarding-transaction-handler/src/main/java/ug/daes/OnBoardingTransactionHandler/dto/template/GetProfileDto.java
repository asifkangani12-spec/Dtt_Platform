package ug.daes.OnBoardingTransactionHandler.dto.template;

public class GetProfileDto {

	private boolean selfieRequired;
	
	private String idType;
	private String idValue;

	public boolean isSelfieRequired() {
		return selfieRequired;
	}

	public void setSelfieRequired(boolean selfieRequired) {
		this.selfieRequired = selfieRequired;
	}
	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

	@Override
	public String toString() {
		return "GetProfileDto [selfieRequired=" + selfieRequired + ", idType=" + idType + ", idValue=" + idValue + "]";
	}

}