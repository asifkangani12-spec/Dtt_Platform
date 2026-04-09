package ug.daes.onboarding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberCertificateDetails;

@Repository
public interface SubscriberCertificateDetailsRepoIface extends JpaRepository<OnbSubscriberCertificateDetails, String>{

	@Query("SELECT scd FROM OnbSubscriberCertificateDetails scd " +
			"WHERE FUNCTION('TO_DATE', scd.createdDate, 'YYYY-MM-DD') >= FUNCTION('TO_DATE', :startDate, 'YYYY-MM-DD') " +
			"AND   FUNCTION('TO_DATE', scd.createdDate, 'YYYY-MM-DD') <= FUNCTION('TO_DATE', :endDate, 'YYYY-MM-DD') " +
			"ORDER BY FUNCTION('TO_DATE', scd.createdDate, 'YYYY-MM-DD') DESC")
	List<OnbSubscriberCertificateDetails> getSubscriberReports(@Param("startDate") String startDate,
                                                               @Param("endDate") String endDate);

}
