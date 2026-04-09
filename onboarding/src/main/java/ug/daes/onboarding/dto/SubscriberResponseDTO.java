package ug.daes.onboarding.dto;

public class SubscriberResponseDTO {

    private String subscriberUid;
    private String fullName;
    private String passportNumber;
    private String nationalIdNumber;
    private String mobileNumber;
    private String fcmToken;

    public String getSubscriberUid() {
        return subscriberUid;
    }

    public void setSubscriberUid(String subscriberUid) {
        this.subscriberUid = subscriberUid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getNationalIdNumber() {
        return nationalIdNumber;
    }

    public void setNationalIdNumber(String nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "SubscriberResponseDTO{" +
                "subscriberUid='" + subscriberUid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", nationalIdNumber='" + nationalIdNumber + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }
}
