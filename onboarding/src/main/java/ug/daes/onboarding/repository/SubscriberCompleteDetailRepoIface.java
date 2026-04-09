package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ug.daes.onboarding.model.OnbSubscriberCertificateDetails;
import ug.daes.onboarding.model.OnbSubscriberCompleteDetail;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface SubscriberCompleteDetailRepoIface extends JpaRepository<OnbSubscriberCompleteDetail, Integer>{

	@Query("SELECT s FROM OnbSubscriberCompleteDetail s " +
			"WHERE s.createdDate >= :startDate AND s.createdDate <= :endDate " +
			"ORDER BY s.createdDate DESC")
	List<OnbSubscriberCertificateDetails> getSubscriberReports(@Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate);


	// 2. Count active devices by email or mobile number
	@Query("SELECT COUNT(s) FROM OnbSubscriberCompleteDetail s WHERE " +
			"(s.deviceStatus = ?1 AND s.emailId = ?2) OR (s.deviceStatus = ?1 AND s.mobileNumber = ?3)")
	int getActiveDeviceCountStatusByEmailAndMobileNo(String status, String email, String mobileNo);

	// 3. Get all active subscribers by status
	@Query("SELECT s FROM OnbSubscriberCompleteDetail s WHERE s.subscriberStatus = ?1 ORDER BY s.createdDate DESC")
	List<OnbSubscriberCompleteDetail> getAllActiveSubscribersDetails(String status);


}
