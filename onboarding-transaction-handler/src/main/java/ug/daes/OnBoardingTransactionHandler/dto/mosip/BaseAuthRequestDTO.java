package ug.daes.OnBoardingTransactionHandler.dto.mosip;


/**
 * The Class BaseAuthRequestDTO.
 * 
 * @author Sachin mali
 */
public class BaseAuthRequestDTO {
	
	/** The value for Id*/
	private String id;
	
	/** The value for version*/
	private String version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "BaseAuthRequestDTO [id=" + id + ", version=" + version + "]";
	}
	
}
