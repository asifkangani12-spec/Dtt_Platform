package ug.daes.onboarding.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ug.daes.onboarding.model.OnbSubscriberConsents;

@Repository
public interface SubscriberConsentsRepo extends JpaRepository<OnbSubscriberConsents,Integer> {
    @Query("SELECT sc FROM OnbSubscriberConsents sc WHERE sc.suid = ?1 AND sc.consentId = ?2")
    OnbSubscriberConsents findSubscriberConsentBySuidAndConsentId(String suid, int consentId);
}
