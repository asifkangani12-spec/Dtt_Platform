package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class OrgListDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String suid;

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    @Override
    public String toString() {
        return "OrgListDto{" +
                "suid='" + suid + '\'' +
                '}';
    }
}
