package com.dtt.organization.repository;

import com.dtt.organization.model.OrgTrustedStakeholder;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface OrgTrustedStakeholdersRepository extends JpaRepository<OrgTrustedStakeholder, Integer> {

	OrgTrustedStakeholder findByReferenceId(String referenceId);

	@Query("SELECT t FROM OrgTrustedStakeholder t")
	List<OrgTrustedStakeholder> getAllTrustedStakeHolder();

	@Query("SELECT t FROM OrgTrustedStakeholder t WHERE t.referredBy = ?1")
	List<OrgTrustedStakeholder> getAllTrustedStakeHolderByOrgId(String organizationId);

	@Query("SELECT t FROM OrgTrustedStakeholder t WHERE t.stakeholderType = ?1")
	List<OrgTrustedStakeholder> getAllTrustedStakeHolderByStakeHolderType(String stakeHolderType);

	@Query("SELECT t FROM OrgTrustedStakeholder t WHERE t.spocUgpassEmail = ?1")
	List<OrgTrustedStakeholder> getStakeHolderList(String spocEmail);

}