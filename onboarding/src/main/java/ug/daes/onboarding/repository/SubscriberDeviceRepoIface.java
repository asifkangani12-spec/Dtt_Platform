/**
 * 
 */
package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ug.daes.onboarding.model.OnbSubscriberDevice;

import java.util.List;

/**
 * @author Raxit Dubey
 *
 */

@Repository
public interface SubscriberDeviceRepoIface extends JpaRepository<OnbSubscriberDevice, Integer>{

	List<OnbSubscriberDevice> findBysubscriberUid(String suid);


	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.deviceUid = :deviceId ORDER BY sd.updatedDate DESC")
	List<OnbSubscriberDevice> findBydeviceUid(@Param("deviceId") String deviceId);

	OnbSubscriberDevice findTopByDeviceUidOrderByUpdatedDateDesc(String deviceUid);

	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.deviceUid = :deviceId ORDER BY sd.updatedDate DESC")
	List<OnbSubscriberDevice> findBydeviceDetails(@Param("deviceId") String deviceId);

	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.deviceUid = :deviceId ORDER BY sd.updatedDate DESC")
	List<OnbSubscriberDevice> findDeviceDetailsById(@Param("deviceId") String deviceId);

	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.deviceUid = :deviceId AND sd.deviceStatus = :status")
    OnbSubscriberDevice findBydeviceUidAndStatus(@Param("deviceId") String deviceId, @Param("status") String status);

	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.deviceUid = :deviceId ORDER BY sd.deviceStatus DESC")
	List<OnbSubscriberDevice> findByDeviceUidDetails(@Param("deviceId") String deviceId);

	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.subscriberUid = :suid AND sd.updatedDate = (" +
			"SELECT MAX(s.updatedDate) FROM OnbSubscriberDevice s WHERE s.subscriberUid = :suid)")
    OnbSubscriberDevice getSubscriber(@Param("suid") String suid);

	@Modifying
	@Transactional
	@Query("UPDATE OnbSubscriberDevice sd SET sd.deviceUid = :deviceUid, sd.deviceStatus = :deviceStatus, " +
			"sd.updatedDate = :updatedDate WHERE sd.id = :subscriberDeviceId")
	int updateSubscriber(@Param("deviceUid") String deviceUid,
						 @Param("deviceStatus") String deviceStatus,
						 @Param("updatedDate") String updatedDate,
						 @Param("subscriberDeviceId") int subscriberDeviceId);

	@Query("SELECT sd FROM OnbSubscriberDevice sd WHERE sd.subscriberUid = :suid")
	List<OnbSubscriberDevice> getSubscriberDeviceStatus(@Param("suid") String suid);
	
}
