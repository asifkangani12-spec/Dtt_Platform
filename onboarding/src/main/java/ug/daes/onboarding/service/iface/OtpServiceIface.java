package ug.daes.onboarding.service.iface;

import java.net.UnknownHostException;
import java.text.ParseException;


import com.dtt.common.util.ApiResponse;
import ug.daes.onboarding.dto.MobileOTPDto;

public interface OtpServiceIface {

	ApiResponse sendOTPMobileSms(MobileOTPDto mobileOTPDto) throws ParseException, UnknownHostException;
	
	ApiResponse sendEmail(MobileOTPDto mobileOTPDto) ;
}
