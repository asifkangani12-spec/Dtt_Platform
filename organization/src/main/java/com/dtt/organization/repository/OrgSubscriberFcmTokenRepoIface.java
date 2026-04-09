package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriberFcmToken;



@Repository
public interface OrgSubscriberFcmTokenRepoIface extends JpaRepository<OrgSubscriberFcmToken, Integer>{

	OrgSubscriberFcmToken findBysubscriberUid(String suid);
	
}
