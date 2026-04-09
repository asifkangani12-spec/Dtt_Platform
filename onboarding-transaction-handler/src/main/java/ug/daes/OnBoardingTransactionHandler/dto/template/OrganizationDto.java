package ug.daes.OnBoardingTransactionHandler.dto.template;

public class OrganizationDto {
    private String orgUid;

    public String getOrgUid() {
        return orgUid;
    }

    public void setOrgUid(String orgUid) {
        this.orgUid = orgUid;
    }

    @Override
    public String toString() {
        return "OrganizationDto{" +
                "orgUid='" + orgUid + '\'' +
                '}';
    }
}
