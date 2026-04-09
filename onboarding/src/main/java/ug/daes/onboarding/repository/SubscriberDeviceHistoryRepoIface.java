package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberDeviceHistory;

import java.util.List;

@Repository
public interface SubscriberDeviceHistoryRepoIface extends JpaRepository<OnbSubscriberDeviceHistory, Integer>{


    @Query("SELECT s FROM OnbSubscriberDeviceHistory s WHERE s.deviceUid = ?1 ORDER BY s.updatedDate DESC")
    List<OnbSubscriberDeviceHistory> findBydeviceUid(String deviceId);

    @Query("SELECT s FROM OnbSubscriberDeviceHistory s WHERE s.deviceUid = ?1 AND s.subscriberUid = ?2")
    List<OnbSubscriberDeviceHistory> findByDeviceUidAndSubscriberUid(String deviceUid, String subscriberUid);

    @Query("SELECT s FROM OnbSubscriberDeviceHistory s WHERE s.deviceUid = ?1 AND s.subscriberUid = ?2 ORDER BY s.updatedDate DESC")
    List<OnbSubscriberDeviceHistory> findByDeviceUidAndSubUid(String deviceUid, String subscriberUid);

    @Query("SELECT s FROM OnbSubscriberDeviceHistory s WHERE s.subscriberUid = ?1 ORDER BY s.updatedDate DESC")
    List<OnbSubscriberDeviceHistory> findBySubscriberUid(String subUID);

    @Query("SELECT s FROM OnbSubscriberDeviceHistory s WHERE s.subscriberUid = ?1 ORDER BY s.createdDate DESC")
    List<OnbSubscriberDeviceHistory> findSubscriberDeviceHistory(String subUID);
    
}
