package com.dtt.organization.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.dtt.organization.dto.NotificationContextDTO;
import com.dtt.organization.dto.NotificationDTO;
import com.dtt.organization.dto.NotificationDataDTO;
import com.dtt.organization.model.OrgBenificiaries;
import com.dtt.organization.model.OrgSubscriber;
import com.dtt.organization.model.OrgSubscriberFcmToken;
import com.dtt.organization.repository.OrgBeneficiariesRepo;
import com.dtt.organization.repository.OrgSubscriberFcmTokenRepoIface;
import com.dtt.organization.repository.OrgSubscriberRepository;

public class LinkedBeneficiraryWorkerThread implements Runnable {

	private final OrgBenificiaries benificiariesDb;
	private final OrgSubscriberRepository subscriberRepository;
	private final OrgBeneficiariesRepo orgBeneficiariesRepo;
	private final OrgSubscriberFcmTokenRepoIface subscriberFcmTokenRepoIface;
	private final String sendNotificationURL;
	private final String sponsorLinkedMessage;

	Logger logger= LoggerFactory.getLogger(LinkedBeneficiraryWorkerThread.class);

	public LinkedBeneficiraryWorkerThread(
			OrgBenificiaries benificiariesDb,
			OrgSubscriberRepository subscriberRepository,
			OrgBeneficiariesRepo orgBeneficiariesRepo,
			OrgSubscriberFcmTokenRepoIface subscriberFcmTokenRepoIface,
			String sendNotificationURL,
			String sponsorLinkedMessage) {

		this.benificiariesDb = benificiariesDb;
		this.subscriberRepository = subscriberRepository;
		this.orgBeneficiariesRepo = orgBeneficiariesRepo;
		this.subscriberFcmTokenRepoIface = subscriberFcmTokenRepoIface;
		this.sendNotificationURL = sendNotificationURL;
		this.sponsorLinkedMessage = sponsorLinkedMessage;
	}
	@Override
	public void run() {
		try {
			


			OrgSubscriber subscriber = subscriberRepository.findSubscriberDetails(
					benificiariesDb.getBeneficiaryNin(),
					benificiariesDb.getBeneficiaryPassport(),
					benificiariesDb.getBeneficiaryUgPassEmail(),
					benificiariesDb.getBeneficiaryMobileNumber()
			).stream().findFirst().orElse(null);


			if (subscriber != null) {
				OrgSubscriberFcmToken subscriberFcmToken = subscriberFcmTokenRepoIface.findBysubscriberUid(subscriber.getSubscriberUid());

				benificiariesDb.setBeneficiaryDigitalId(subscriber.getSubscriberUid());
				benificiariesDb.setBeneficiaryName(subscriber.getFullName());
				benificiariesDb.setBeneficiaryConsentAcquired(true);
				benificiariesDb.setBeneficiaryMobileNumber(subscriber.getMobileNumber());
				benificiariesDb.setBeneficiaryUgPassEmail(subscriber.getEmailId());
				benificiariesDb.setBeneficiaryNin(subscriber.getNationalId());
				benificiariesDb.setBeneficiaryPassport(subscriber.getIdDocNumber());

				orgBeneficiariesRepo.save(benificiariesDb);
				logger.info("subscriber.getFullName(): {}", subscriber.getFullName());
				logger.info("subscriberView.getFcmToken(): {}", subscriberFcmToken.getFcmToken());
				logger.info("sendNotificationURL: {}", sendNotificationURL);
				sendNotification(subscriber.getFullName(), subscriberFcmToken.getFcmToken(), sendNotificationURL,sponsorLinkedMessage);

			}

		} catch (Exception e) {
			logger.info("{}",e.getMessage());
		}

	}

	public void sendNotification(String fullName, String fcmToken, String sendNotificationURL,String sponsorLinkedMessage) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			NotificationDTO notificationBody = new NotificationDTO();
			NotificationDataDTO dataDTO = new NotificationDataDTO();
		    NotificationContextDTO contextDTO = new NotificationContextDTO();
			notificationBody.setTo(fcmToken);
			notificationBody.setPriority("high");
			dataDTO.setTitle("Hi " + fullName);
			Map<String, String> orgLinkStatus = new HashMap<>();

			dataDTO.setBody(sponsorLinkedMessage);
			orgLinkStatus.put("beneficiaryLinkedStatus", "Success");

			contextDTO.setPrefBeneficiaryLink(orgLinkStatus);
			dataDTO.setNotificationContext(contextDTO);
			notificationBody.setData(dataDTO);
			HttpEntity<Object> requestEntity = new HttpEntity<>(notificationBody, headers);

			ResponseEntity<Object> res = restTemplate.exchange(sendNotificationURL, HttpMethod.POST, requestEntity,
					Object.class);
			if (res.getStatusCode().value() == 200) {
				logger.info("Notification sent");
			} else {
				logger.info("Notification failed");
			}

		} catch (Exception e) {
			logger.info("{}",e.getMessage());
		}
	}

}
