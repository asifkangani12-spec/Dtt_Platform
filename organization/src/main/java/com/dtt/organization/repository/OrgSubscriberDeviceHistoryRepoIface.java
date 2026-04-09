package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSubscriberDeviceHistory;


@Repository
public interface OrgSubscriberDeviceHistoryRepoIface extends JpaRepository<OrgSubscriberDeviceHistory, Integer>{

    OrgSubscriberDeviceHistory findTop1ByDeviceUidOrderByUpdatedDateDesc(String deviceUid);
    default OrgSubscriberDeviceHistory findBydeviceUid(String deviceUid) {
        return findTop1ByDeviceUidOrderByUpdatedDateDesc(deviceUid);
    }

    @Query("SELECT s FROM OrgSubscriberDeviceHistory s WHERE s.deviceUid = :deviceUid AND s.subscriberUid = :subscriberUid")
    List<OrgSubscriberDeviceHistory> findByDeviceUidAndSubscriberUid(@Param("deviceUid") String deviceUid, @Param("subscriberUid") String subscriberUid);

    OrgSubscriberDeviceHistory findTop1ByDeviceUidAndSubscriberUidOrderByUpdatedDateDesc(String deviceUid, String subscriberUid);
    default OrgSubscriberDeviceHistory findByDeviceUidAndSubUid(String deviceUid, String subscriberUid) {
        return findTop1ByDeviceUidAndSubscriberUidOrderByUpdatedDateDesc(deviceUid, subscriberUid);
    }




}