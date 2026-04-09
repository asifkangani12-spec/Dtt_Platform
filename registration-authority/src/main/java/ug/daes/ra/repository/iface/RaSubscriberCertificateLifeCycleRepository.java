/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.repository.iface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberCertificateLifeCycle;

/**
 * The Interface SubscriberCertificateDataRepository.
 */
@Repository
public interface RaSubscriberCertificateLifeCycleRepository
		extends JpaRepository<RASubscriberCertificateLifeCycle, Integer> {

	/**
	 * Find by subscriber unique id.
	 *
	 * @param subscriberUniqueId
	 *            the subscriber unique id
	 * @return the list
	 */
	List<RASubscriberCertificateLifeCycle> findBysubscriberUniqueId(String subscriberUniqueId);
	
	List<RASubscriberCertificateLifeCycle> findBySubscriberUniqueIdAndCertificateStatus(String subscriberUniqueId , String status);
}
