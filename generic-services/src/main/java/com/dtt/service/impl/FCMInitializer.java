package com.dtt.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FCMInitializer {

	private static final Logger logger =
			LoggerFactory.getLogger(FCMInitializer.class);

	@PostConstruct
	public void initialize() {
		try {
			ClassPathResource resource = new ClassPathResource("sma-firebase-adminsdk.json");

			logger.info("Path ==> {}", resource.getPath());

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
					.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options, "sma-staging");
			}

		} catch (Exception e) {
			logger.error("Error initializing Firebase", e);
		}
	}
}