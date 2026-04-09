package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dtt.organization.model.OrgOrganizationDirectors;

@Repository
public interface OrganizationDirectorsRepository extends JpaRepository<OrgOrganizationDirectors, Integer>{

	List<OrgOrganizationDirectors> findByOrganizationUid(String organizationUid);

	@Modifying
	@Query("DELETE FROM OrgOrganizationDirectors o WHERE o.organizationUid = ?1")
	@Transactional(rollbackFor = Exception.class)
	int deleteOrganizationDirectorsEmailById(String organizationUid);
}
