package com.dtt.organization.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriberOnboardingData;


@Repository
public interface OrgSubscriberOnboardingDataRepoIface extends JpaRepository<OrgSubscriberOnboardingData, Integer> {



	OrgSubscriberOnboardingData findTop1BySubscriberUidOrderByCreatedDateDesc(String uid);

	default OrgSubscriberOnboardingData getBySubUid(String uid) {
		return findTop1BySubscriberUidOrderByCreatedDateDesc(uid);
	}

}
