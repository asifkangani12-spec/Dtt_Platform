package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ug.daes.onboarding.model.OnbSubscriberPreferences;

@Repository
public interface SubscriberPreferencesRepo extends JpaRepository<OnbSubscriberPreferences,Integer> {

    @Query("SELECT s FROM OnbSubscriberPreferences s WHERE s.suid = ?1")
    OnbSubscriberPreferences getBySubUid(String uid);
}
