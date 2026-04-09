package ug.daes.onboarding.dto;

import java.util.List;

public class PaymentStatusHolder {

    private List<String> paymentStatus;
    private String paymentIntiatiedStatus;
    private String paymentCertStatus;

    public List<String> getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(List<String> paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentIntiatiedStatus() {
        return paymentIntiatiedStatus;
    }

    public void setPaymentIntiatiedStatus(String paymentIntiatiedStatus) {
        this.paymentIntiatiedStatus = paymentIntiatiedStatus;
    }

    public String getPaymentCertStatus() {
        return paymentCertStatus;
    }

    public void setPaymentCertStatus(String paymentCertStatus) {
        this.paymentCertStatus = paymentCertStatus;
    }
}