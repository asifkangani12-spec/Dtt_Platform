package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.dtt.organization.model.OrgOrganizationService;

public interface OrganizationServiceRepository extends JpaRepository<OrgOrganizationService, Integer> {

	List<OrgOrganizationService> findByOrganizationUid(String organizationUid);

	@Modifying
	@Transactional(rollbackFor = Exception.class)
	@Query("DELETE FROM OrgOrganizationService o WHERE o.organizationUid = :organizationUid")
	int deleteOrganizationServiceEmailById(String organizationUid);
}
