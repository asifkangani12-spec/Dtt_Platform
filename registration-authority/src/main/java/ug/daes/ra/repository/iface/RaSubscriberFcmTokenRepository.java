/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberFcmToken;

/**
 * The Interface SubscriberFcmTokenRepoIface.
 */
@Repository
public interface RaSubscriberFcmTokenRepository extends JpaRepository<RASubscriberFcmToken, Integer> {

	/**
	 * Find bysubscriber uid.
	 *
	 * @param subscriberUid the subscriber uid
	 * @return the string
	 */
	@Query("SELECT sft.fcmToken FROM RASubscriberFcmToken sft WHERE sft.subscriberUid = ?1")
	String findBysubscriberUid(String subscriberUid);
}
