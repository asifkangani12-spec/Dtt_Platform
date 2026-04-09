package ug.daes.ra.repository.iface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RAOrganizationCertificates;

@Repository
public interface RaOrganizationCertificatesRepository extends JpaRepository<RAOrganizationCertificates, String> {


	@Query("SELECT i FROM RAOrganizationCertificates i WHERE i.certificateStatus = ?1 AND i.organizationUid = ?2")
    RAOrganizationCertificates findByCertificateStatusAndOrganizationUniqueId(String paramString1, String paramString2);


	@Query("SELECT i FROM RAOrganizationCertificates i WHERE i.certificateStatus = ?1 AND i.organizationUid = ?2")
	List<RAOrganizationCertificates> findByCertificateStatusAndOrganizationUid(String paramString1, String paramString2);


	@Query("SELECT i FROM RAOrganizationCertificates i WHERE i.certificateEndDate <= CURRENT_DATE AND i.certificateStatus = 'ACTIVE'")
	List<RAOrganizationCertificates> findByCertificateStatusExpired();


	@Query("SELECT i.certificateData FROM RAOrganizationCertificates i WHERE i.organizationUid = ?1 AND i.certificateStatus = 'ACTIVE'")
	String getOrgCertData(String ouid);
}
