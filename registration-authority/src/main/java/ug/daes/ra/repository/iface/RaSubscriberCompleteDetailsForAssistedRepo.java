package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberCompleteDetailsForAssisted;

@Repository
public interface RaSubscriberCompleteDetailsForAssistedRepo extends JpaRepository<RASubscriberCompleteDetailsForAssisted, String> {
	
	/**
	 * Find by subscriber unique id.
	 *
	 * @param subscriberUniqueId
	 *            the subscriber unique id
	 * @return the subscriber complete details
	 */
	RASubscriberCompleteDetailsForAssisted findBysubscriberUid(String subscriberUniqueId);

}
