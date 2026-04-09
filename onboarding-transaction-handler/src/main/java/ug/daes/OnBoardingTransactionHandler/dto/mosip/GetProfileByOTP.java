package ug.daes.OnBoardingTransactionHandler.dto.mosip;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetProfileByOTP {


    @JsonProperty("individualId")
    private String individualId;

    @JsonProperty("individualIdType")
    private String individualIdType;

    @JsonProperty("request")
    private OtpRequestDto request;

    // Getters and Setters
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

    public OtpRequestDto getRequest() {
        return request;
    }

    public void setRequest(OtpRequestDto request) {
        this.request = request;
    }
}
