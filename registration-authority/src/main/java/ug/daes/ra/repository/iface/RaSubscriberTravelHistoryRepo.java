package ug.daes.ra.repository.iface;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ug.daes.ra.model.RASubscriberTravelHistory;

import java.util.List;

@Repository
public interface RaSubscriberTravelHistoryRepo extends JpaRepository<RASubscriberTravelHistory, Integer> {
    @Query("SELECT COUNT(s) FROM RASubscriberTravelHistory s WHERE s.immigrationStatus = ?1 AND s.immigrationType = ?2 AND s.passport = ?3")
    int noOfEntries(String status, String type, String passport);

    @Query("SELECT s FROM RASubscriberTravelHistory s WHERE s.immigrationStatus = ?1 AND s.immigrationType = ?2 AND s.passport = ?3 ORDER BY s.travelDate DESC")
    List<RASubscriberTravelHistory> findLastTravelDate(String status, String type, String passport);

    @Query("SELECT s FROM RASubscriberTravelHistory s WHERE s.immigrationStatus = 'CLEARED' AND s.passport = ?1 ORDER BY s.travelDate DESC")
    List<RASubscriberTravelHistory> findLatestCleared(String passport);}
