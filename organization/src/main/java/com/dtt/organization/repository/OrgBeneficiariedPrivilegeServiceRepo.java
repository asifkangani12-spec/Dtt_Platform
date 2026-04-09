package com.dtt.organization.repository;

import com.dtt.organization.model.OrgBeneficiariedPrivilegeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgBeneficiariedPrivilegeServiceRepo extends JpaRepository<OrgBeneficiariedPrivilegeService, Integer> {

    @Query("SELECT p FROM OrgBeneficiariedPrivilegeService p WHERE p.status = 'ACTIVE'")
    List<OrgBeneficiariedPrivilegeService> findPrivilegeByStatus();

    @Query("SELECT p FROM OrgBeneficiariedPrivilegeService p WHERE p.privilegeId = ?1")
    OrgBeneficiariedPrivilegeService findPrivilegeById(int id);
}
