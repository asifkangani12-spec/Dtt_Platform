/**
 * 
 */
package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberFcmToken;

/**
 * @author Raxit Dubey
 *
 */
@Repository
public interface SubscriberFcmTokenRepoIface extends JpaRepository<OnbSubscriberFcmToken, Integer>{

	OnbSubscriberFcmToken findBysubscriberUid(String suid);

	@Query("SELECT s.fcmToken FROM OnbSubscriberFcmToken s WHERE s.subscriberUid = :subscriberUid")
	String getFcmTokenBySubscriberUid(@Param("subscriberUid") String subscriberUid);
	
}
