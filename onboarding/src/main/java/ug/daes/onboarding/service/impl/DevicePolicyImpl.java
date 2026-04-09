package ug.daes.onboarding.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;


import ug.daes.onboarding.model.OnbDevicePolicyModel;
import ug.daes.onboarding.repository.DevicePolicyRepository;
import ug.daes.onboarding.service.iface.DevicePolicyIface;

@Service
public class DevicePolicyImpl implements DevicePolicyIface {

	private static Logger logger = LoggerFactory.getLogger(DevicePolicyImpl.class);


	private static final String CLASS = "DevicePolicyImpl";

	@Value(value = "${device.update.min.policy}")
	private int minHour;

	@Value(value = "${device.update.max.policy}")
	private int maxHour;

	private final DevicePolicyRepository devicePolicyRepository;

	private final ExceptionHandlerUtil  exceptionHandlerUtil;

	public DevicePolicyImpl(DevicePolicyRepository devicePolicyRepository,
							ExceptionHandlerUtil exceptionHandlerUtil) {
		this.devicePolicyRepository = devicePolicyRepository;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	@Override
	public ApiResponse devicePolicyHour(int hour) {
		try {
			logger.info(CLASS + " request hour {}", hour);

			if (hour < minHour || hour > maxHour) {
				return exceptionHandlerUtil.createFailedResponseWithCustomMessage(
						"Please enter value between " + minHour + " and " + maxHour, null);
			}
			OnbDevicePolicyModel devicePolicy = devicePolicyRepository.getDevicePolicyHour();
			if (devicePolicy == null) {
				OnbDevicePolicyModel devicePolicyModel = new OnbDevicePolicyModel();
				devicePolicyModel.setDevicePolicyHour(hour);
				devicePolicyRepository.save(devicePolicyModel);
				return exceptionHandlerUtil.successResponse("api.response.Device.Policy.updated.successfully");
			} else {
				devicePolicy.setDevicePolicyHour(hour);
				devicePolicyRepository.save(devicePolicy);
				return exceptionHandlerUtil.successResponse("api.response.Device.Policy.updated.successfully");
			}
		} catch (Exception e) {
			logger.error("Unexpected exception", e);

			return exceptionHandlerUtil.handleException(e);
		}
	}

}
