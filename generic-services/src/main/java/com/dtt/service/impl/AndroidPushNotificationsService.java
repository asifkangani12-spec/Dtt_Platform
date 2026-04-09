package com.dtt.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dtt.requestdto.NotificationContextDTO;
@Service
public class AndroidPushNotificationsService {
	private static final Logger logger =
			LoggerFactory.getLogger(AndroidPushNotificationsService.class);



	public Map<String, Object> createMap(NotificationContextDTO notificationContextDTO) {
		logger.info("notificationContextDTO ==> {}", notificationContextDTO);
		Map<String, Object> map = new HashMap<>();
		if (notificationContextDTO.isPrefOnboardingStatus()) {
			map.put("PREF_ONBOARDING_STATUS", true);
		}

		if (notificationContextDTO.getPrefOnboardingApprovalStatus() != null) {
			map.put("PREF_ONBOARDING_APPROVAL_STATUS", notificationContextDTO.getPrefOnboardingApprovalStatus());
		}

		if (notificationContextDTO.getPrefCertificateStatus() != null) {
			map.put("PREF_CERTIFICATE_STATUS", notificationContextDTO.getPrefCertificateStatus());
		}

		if (notificationContextDTO.isPrefCertificateRevokeStatus()) {
			map.put("PREF_CERTIFICATE_REVOKE_STATUS", true);
		}

		if (notificationContextDTO.getPromotionalNotification() != null) {
			map.put("PROMOTIONAL_NOTIFICATION", notificationContextDTO.getPromotionalNotification());
		}

		if (notificationContextDTO.isSigningAuth()) {
			map.put("SIGNING_AUTH", notificationContextDTO.isSigningAuth());
			map.put("IS_SIGN_HASH", notificationContextDTO.isSignHash());
			map.put("IS_BOTH_PINS", notificationContextDTO.isBothPins());
			map.put("IS_SIGN_PINS", notificationContextDTO.isSignPins());
		}

		if (notificationContextDTO.getPrefDocumentSigned() != null) {
			map.put("PREF_DOCUMENT_SIGNED",
					(new JSONObject(notificationContextDTO.getPrefDocumentSigned())).toString());
		}

		if (notificationContextDTO.getPrefPaymentStatus() != null
				|| notificationContextDTO.getPrefTransactionId() != null) {
			map.put("PREF_PAYMENT_STATUS",
					(new JSONObject(notificationContextDTO.getPrefPaymentStatus())).toString());
			map.put("PREF_TRANSACTION_ID",
					(new JSONObject(notificationContextDTO.getPrefTransactionId())).toString());
		}

		if (notificationContextDTO.getPrefOrgLink() != null) {
			map.put("PREF_ORG_LINK", (new JSONObject(notificationContextDTO.getPrefOrgLink())).toString());
		}

		if (notificationContextDTO.getPrefBeneficiaryLink() != null) {
			map.put("PREF_BENEFICIARY_LINK",
					(new JSONObject(notificationContextDTO.getPrefBeneficiaryLink())).toString());
		}

		if (notificationContextDTO.getPrefOdooNotification() != null) {
			map.put("PREF_ODOO_NOTIFICATION",
					(new JSONObject(notificationContextDTO.getPrefOdooNotification())).toString());
		}

		if (notificationContextDTO.getPrefVisaAuthority() != null) {
			map.put("PREF_VISA_AUTHORITY",
					(new JSONObject(notificationContextDTO.getPrefVisaAuthority())).toString());
		}

		if (notificationContextDTO.getPrefImmigrationAuthority() != null) {
			map.put("PREF_IMMIGRATION_AUTHORITY",
					(new JSONObject(notificationContextDTO.getPrefImmigrationAuthority())).toString());
		}

		return map;
	}
}
