package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class DocumentDataDto implements Serializable {

    private String documentNumber;
    private int documentType;
    private String documentCode;
    private String dateOfBirth;
    private String dateOfExpiry;
    private String gender;
    private String issuingState;
    private String nationality;
    private String optionalData1;
    private String optionalData2;
    private String primaryIdentifier;
    private String secondaryIdentifier;
    private transient Object rawResponseData;

    public Object getRawResponseData() {
        return rawResponseData;
    }

    public void setRawResponseData(Object rawResponseData) {
        this.rawResponseData = rawResponseData;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIssuingState() {
        return issuingState;
    }

    public void setIssuingState(String issuingState) {
        this.issuingState = issuingState;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOptionalData1() {
        return optionalData1;
    }

    public void setOptionalData1(String optionalData1) {
        this.optionalData1 = optionalData1;
    }

    public String getOptionalData2() {
        return optionalData2;
    }

    public void setOptionalData2(String optionalData2) {
        this.optionalData2 = optionalData2;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public void setPrimaryIdentifier(String primaryIdentifier) {
        this.primaryIdentifier = primaryIdentifier;
    }

    public String getSecondaryIdentifier() {
        return secondaryIdentifier;
    }

    public void setSecondaryIdentifier(String secondaryIdentifier) {
        this.secondaryIdentifier = secondaryIdentifier;
    }

    @Override
    public String toString() {
        return "{" +
                "\"documentNumber\":\"" + documentNumber + "\"," +
                "\"documentType\":\"" + documentType + "\"," +
                "\"documentCode\":\"" + documentCode + "\"," +
                "\"dateOfBirth\":\"" + dateOfBirth + "\"," +
                "\"dateOfExpiry\":\"" + dateOfExpiry + "\"," +
                "\"gender\":\"" + gender + "\"," +
                "\"issuingState\":\"" + issuingState + "\"," +
                "\"nationality\":\"" + nationality + "\"," +
                "\"optionalData1\":\"" + optionalData1 + "\"," +
                "\"optionalData2\":\"" + optionalData2 + "\"," +
                "\"primaryIdentifier\":\"" + primaryIdentifier + "\"," +
                "\"secondaryIdentifier\":\"" + secondaryIdentifier + "\"" +
                "}";
    }

}
