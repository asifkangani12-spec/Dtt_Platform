package com.dtt.organization.restcontroller;


import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.organization.config.OrgSentryClientExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SentryController {


	Logger logger= LoggerFactory.getLogger(SentryController.class);
	private final OrgSentryClientExceptions sentryClientExceptions;

	public SentryController(OrgSentryClientExceptions sentryClientExceptions) {
		this.sentryClientExceptions = sentryClientExceptions;
	}

	@GetMapping("api/get/service/sentry")
	public ApiResponse getServiceStatusSentry() {
		String suid = null;
		try {

			suid = generateSubscriberUniqueId();
			sentryClientExceptions.captureTags(suid, "SentryController","getServiceStatusSentry" );

			return AppUtil.createApiResponse(false, "Service is down", null);

		} catch (Exception e) {
			logger.error("Error occurred while processing request", e);

			sentryClientExceptions.captureExceptions(e);

			return AppUtil.createApiResponse(false, "down", null);
		}

	}

	public String generateSubscriberUniqueId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}



}
