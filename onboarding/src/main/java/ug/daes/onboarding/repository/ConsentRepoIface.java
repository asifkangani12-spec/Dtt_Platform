package ug.daes.onboarding.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbConsent;

@Repository
public interface ConsentRepoIface extends JpaRepository<OnbConsent, Integer> {

	@Query("SELECT c FROM OnbConsent c")
    OnbConsent getConsent();

	@Query("SELECT c FROM OnbConsent c WHERE c.status = 'ACTIVE'")
    OnbConsent getActiveConsent();

	// 3. Find by consentId (Spring Data JPA auto method)
	OnbConsent findByConsentId(int id);

	@Query("UPDATE OnbConsent c SET c.consent = :consent, c.consentType = :consentType, c.status = :status WHERE c.consentId = :consentId")
	void upDateConsent(int consentId, String consent, String consentType, String status);

	// 5. Update status to ACTIVE
	@Modifying
	@Transactional
	@Query("UPDATE OnbConsent c SET c.status = :status WHERE c.consentId = :consentId")
	void updateConsentStatusActive(@Param("consentId") int consentId, @Param("status") String status);

	// Update status to INACTIVE
	@Modifying
	@Transactional
	@Query("UPDATE OnbConsent c SET c.status = :status WHERE c.consentId = :consentId")
	void updateConsentStatusInactive(@Param("consentId") int consentId, @Param("status") String status);
}
