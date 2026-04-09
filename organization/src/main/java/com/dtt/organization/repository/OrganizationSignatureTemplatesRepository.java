package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dtt.organization.model.OrgOrganizationSignatureTemplates;

@Repository
public interface OrganizationSignatureTemplatesRepository extends JpaRepository<OrgOrganizationSignatureTemplates, Integer>{


	List<OrgOrganizationSignatureTemplates> findByOrganizationUid(String organizationUid);


	@Modifying
	@Transactional
	@Query("DELETE FROM OrgOrganizationSignatureTemplates o WHERE o.organizationUid = :organizationUid")
	int deleteSignatureTemplateByOrgId(@Param("organizationUid") String organizationUid);

	@Query("SELECT o FROM OrgOrganizationSignatureTemplates o WHERE o.organizationUid = :orgId ORDER BY o.type DESC")
	List<OrgOrganizationSignatureTemplates> getOrgSignatureTemplates(@Param("orgId") String orgId);

	@Query("SELECT o FROM OrgOrganizationSignatureTemplates o WHERE o.organizationUid = :orgId")
	List<OrgOrganizationSignatureTemplates> getUserSignatureTemplatesDetails(@Param("orgId") String orgId);


	@Query("SELECT o FROM OrgOrganizationSignatureTemplates o WHERE o.organizationUid = :orgId AND o.type = :type")
    OrgOrganizationSignatureTemplates getOrgSignatureDetailsByType(@Param("orgId") String orgId, @Param("type") String type);


}
