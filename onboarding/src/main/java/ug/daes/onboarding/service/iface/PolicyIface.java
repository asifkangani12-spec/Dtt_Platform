package ug.daes.onboarding.service.iface;


import com.dtt.common.util.ApiResponse;

public interface PolicyIface {

    boolean checkPolicy(String date, String pattern, long policy);

    String matchDeviceUid(String suid, String deviceUid);

    ApiResponse checkPolicyRange(String date, String pattern, long minLimit);
    
}
