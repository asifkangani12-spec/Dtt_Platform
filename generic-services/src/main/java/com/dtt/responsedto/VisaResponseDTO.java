package com.dtt.responsedto;

public class VisaResponseDTO {


        String uidNumber;

        String fileNumber;

        String documentNo;

        String documentType;

            String fullNameEn;
            String fullNameAr;
            String placeOfBirthEn;
            String placeOfBirthAr;
            String occupationEn;
            String occupationAr;
            String currentNationality;


            String visaType;

            String issueDate;

            String expiryDate;

            String personFace ;

    public String getUidNumber() {
        return uidNumber;
    }

    public void setUidNumber(String uidNumber) {
        this.uidNumber = uidNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFullNameEn() {
        return fullNameEn;
    }

    public void setFullNameEn(String fullNameEn) {
        this.fullNameEn = fullNameEn;
    }

    public String getFullNameAr() {
        return fullNameAr;
    }

    public void setFullNameAr(String fullNameAr) {
        this.fullNameAr = fullNameAr;
    }

    public String getPlaceOfBirthEn() {
        return placeOfBirthEn;
    }

    public void setPlaceOfBirthEn(String placeOfBirthEn) {
        this.placeOfBirthEn = placeOfBirthEn;
    }

    public String getPlaceOfBirthAr() {
        return placeOfBirthAr;
    }

    public void setPlaceOfBirthAr(String placeOfBirthAr) {
        this.placeOfBirthAr = placeOfBirthAr;
    }

    public String getOccupationEn() {
        return occupationEn;
    }

    public void setOccupationEn(String occupationEn) {
        this.occupationEn = occupationEn;
    }

    public String getOccupationAr() {
        return occupationAr;
    }

    public void setOccupationAr(String occupationAr) {
        this.occupationAr = occupationAr;
    }

    public String getCurrentNationality() {
        return currentNationality;
    }

    public void setCurrentNationality(String currentNationality) {
        this.currentNationality = currentNationality;
    }

    public String getVisaType() {
        return visaType;
    }

    public void setVisaType(String visaType) {
        this.visaType = visaType;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPersonFace() {
        return personFace;
    }

    public void setPersonFace(String personFace) {
        this.personFace = personFace;
    }

    @Override
    public String toString() {
        return "VisaResponseDTO{" +
                "uidNumber='" + uidNumber + '\'' +
                ", fileNumber='" + fileNumber + '\'' +
                ", documentNo='" + documentNo + '\'' +
                ", documentType='" + documentType + '\'' +
                ", fullNameEn='" + fullNameEn + '\'' +
                ", fullNameAr='" + fullNameAr + '\'' +
                ", placeOfBirthEn='" + placeOfBirthEn + '\'' +
                ", placeOfBirthAr='" + placeOfBirthAr + '\'' +
                ", occupationEn='" + occupationEn + '\'' +
                ", occupationAr='" + occupationAr + '\'' +
                ", currentNationality='" + currentNationality + '\'' +
                ", visaType='" + visaType + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", personFace='" + personFace + '\'' +
                '}';
    }
}
