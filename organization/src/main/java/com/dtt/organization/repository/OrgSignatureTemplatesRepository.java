package com.dtt.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgSignatureTemplates;

@Repository
public interface OrgSignatureTemplatesRepository extends JpaRepository<OrgSignatureTemplates, Integer> {

	@Query("SELECT st FROM OrgSignatureTemplates st")
	List<OrgSignatureTemplates> getAllTemplates();

	@Query("SELECT st FROM OrgSignatureTemplates st WHERE st.id = :id")
    OrgSignatureTemplates getTemplateImageById(int id);
}
