package ug.daes.OnBoardingTransactionHandler.dto.template;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class MainDto implements Serializable {

    private String appName;

    private String profileType;

    @NotBlank(message = "Document Type Can't be null or empty")
    private String documentType;


    private DocumentDataDto documentDetails;

    private String selfieImage;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getSelfie() {
        return selfieImage;
    }

    public void setSelfie(String selfie) {
        this.selfieImage = selfie;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public DocumentDataDto getDocumentDetails() {
        return documentDetails;
    }

    public void setDocumentDetails(DocumentDataDto documentDetails) {
        this.documentDetails = documentDetails;
    }


    @Override
    public String toString() {
        return "MainDto{" +
                "appName='" + appName + '\'' +
                ", profileType='" + profileType + '\'' +
                ", documentType='" + documentType + '\'' +
                ", documentDetails=" + documentDetails +
                ", selfieImage='" + selfieImage + '\'' +
                '}';
    }
}

