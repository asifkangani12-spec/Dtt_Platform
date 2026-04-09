package com.dtt.organization.repository;


import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriberView;

@Transactional
@Repository
public interface OrgSubscriberViewRepository extends JpaRepository<OrgSubscriberView, String>{

	@Query("SELECT s FROM OrgSubscriberView s WHERE s.subscriberUid = ?1")
    OrgSubscriberView getSubscriberDetailsBySuid(String suid);

	@Query("SELECT s FROM OrgSubscriberView s WHERE s.emailId = ?1")
    OrgSubscriberView findByUgpassMail(String mail);

	@Query("SELECT s FROM OrgSubscriberView s WHERE s.idDocNumber = ?1")
    OrgSubscriberView findByIdDocNumber(String id);

	@Query("SELECT s FROM OrgSubscriberView s WHERE s.mobileNumber = ?1")
    OrgSubscriberView findByMobile(String mobile);
}
