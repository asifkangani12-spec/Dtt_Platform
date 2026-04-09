package ug.daes.ra.repository.iface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberDeviceHistory;


@Repository
public interface RaSubscriberDeviceHistoryRepoIface extends JpaRepository<RASubscriberDeviceHistory, Integer>{
    @Query("SELECT s FROM RASubscriberDeviceHistory s WHERE s.deviceUid = :deviceId ORDER BY s.updatedDate DESC")
    RASubscriberDeviceHistory findBydeviceUid(@Param("deviceId") String deviceId);

    @Query("SELECT s FROM RASubscriberDeviceHistory s WHERE s.deviceUid = :deviceUid AND s.subscriberUid = :subscriberUid")
    List<RASubscriberDeviceHistory> findByDeviceUidAndSubscriberUid(@Param("deviceUid") String deviceUid, @Param("subscriberUid") String subscriberUid);



}
