package com.dtt.organization.repository;

import com.dtt.organization.model.OrgSoftwareLicenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgSoftwareLicensesRepository extends JpaRepository<OrgSoftwareLicenses,Integer> {



    @Query("SELECT s FROM OrgSoftwareLicenses s WHERE s.ouid = ?1 AND s.licenseType = ?2")
    OrgSoftwareLicenses findByOuidAndLicenseType(String ouid, String licenseType);


    @Query("SELECT s FROM OrgSoftwareLicenses s WHERE s.ouid = ?1")
    OrgSoftwareLicenses findByOrgUid(String ouid);



    @Query(value = "SELECT * FROM software_licenses WHERE ouid = ?1 " +
            "ORDER BY (CASE WHEN licence_status = 'APPLIED' THEN 0 ELSE 1 END), " +
            "updated_date_time DESC",
            nativeQuery = true)
    List<OrgSoftwareLicenses> findByOuid(String ouid);


    @Query(value = "SELECT * FROM software_licenses WHERE ouid = ?1 " +
            "ORDER BY (CASE WHEN licence_status = 'APPLIED' THEN 0 ELSE 1 END), " +
            "updated_date_time DESC",
            nativeQuery = true)
    OrgSoftwareLicenses findByOuidVG(String ouid);

    @Query("SELECT s FROM OrgSoftwareLicenses s ORDER BY s.updatedDateTime DESC")
    List<OrgSoftwareLicenses> getListForGenerateLicenses();




}
