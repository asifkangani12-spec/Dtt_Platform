package ug.daes.OnBoardingTransactionHandler.dto.template;

public class PaymentDto {
    private String suid;
    private String serviceId;
    private String stakeHolder;

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getStakeHolder() {
        return stakeHolder;
    }

    public void setStakeHolder(String stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public String toString() {
        return "PaymentDto{" +
                "suid='" + suid + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", stakeHolder='" + stakeHolder + '\'' +
                '}';
    }
}
