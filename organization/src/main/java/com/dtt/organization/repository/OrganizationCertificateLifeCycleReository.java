package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgOrganizationCertificateLifeCycle;

@Repository
public interface OrganizationCertificateLifeCycleReository extends JpaRepository<OrgOrganizationCertificateLifeCycle, Integer> {

}
