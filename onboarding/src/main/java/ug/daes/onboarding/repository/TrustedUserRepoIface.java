package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ug.daes.onboarding.model.OnbTrustedUser;

import java.util.List;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TrustedUserRepoIface extends JpaRepository<OnbTrustedUser, Integer> {
	OnbTrustedUser findByemailId(String emailId);

	@Query("SELECT t.emailId FROM OnbTrustedUser t")
	List<String> getTrustedEmails();

	@Query("SELECT t FROM OnbTrustedUser t WHERE t.emailId = ?1")
    OnbTrustedUser getTrustedUserDratilsByEmail(String email);

}
