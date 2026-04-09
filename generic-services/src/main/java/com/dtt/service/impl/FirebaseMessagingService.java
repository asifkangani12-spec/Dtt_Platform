package com.dtt.service.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import com.dtt.requestdto.PushNotificationRequest;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@Service
public class FirebaseMessagingService {
    private static final Logger logger =
            LoggerFactory.getLogger(FirebaseMessagingService.class);
	
	private final FirebaseMessaging firebaseMessaging;

    private final AndroidPushNotificationsService androidPushNotificationsService;


    public FirebaseMessagingService(
            FirebaseMessaging firebaseMessaging,
            AndroidPushNotificationsService androidPushNotificationsService) {

        this.firebaseMessaging = firebaseMessaging;
        this.androidPushNotificationsService = androidPushNotificationsService;
    }

    public String sendNotification(PushNotificationRequest notificationRequest) throws FirebaseMessagingException {

        Map<String, String> data = new HashMap<>();
        Map<String, Object> map = androidPushNotificationsService.createMap(
                notificationRequest.getData().getNotificationContext()
        );

        data.put("title", notificationRequest.getData().getTitle());
        data.put("body", notificationRequest.getData().getBody());
        data.put("notificationContext", map.toString());

        logger.info("Data ==> {}", data);

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(Priority.HIGH)
                .build();

        ApsAlert apsAlert = ApsAlert.builder()
                .setTitle(notificationRequest.getData().getTitle())
                .setBody(notificationRequest.getData().getBody())
                .build();

        Aps aps = Aps.builder()
                .setSound("default")
                .setAlert(apsAlert)
                .setMutableContent(true)
                .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(aps)
                .build();

        Message message = Message.builder()
                .setToken(notificationRequest.getTo())
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .putAllData(data)
                .build();

        try {
            String response = firebaseMessaging.send(message);
            logger.info("Notification sent successfully. Message ID: {}", response);
            return "Notification sent successfully. Message ID: " + response;

        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send notification:::{}", e.getMessage());
            return "Failed to send notification: " + e.getMessage();
        }
    }


}

