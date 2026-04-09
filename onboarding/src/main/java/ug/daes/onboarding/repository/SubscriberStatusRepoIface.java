/**
 * 
 */
package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberStatus;

/**
 * @author Raxit Dubey
 *
 */
@Repository
public interface SubscriberStatusRepoIface extends JpaRepository<OnbSubscriberStatus, Integer>{


	@Query("SELECT s FROM OnbSubscriberStatus s WHERE s.subscriberUid = :suid")
    OnbSubscriberStatus findBysubscriberUid(@Param("suid") String suid);
	
}
