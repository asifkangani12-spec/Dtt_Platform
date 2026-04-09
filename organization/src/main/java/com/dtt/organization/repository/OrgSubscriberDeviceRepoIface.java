package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriberDevice;


@Repository
public interface OrgSubscriberDeviceRepoIface extends JpaRepository<OrgSubscriberDevice, Integer>{


	OrgSubscriberDevice findTop1ByDeviceUidOrderByUpdatedDateDesc(String deviceUid);
	default OrgSubscriberDevice findBydeviceUid(String deviceUid) {
		return findTop1ByDeviceUidOrderByUpdatedDateDesc(deviceUid);
	}

	@Query("SELECT s FROM OrgSubscriberDevice s WHERE s.deviceUid = :deviceUid AND s.deviceStatus = :status")
    OrgSubscriberDevice findBydeviceUidAndStatus(@Param("deviceUid") String deviceUid, @Param("status") String status);

	@Query("SELECT s FROM OrgSubscriberDevice s WHERE s.subscriberUid = :subscriberUid AND s.updatedDate = (SELECT MAX(s2.updatedDate) FROM OrgSubscriberDevice s2 WHERE s2.subscriberUid = :subscriberUid)")
    OrgSubscriberDevice getSubscriber(@Param("subscriberUid") String subscriberUid);
}
