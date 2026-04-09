package ug.daes.OnBoardingTransactionHandler.dto.mosip;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OtpRequestDto {


    @JsonProperty("otp")
    private String otp;

    // Getters and Setters
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


}
