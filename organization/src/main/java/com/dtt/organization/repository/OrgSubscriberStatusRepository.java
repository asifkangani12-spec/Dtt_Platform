package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriberStatusModel;

@Repository
public interface OrgSubscriberStatusRepository extends JpaRepository<OrgSubscriberStatusModel, String>{

	OrgSubscriberStatusModel findBysubscriberUid(String subscriberUniqueId);

}
