package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberPersonalDocument;

@Repository
public interface SubscriberPersonalDocumentRepo extends JpaRepository<OnbSubscriberPersonalDocument, Integer>{
	
	OnbSubscriberPersonalDocument findBySubscriberUniqueId(String suid);

}
