/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021,
 * All rights reserved.
 */
package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberDevice;

import java.util.List;


/**
 * The Interface SubscriberDeviceRepoIface.
 */
@Repository
public interface RaSubscriberDeviceRepository extends JpaRepository<RASubscriberDevice, Integer> {

	@Query("SELECT sd FROM RASubscriberDevice sd WHERE sd.deviceUid = ?1 ORDER BY sd.updatedDate DESC")
	List<RASubscriberDevice> findBydeviceUid(String deviceId);

	RASubscriberDevice findBysubscriberUid(String subscriberUid);

	@Query("SELECT sd FROM RASubscriberDevice sd WHERE sd.deviceUid = ?1 AND sd.deviceStatus = ?2")
    RASubscriberDevice findBydeviceUidAndStatus(String deviceId, String status);

	@Query("SELECT sd FROM RASubscriberDevice sd WHERE sd.subscriberUid = ?1 AND sd.updatedDate = " +
			"(SELECT MAX(s.updatedDate) FROM RASubscriberDevice s WHERE s.subscriberUid = ?1)")
    RASubscriberDevice getSubscriber(String suid);

}
