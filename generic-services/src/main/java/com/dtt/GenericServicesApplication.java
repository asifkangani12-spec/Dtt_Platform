package com.dtt;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import ug.daes.DAESService;
import ug.daes.PKICoreServiceException;
import ug.daes.Result;

import java.io.IOException;

@Configuration
public class GenericServicesApplication {

	private static final Logger logger = LoggerFactory.getLogger(GenericServicesApplication.class);
	
	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(new ClassPathResource("sma-firebase-adminsdk.json").getInputStream());
		FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
		return FirebaseMessaging.getInstance(app);
	}

	@PostConstruct
	public void init() {
		try {
			Result result = DAESService.initPKINativeUtils();
			if (result.getStatus() == 0) {
				logger.info("Started::: {}",result.getStatusMessage());
			} else {
				logger.info("{}",result.getStatusMessage());
				System.exit(1);
			}
		} catch (PKICoreServiceException e) {
			logger.info("{}",e.getMessage());
			System.exit(1);
		}
	}

}
