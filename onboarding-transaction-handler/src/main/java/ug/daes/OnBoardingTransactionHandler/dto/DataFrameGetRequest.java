package ug.daes.OnBoardingTransactionHandler.dto;

import java.io.Serializable;
import java.util.Arrays;

public class DataFrameGetRequest implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serviceMethod;
    private transient Object[] pars;

    public DataFrameGetRequest() {
        // DataFrameGetRequest
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public Object[] getPars() {
        return pars;
    }

    public void setPars(Object[] pars) {
        this.pars = pars;
    }

	@Override
	public String toString() {
		return "DataFrameGetRequest [serviceMethod=" + serviceMethod + ", pars=" + Arrays.toString(pars) + "]";
	}
}
