package com.dtt.organization.repository;


import com.dtt.organization.model.OrgBeneficiaryValidity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OrgBeneficiaryValidityRepository extends JpaRepository<OrgBeneficiaryValidity, Integer> {

    @Query("SELECT b FROM OrgBeneficiaryValidity b WHERE b.beneficiaryId = ?1")
    List<OrgBeneficiaryValidity> findAllBeneficiaryValiditybybenefeciaryId(int beneficiaryId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrgBeneficiaryValidity b WHERE b.beneficiaryId = ?1")
    int deleteAllBeneficiaryValiditybybenefeciaryId(int beneficiaryId);


    @Modifying
    @Query("UPDATE OrgBeneficiaryValidity b SET b.status = 'INACTIVE' WHERE b.beneficiaryId = ?1")
    int changeStatusByBeneficiaryId(int beneficiaryId);


}
