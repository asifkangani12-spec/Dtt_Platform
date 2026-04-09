package com.dtt.organization.repository;


import com.dtt.organization.model.OrgOrganizationPrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrganisationPrivilegesRepository extends JpaRepository<OrgOrganizationPrivileges,Integer> {

    @Query("SELECT o.privilege FROM OrgOrganizationPrivileges o WHERE o.organizationId = ?1 AND o.status = 'APPROVED'")
    List<String> fetchPrivilegesByOrganisation(String orgId);


    @Query("SELECT o FROM OrgOrganizationPrivileges o WHERE o.id = ?1")
    OrgOrganizationPrivileges fetchById(int id);


    @Query("SELECT o FROM OrgOrganizationPrivileges o WHERE o.organizationId = ?1 AND o.privilege = ?2 AND o.status <> 'REJECTED'")
    OrgOrganizationPrivileges fetchByPrivilege(String orgId, String privilege);


    @Query("SELECT o FROM OrgOrganizationPrivileges o ORDER BY o.createdOn DESC")
    List<OrgOrganizationPrivileges> getAll();


    @Modifying
    @Transactional
    @Query("UPDATE OrgOrganizationPrivileges o SET o.status = 'SUSPENDED' WHERE o.organizationId = ?1 AND o.status = 'APPROVED'")
    int updatePrivileges(String orgId);

}
