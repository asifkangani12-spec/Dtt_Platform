package com.dtt.organization.dto;

import com.dtt.organization.model.OrgOrganizationCertificates;

import java.util.List;

public class OrgCertResponse {

    private List<OrgOrganizationCertificates> organizationCertificates;

    private float qrFaceMatchThreshold;

    public List<OrgOrganizationCertificates> getOrganizationCertificates() {
        return organizationCertificates;
    }

    public void setOrganizationCertificates(List<OrgOrganizationCertificates> organizationCertificates) {
        this.organizationCertificates = organizationCertificates;
    }

    public float getQrFaceMatchThreshold() {
        return qrFaceMatchThreshold;
    }

    public void setQrFaceMatchThreshold(float qrFaceMatchThreshold) {
        this.qrFaceMatchThreshold = qrFaceMatchThreshold;
    }

    @Override
    public String toString() {
        return "OrgCertResponse{" +
                "organizationCertificates=" + organizationCertificates +
                ", qrFaceMatchThreshold=" + qrFaceMatchThreshold +
                '}';
    }
}
