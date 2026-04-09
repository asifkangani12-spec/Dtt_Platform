package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSusbcriberDetailsView;

@Repository
public interface SusbcriberDetailsViewRepo extends JpaRepository<OnbSusbcriberDetailsView, Integer>{

	OnbSusbcriberDetailsView findBysubscriberUid(String subscriberUniqueId);

	@Query("SELECT s.subscriberUid FROM OnbSusbcriberDetailsView s WHERE s.mobileNumber = ?1")
	String findBymobileNumber(String mobileNumber);

	@Query("SELECT s.subscriberUid FROM OnbSusbcriberDetailsView s WHERE s.eMail = ?1")
	String findByemailId(String emailId);


}
