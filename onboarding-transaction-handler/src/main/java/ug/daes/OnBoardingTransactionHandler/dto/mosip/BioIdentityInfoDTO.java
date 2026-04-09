package ug.daes.OnBoardingTransactionHandler.dto.mosip;


/**
 * The Class BioIdentityInfoDTO.
 * 
 * @author Sachin mali
 */
public class BioIdentityInfoDTO {
	
	/** The Value for data */
	private String data;

	private String specVersion;
	
	/** The Value for hash */
	private String hash;
	
	/** The Value for sessionKey */
	private String sessionKey;
	
	/** The Value for signature */
	private String signature;

	private String thumbprint;


	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;

	}

	public String getSpecVersion() {
		return specVersion;
	}

	public void setSpecVersion(String specVersion) {
		this.specVersion = specVersion;
	}

	public String getThumbprint() {
		return thumbprint;
	}

	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}


	@Override
	public String toString() {
		return "{" +
				"data='" + data + '\'' +
				", specVersion='" + specVersion + '\'' +
				", hash='" + hash + '\'' +
				", sessionKey='" + sessionKey + '\'' +
				", signature='" + signature + '\'' +
				", thumbprint='" + thumbprint + '\'' +
				'}';
	}
}
