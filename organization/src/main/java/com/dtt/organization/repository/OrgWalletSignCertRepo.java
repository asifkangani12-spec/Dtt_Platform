package com.dtt.organization.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrgWalletSignCertificate;

@Repository
public interface OrgWalletSignCertRepo extends JpaRepository<OrgWalletSignCertificate, String>{

	@Query("SELECT w FROM OrgWalletSignCertificate w WHERE w.certificateStatus = ?1 AND w.organizationUid = ?2")
    OrgWalletSignCertificate findByOrganizationId(String status, String organizationId);

	OrgWalletSignCertificate findTopByOrganizationUidOrderByUpdatedDateDesc(String organizationId);


	@Query("SELECT w FROM OrgWalletSignCertificate w WHERE w.certificateEndDate <= CURRENT_DATE AND w.certificateStatus = 'ACTIVE' AND w.organizationUid = ?1")
    OrgWalletSignCertificate findByCertificateStatusExpiredVG(String ouid);

	OrgWalletSignCertificate findByTransactionReferenceId(String walletTransactionRefId);

}
