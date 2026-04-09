package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberCertificatePinHistory;
@Repository
public interface SubscriberCertPinHistoryRepoIface extends JpaRepository<OnbSubscriberCertificatePinHistory,String>{
	OnbSubscriberCertificatePinHistory findBysubscriberUid(String uid);
}