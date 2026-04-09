package ug.daes.OnBoardingTransactionHandler.dto.mosip;


/**
 * The Class AuthRequestDTO.
 * @author Sachin mali
 */
public class AuthRequestDTO extends BaseAuthRequestDTO {

	/** The value for requestedAuth*/
	private AuthTypeDTO requestedAuth;

	/** The value for transactionID*/
	private String transactionID;

	/** The value for requestTime*/
	private String requestTime;
	
	/** The value for request*/
	private RequestAuthDTO request;

	/** The value for consentObtained*/
	private boolean consentObtained;
	

	/** The value for individualId*/
	private String individualId;
	

	/** The value for individualIdType*/
	private String individualIdType;
	
	/** The value for requestHMAC*/
	private String requestHMAC;
	
	/** The value for thumbprint*/
	private String thumbprint;
	
	/** The value for requestSessionKey*/
	private String requestSessionKey;
	
	private String env;
	
	private String  domainUri;


	public AuthTypeDTO getRequestedAuth() {
		return requestedAuth;
	}

	public void setRequestedAuth(AuthTypeDTO requestedAuth) {
		this.requestedAuth = requestedAuth;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public RequestAuthDTO getRequest() {
		return request;
	}

	public void setRequest(RequestAuthDTO request) {
		this.request = request;
	}

	public boolean isConsentObtained() {
		return consentObtained;
	}

	public void setConsentObtained(boolean consentObtained) {
		this.consentObtained = consentObtained;
	}

	public String getIndividualId() {
		return individualId;
	}

	public void setIndividualId(String individualId) {
		this.individualId = individualId;
	}

	public String getIndividualIdType() {
		return individualIdType;
	}

	public void setIndividualIdType(String individualIdType) {
		this.individualIdType = individualIdType;
	}

	public String getRequestHMAC() {
		return requestHMAC;
	}

	public void setRequestHMAC(String requestHMAC) {
		this.requestHMAC = requestHMAC;
	}

	public String getThumbprint() {
		return thumbprint;
	}

	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}

	public String getRequestSessionKey() {
		return requestSessionKey;
	}

	public void setRequestSessionKey(String requestSessionKey) {
		this.requestSessionKey = requestSessionKey;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getDomainUri() {
		return domainUri;
	}

	public void setDomainUri(String domainUri) {
		this.domainUri = domainUri;
	}


	@Override
	public String toString() {
		return "AuthRequestDTO{" +
				"requestedAuth=" + requestedAuth +
				", transactionID='" + transactionID + '\'' +
				", requestTime='" + requestTime + '\'' +
				", request=" + request +
				", consentObtained=" + consentObtained +
				", individualId='" + individualId + '\'' +
				", individualIdType='" + individualIdType + '\'' +
				", requestHMAC='" + requestHMAC + '\'' +
				", thumbprint='" + thumbprint + '\'' +
				", requestSessionKey='" + requestSessionKey + '\'' +
				", env='" + env + '\'' +
				", domainUri='" + domainUri + '\'' +
				'}';
	}
}
