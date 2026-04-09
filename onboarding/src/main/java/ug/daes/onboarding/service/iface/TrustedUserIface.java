package ug.daes.onboarding.service.iface;

import java.util.List;

import com.dtt.common.util.ApiResponse;
import ug.daes.onboarding.model.OnbTrustedUser;

public interface TrustedUserIface {
	
	ApiResponse getTrustedUserByEmail(String email);
	
	ApiResponse updateTrustedUser(OnbTrustedUser trustedUser);
	
	ApiResponse deleteTrustedUser(String email);
	
	ApiResponse getAllTrustedUser();
	
	ApiResponse addTrustedUser(List<OnbTrustedUser> trustedUser);

}
