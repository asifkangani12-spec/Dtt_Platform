package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dtt.organization.model.OrgLicenseDeviceList;

@Repository
public interface OrgLicenseDeviceListRepo extends JpaRepository<OrgLicenseDeviceList, Integer> {

	@Query("SELECT l FROM OrgLicenseDeviceList l WHERE l.clientId = ?1")
    OrgLicenseDeviceList getLicenseDeviceDetails(String clientId);

	@Query("SELECT l FROM OrgLicenseDeviceList l WHERE l.deviceId = ?1 AND l.applicationName = ?2")
    OrgLicenseDeviceList getLicenseDevice(String oldDeviceId, String applicationName);


	@Query("SELECT l FROM OrgLicenseDeviceList l WHERE l.applicationName = ?1")
	List<OrgLicenseDeviceList> getLicenseDeviceList(String applicationName);


	@Modifying
	@Transactional
	@Query("DELETE FROM OrgLicenseDeviceList l WHERE l.deviceId = ?1 AND l.applicationName = ?2")
	int deleteRecordByDeviceId(String suid, String applicationName);


	@Query("SELECT l.deviceId FROM OrgLicenseDeviceList l WHERE l.clientId = ?1")
	List<String> getLicenseDeviceDetailsList(String clientId);

}
