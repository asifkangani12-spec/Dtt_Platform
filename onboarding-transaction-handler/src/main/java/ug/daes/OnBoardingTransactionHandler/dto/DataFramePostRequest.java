package ug.daes.OnBoardingTransactionHandler.dto;

import java.io.Serializable;


public class DataFramePostRequest implements Serializable {
    
	private String serviceMethod;
    private transient Object requestBody;
    private String hashValue;

    public DataFramePostRequest() {
        // DataFramePostRequest
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

	@Override
	public String toString() {
		return "DataFramePostRequest [serviceMethod=" + serviceMethod + ", requestBody=" + requestBody + ", hashValue="
				+ hashValue + "]";
	}
}
