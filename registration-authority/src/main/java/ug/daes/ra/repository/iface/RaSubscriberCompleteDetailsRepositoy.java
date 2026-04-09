/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberCompleteDetails;

/**
 * The Interface SubscriberCertificateDataRepository.
 */
@Repository
public interface RaSubscriberCompleteDetailsRepositoy extends JpaRepository<RASubscriberCompleteDetails, String> {

	/**
	 * Find by subscriber unique id.
	 *
	 * @param subscriberUniqueId
	 *            the subscriber unique id
	 * @return the subscriber complete details
	 */
	RASubscriberCompleteDetails findBysubscriberUid(String subscriberUniqueId);
	
	
}
