package ug.daes.onboarding.service.iface;

import com.dtt.common.util.ApiResponse;
import ug.daes.onboarding.dto.DeviceInfo;
import ug.daes.onboarding.dto.MobileOTPDto;
import ug.daes.onboarding.model.OnbSubscriberDevice;


public interface DeviceUpdateIface {
    ApiResponse validateSubscriberAndDevice(DeviceInfo deviceInfo,MobileOTPDto mobileOTPDto);


    void updateSubscriberDeviceAndHistory(OnbSubscriberDevice oldDevice, String newDeviceUid);

    ApiResponse activateNewDevice(DeviceInfo deviceInfo, MobileOTPDto mobileOTPDto);
}
