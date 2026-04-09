package com.dtt.organization.repository;

import com.dtt.organization.model.OrgOrganizationStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrganizationStatusRepository extends JpaRepository<OrgOrganizationStatusModel, Integer> {
	
	
	OrgOrganizationStatusModel findByorganizationUid(String organizationUniqueId);

}
