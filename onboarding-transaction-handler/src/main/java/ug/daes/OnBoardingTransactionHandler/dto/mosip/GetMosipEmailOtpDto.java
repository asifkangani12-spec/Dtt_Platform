package ug.daes.OnBoardingTransactionHandler.dto.mosip;

public class GetMosipEmailOtpDto {

    private String individualId;
    private String individualIdType;

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

    @Override
    public String toString() {
        return "GetMosipEmailOtpDto{" +
                "individualId='" + individualId + '\'' +
                ", individualIdType='" + individualIdType + '\'' +
                '}';
    }
}
