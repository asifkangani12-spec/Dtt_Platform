/**
 * 
 */
package com.dtt.repo;

import java.util.List;
import java.util.Optional;

import com.dtt.model.GenericSubscriberOnboardingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



/**
 * @author Raxit Dubey
 *
 */
@Repository
public interface GenericSubscriberOnboardingDataRepo extends JpaRepository<GenericSubscriberOnboardingData, Integer>{


	GenericSubscriberOnboardingData findBysubscriberUid(String suid);

	@Query("SELECT s FROM GenericSubscriberOnboardingData s WHERE s.subscriberUid = ?1 ORDER BY s.createdDate DESC")
	List<GenericSubscriberOnboardingData> getBySubUid(String uid); // You can fetch first in Java: list.get(0)

	@Query("SELECT s FROM GenericSubscriberOnboardingData s WHERE s.subscriberUid = ?1 ORDER BY s.createdDate DESC")
	List<GenericSubscriberOnboardingData> findLatestSubscriber(String suid);

	@Query("SELECT COUNT(s) FROM GenericSubscriberOnboardingData s WHERE s.optionalData1 = ?1 AND s.onboardingMethod = 'NIN'")
	int getOptionalData1(String optionalData1);

	@Query("SELECT DISTINCT s.subscriberUid FROM GenericSubscriberOnboardingData s WHERE s.optionalData1 = ?1")
	String getOptionalData1Subscriber(String optionalData1);

	@Query("SELECT s FROM GenericSubscriberOnboardingData s WHERE s.idDocNumber = ?1")
	List<GenericSubscriberOnboardingData> findSubscriberByDocId(String documentNumber);

	@Query("SELECT s FROM GenericSubscriberOnboardingData s")
	List<GenericSubscriberOnboardingData> getAllSelfies();



//	@Query("SELECT s FROM GenericSubscriberOnboardingData s WHERE s.idDocNumber = ?1 ORDER BY s.createdDate DESC")
//	SubscriberOnboardingData findSubscriberByDocIdLatestRecord(String documentNumber); // Pick first in code
//

	@Query("SELECT s FROM GenericSubscriberOnboardingData s WHERE s.idDocNumber = ?1 ORDER BY s.createdDate DESC")
	List<GenericSubscriberOnboardingData> findSubscriberByDocIdLatestRecord(String documentNumber);

	Optional<GenericSubscriberOnboardingData>
	findTopBySubscriberUidOrderBySubscriberOnboardingDataIdDesc(String subscriberUid);

}
