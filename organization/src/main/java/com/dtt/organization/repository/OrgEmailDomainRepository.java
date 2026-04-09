package com.dtt.organization.repository;

import com.dtt.organization.model.OrgOrganizationEmailDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgEmailDomainRepository extends JpaRepository<OrgOrganizationEmailDomain, Integer> {

    OrgOrganizationEmailDomain findByOrganizationUid(String organizationUid);

}
