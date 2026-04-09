package com.dtt.organization.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgOrganizationCertificates;


@Repository
public interface OrganizationCertificatesRepository extends JpaRepository<OrgOrganizationCertificates, String> {


	@Query("SELECT o FROM OrgOrganizationCertificates o WHERE o.organizationUid = ?1")
    OrgOrganizationCertificates findByorganizationOuid(String organizationUid);

	@Query("SELECT o FROM OrgOrganizationCertificates o WHERE o.certificateStatus = ?1 AND o.organizationUid = ?2")
    OrgOrganizationCertificates findByCertificateStatusAndOrganizationUniqueId(String certificateStatus, String organizationUid);

	@Query("SELECT o FROM OrgOrganizationCertificates o WHERE o.certificateStatus = ?1 AND o.organizationUid = ?2")
	List<OrgOrganizationCertificates> findByCertificateStatusAndOrganizationUid(String certificateStatus, String certificates);


	@Query("SELECT o FROM OrgOrganizationCertificates o WHERE o.certificateEndDate <= CURRENT_DATE AND o.certificateStatus = 'ACTIVE'")
	List<OrgOrganizationCertificates> findByCertificateStatusExpired();


	@Query("SELECT o FROM OrgOrganizationCertificates o WHERE o.organizationUid = ?1")
    OrgOrganizationCertificates getOrganizationDetails(String organizationUid);


	OrgOrganizationCertificates findTop1ByOrganizationUidOrderByCertificateStartDateDesc(String organizationUid);
	default OrgOrganizationCertificates findByorganizationUid(String organizationUid) {
		return findTop1ByOrganizationUidOrderByCertificateStartDateDesc(organizationUid);
	}

	// No query needed, JPA can derive
	OrgOrganizationCertificates findByTransactionReferenceId(String transactionReferenceId);

	@Query("SELECT o FROM OrgOrganizationCertificates o " +
			"WHERE o.certificateEndDate >= CURRENT_DATE " +
			"AND o.certificateEndDate <= :futureDate " +
			"AND o.certificateStatus = 'ACTIVE'")
	List<OrgOrganizationCertificates> findByOrganizationCertificateStatusExpired(@Param("futureDate") LocalDate futureDate);

	// Default method to call JPQL with fixed interval (100 days)
	default List<OrgOrganizationCertificates> findByOrganizationCertificateStatusExpired() {
		LocalDate futureDate = LocalDate.now().plusDays(100);
		return findByOrganizationCertificateStatusExpired(futureDate);
	}


}
