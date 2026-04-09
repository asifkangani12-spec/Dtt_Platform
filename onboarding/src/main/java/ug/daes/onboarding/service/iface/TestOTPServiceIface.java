package ug.daes.onboarding.service.iface;


import com.dtt.common.util.ApiResponse;

public interface TestOTPServiceIface {
	
	ApiResponse testMobileOtpService();
	
	ApiResponse testEmailOtpService();
	
	ApiResponse testSendNotification();
	
}
