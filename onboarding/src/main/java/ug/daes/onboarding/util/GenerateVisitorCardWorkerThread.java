package ug.daes.onboarding.util;

import com.dtt.common.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import ug.daes.onboarding.dto.VisitorCardRequestDTO;
import ug.daes.onboarding.model.OnbSubscriber;
import ug.daes.onboarding.model.OnbSubscriberOnboardingData;
import ug.daes.onboarding.model.OnbSubscriberRaData;

public class GenerateVisitorCardWorkerThread implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(GenerateVisitorCardWorkerThread.class);

	/** The Constant CLASS. */
	static final String CLASS = "GenerateVisitorCardWorkerThread";
	
	private OnbSubscriber subscriber;
	private String visitorCardURL;
	private OnbSubscriberRaData subscriberRaData;
	private OnbSubscriberOnboardingData subscriberOnboardingData;

	public GenerateVisitorCardWorkerThread(String visitorCardURL, OnbSubscriber subscriber, OnbSubscriberRaData finalRaData, OnbSubscriberOnboardingData finalOnboardingData) {
		this.visitorCardURL = visitorCardURL;
		this.subscriber = subscriber;
		this.subscriberRaData = finalRaData;
		this.subscriberOnboardingData = finalOnboardingData;
	}

	@Override
	public void run() {
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			VisitorCardRequestDTO visitorCardRequestDTO = new VisitorCardRequestDTO();
			visitorCardRequestDTO.setVisitorCardNumber(subscriber.getSubscriberUid());
			visitorCardRequestDTO.setNationality(subscriberRaData.getCountryName());
			visitorCardRequestDTO.setSuid(subscriber.getSubscriberUid());
			visitorCardRequestDTO.setDateOfBirth(subscriber.getDateOfBirth().substring(0, 10));
			visitorCardRequestDTO.setSelfieUri(subscriberOnboardingData.getSelfieUri());
			visitorCardRequestDTO.setFullName(subscriber.getFullName());
			visitorCardRequestDTO.setIdDocNumber(subscriber.getIdDocNumber());
			visitorCardRequestDTO.setGender(subscriberOnboardingData.getGender());
			visitorCardRequestDTO.setSubscriberType(subscriberOnboardingData.getSubscriberType());
			
			
			HttpHeaders httpHeaders=new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(visitorCardRequestDTO, httpHeaders);
			
			restTemplate.exchange(visitorCardURL,HttpMethod.POST,requestEntity, ApiResponse.class);
			System.out.println(" visitor card generated successfully :: "+subscriber.getFullName());
		} catch (Exception e) {
			System.out.println(" Visito card generation failed ");
			logger.error("Unexpected exception", e);
		}
		
	}

}
