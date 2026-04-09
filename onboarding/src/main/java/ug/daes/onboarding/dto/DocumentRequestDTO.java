package ug.daes.onboarding.dto;

public class DocumentRequestDTO {

    private String documentType;
    private String documentNumber;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    public String toString() {
        return "DocumentRequestDTO{" +
                "documentType='" + documentType + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                '}';
    }
}
