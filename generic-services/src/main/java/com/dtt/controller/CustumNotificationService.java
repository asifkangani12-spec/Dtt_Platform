package com.dtt.controller;

import com.dtt.requestdto.PushNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dtt.service.impl.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessagingException;

@Controller
public class CustumNotificationService {
	private static final Logger logger = LoggerFactory.getLogger(CustumNotificationService.class);

	private final FirebaseMessagingService messagingService;

    public CustumNotificationService(FirebaseMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/send/notification")
	public ResponseEntity<String> sentIosNotification(@RequestBody PushNotificationRequest notificationRequest)
			throws FirebaseMessagingException {
		try {

			logger.debug("in controller");
			String response = messagingService.sendNotification(notificationRequest);
			logger.debug("Response => {}", response);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.info("{}",e.getMessage());
			return ResponseEntity.internalServerError().body("Notification failed: " + e.getMessage());
		}

	}


}
