package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriber;

@Repository
public interface OrgSubscriberRepository extends JpaRepository<OrgSubscriber, String> {


	@Query("SELECT s.subscriberUid FROM OrgSubscriber s WHERE s.emailId = ?1")
	String findByemailId(String emailId);

	/**
	 * Gets the subscriber list by email id.
	 *
	 * @param emailId the email id
	 * @return the subscriber list by email id
	 */

	@Query("SELECT s.emailId FROM OrgSubscriber s WHERE s.emailId LIKE CONCAT('%', ?1, '%')")
	List<String> getSubscriberListByEmailId(String emailId);


	@Query("SELECT s FROM OrgSubscriber s WHERE s.subscriberUid = ?1")
    OrgSubscriber getSubscriberEmail(String suid);


	@Query("SELECT s FROM OrgSubscriber s WHERE s.emailId = ?1")
    OrgSubscriber getSubscriber(String emailId);

	/**
	 * Find bysubscriber uid.
	 *
	 * @param subscriberUniqueId the subscriber unique id
	 * @return the subscriber
	 */

	@Query("SELECT s FROM OrgSubscriber s WHERE s.subscriberUid = ?1")
    OrgSubscriber findBysubscriberUid(String subscriberUniqueId);

	@Query("SELECT s FROM OrgSubscriber s " +
			"WHERE s.nationalId = ?1 " +
			"   OR s.idDocNumber = ?2 " +
			"   OR s.emailId = ?3 " +
			"   OR s.mobileNumber = ?4")
	List<OrgSubscriber> findSubscriberDetails(String ninNumber,
                                              String passportNumber,
                                              String ugPassEmailId,
                                              String mobileNumber);




}
