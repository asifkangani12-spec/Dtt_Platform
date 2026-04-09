package com.dtt.organization.repository;

import com.dtt.organization.model.OrgSubscriberPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgSubscriberPreferencesRepo extends JpaRepository<OrgSubscriberPreferences,Integer> {

    @Query("SELECT s FROM OrgSubscriberPreferences s WHERE s.suid = ?1")
   OrgSubscriberPreferences getBySubUid(String uid);
}
