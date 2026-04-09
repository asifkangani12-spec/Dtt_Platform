package ug.daes.OnBoardingTransactionHandler.dto;

public class UINDto {

    private String idDocType;
    private String idDocNumber;

    public String getIdDocType() {
        return idDocType;
    }

    public void setIdDocType(String idDocType) {
        this.idDocType = idDocType;
    }

    public String getIdDocNumber() {
        return idDocNumber;
    }

    public void setIdDocNumber(String idDocNumber) {
        this.idDocNumber = idDocNumber;
    }

    @Override
    public String toString() {
        return "UINDto{" +
                "idDocType='" + idDocType + '\'' +
                ", idDocNumber='" + idDocNumber + '\'' +
                '}';
    }
}
