package com.dtt.organization.repository;

import com.dtt.organization.model.OrgOrganizationDetailsForClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationDetailsForClientRepoIface extends JpaRepository<OrgOrganizationDetailsForClient,String> {

    @Query("SELECT o FROM OrgOrganizationDetailsForClient o WHERE o.clientId = ?1")
    OrgOrganizationDetailsForClient getOrganizationDetails(String clientId);

    @Query("SELECT o FROM OrgOrganizationDetailsForClient o WHERE o.applicationName = ?1 AND o.organizationUid = ?2")
    OrgOrganizationDetailsForClient getClientId(String appName, String ouid);

    @Query("SELECT o FROM OrgOrganizationDetailsForClient o WHERE o.applicationName = ?1")
    OrgOrganizationDetailsForClient getOrganizationClientDetails(String appName);

}