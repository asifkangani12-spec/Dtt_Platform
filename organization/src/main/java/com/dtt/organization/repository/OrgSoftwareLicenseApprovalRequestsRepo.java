package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSoftwareLicenseApprovalRequests;


@Repository
public interface OrgSoftwareLicenseApprovalRequestsRepo extends JpaRepository<OrgSoftwareLicenseApprovalRequests,Integer>{

    @Query("SELECT s FROM OrgSoftwareLicenseApprovalRequests s")
    OrgSoftwareLicenseApprovalRequests getSoftwareLicenseApprovalRequests();


    @Query("SELECT s FROM OrgSoftwareLicenseApprovalRequests s WHERE s.ouid = ?1 AND s.licenseType = ?2 AND s.appid = ?3")
    OrgSoftwareLicenseApprovalRequests getSoftwareDetails(String orguid, String type, String softwareName);

}
