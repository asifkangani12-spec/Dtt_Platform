package ug.daes.onboarding.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import ug.daes.onboarding.model.OnbSubscriberDevice;
import ug.daes.onboarding.repository.SubscriberDeviceRepoIface;
import ug.daes.onboarding.service.iface.PolicyIface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class PolicyImpl implements PolicyIface {
    private static final Logger logger = LoggerFactory.getLogger(PolicyImpl.class);
    private final SubscriberDeviceRepoIface subscriberDeviceRepoIface;
    private final ExceptionHandlerUtil exceptionHandlerUtil;
    public PolicyImpl(SubscriberDeviceRepoIface subscriberDeviceRepoIface,
                      ExceptionHandlerUtil exceptionHandlerUtil) {
        this.subscriberDeviceRepoIface = subscriberDeviceRepoIface;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    @Override
    public boolean checkPolicy(String date, String pattern, long policy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            LocalDateTime currTime= LocalDateTime.now();
            long noOfHours=dateTime.until(currTime, ChronoUnit.HOURS);
            return noOfHours >= policy;
        } catch (Exception e) {
            logger.error("Error parsing date. Use format yyyy-MM-dd HH:mm:ss", e);
            return false;
        }

    }

    @Override
    public String matchDeviceUid(String suid, String deviceUid) {

        try {
            List<OnbSubscriberDevice> devices = subscriberDeviceRepoIface.findBydeviceUid(deviceUid);
            OnbSubscriberDevice subscriberDevice = devices.isEmpty() ? null : devices.get(0);

            return subscriberDevice==null?null:subscriberDevice.getDeviceUid();
        } catch (Exception e) {
            logger.error("Error while matching device UID", e);
            return null;
        }
    }

    @Override
    public ApiResponse checkPolicyRange(String date, String pattern, long minLimit) {

        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date value is null or empty");
        }

        try {
            LocalDateTime dateTime;

            if (date.contains("T")) {
                dateTime = LocalDateTime.parse(date);
            } else {
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateTime = LocalDateTime.parse(date, formatter);
            }

            LocalDateTime currTime = LocalDateTime.now();
            long noOfHours = dateTime.until(currTime, ChronoUnit.HOURS);

            if (noOfHours < minLimit) {
                return exceptionHandlerUtil
                        .createErrorResponseWithResult("api.error.policy.limit", false);
            }

            return exceptionHandlerUtil
                    .createSuccessResponse("api.response.device.policy", noOfHours);

        } catch (DateTimeParseException e) {
            logger.error("Invalid date value: {}", date, e);
            return exceptionHandlerUtil
                    .createErrorResponseWithResult("Invalid date format", false);
        }
    }
}
