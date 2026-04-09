package com.dtt.responsedto;

public class UAEIDAuthenticationProfileDTO {

    private String idn;
    private String fullnameEN;
    private String fullnameAR;
    private String firstnameEN;
    private String firstnameAR;
    private String lastnameEN;
    private String lastnameAR;
    private String nationalityEN;
    private String nationalityAR;
    private String gender;
    private String idType;
    private String titleEN;
    private String titleAR;
    private String dateOfBirth;
    private String profileType;
    private String suid;
    private String loa;
    private String unifiedId;

    private String passportNumber;

    public String getIdn() {
        return idn;
    }

    public void setIdn(String idn) {
        this.idn = idn;
    }

    public String getFullnameEN() {
        return fullnameEN;
    }

    public void setFullnameEN(String fullnameEN) {
        this.fullnameEN = fullnameEN;
    }

    public String getFullnameAR() {
        return fullnameAR;
    }

    public void setFullnameAR(String fullnameAR) {
        this.fullnameAR = fullnameAR;
    }

    public String getFirstnameEN() {
        return firstnameEN;
    }

    public void setFirstnameEN(String firstnameEN) {
        this.firstnameEN = firstnameEN;
    }

    public String getFirstnameAR() {
        return firstnameAR;
    }

    public void setFirstnameAR(String firstnameAR) {
        this.firstnameAR = firstnameAR;
    }

    public String getLastnameEN() {
        return lastnameEN;
    }

    public void setLastnameEN(String lastnameEN) {
        this.lastnameEN = lastnameEN;
    }

    public String getLastnameAR() {
        return lastnameAR;
    }

    public void setLastnameAR(String lastnameAR) {
        this.lastnameAR = lastnameAR;
    }

    public String getNationalityEN() {
        return nationalityEN;
    }

    public void setNationalityEN(String nationalityEN) {
        this.nationalityEN = nationalityEN;
    }

    public String getNationalityAR() {
        return nationalityAR;
    }

    public void setNationalityAR(String nationalityAR) {
        this.nationalityAR = nationalityAR;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getTitleEN() {
        return titleEN;
    }

    public void setTitleEN(String titleEN) {
        this.titleEN = titleEN;
    }

    public String getTitleAR() {
        return titleAR;
    }

    public void setTitleAR(String titleAR) {
        this.titleAR = titleAR;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getLoa() {
        return loa;
    }

    public void setLoa(String loa) {
        this.loa = loa;
    }

    public String getUnifiedId() {
        return unifiedId;
    }

    public void setUnifiedId(String unifiedId) {
        this.unifiedId = unifiedId;
    }


    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String toString() {
        return "UAEIDAuthenticationProfileDTO{" +
                "idn='" + idn + '\'' +
                ", fullnameEN='" + fullnameEN + '\'' +
                ", fullnameAR='" + fullnameAR + '\'' +
                ", firstnameEN='" + firstnameEN + '\'' +
                ", firstnameAR='" + firstnameAR + '\'' +
                ", lastnameEN='" + lastnameEN + '\'' +
                ", lastnameAR='" + lastnameAR + '\'' +
                ", nationalityEN='" + nationalityEN + '\'' +
                ", nationalityAR='" + nationalityAR + '\'' +
                ", gender='" + gender + '\'' +
                ", idType='" + idType + '\'' +
                ", titleEN='" + titleEN + '\'' +
                ", titleAR='" + titleAR + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", profileType='" + profileType + '\'' +
                ", suid='" + suid + '\'' +
                ", loa='" + loa + '\'' +
                ", unifiedId='" + unifiedId + '\'' +
                '}';
    }
}
