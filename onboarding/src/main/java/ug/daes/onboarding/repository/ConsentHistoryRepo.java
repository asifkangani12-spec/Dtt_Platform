package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ug.daes.onboarding.model.OnbConsentHistory;

import java.util.List;


@Repository
public interface ConsentHistoryRepo extends JpaRepository<OnbConsentHistory, Integer> {


    @Query("SELECT c FROM OnbConsentHistory c WHERE c.consentRequired = true ORDER BY c.createdOn DESC")
    List<OnbConsentHistory> findLatestConsent();


    OnbConsentHistory findTopByConsentIdOrderByCreatedOnDesc(int id);

}
