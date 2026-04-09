package ug.daes.ra.repository.iface;


import java.util.List;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASubscriberCertificates;

@Repository
public interface RaSubscriberCertificatesRepository extends JpaRepository<RASubscriberCertificates, String> {


	List<RASubscriberCertificates> findBysubscriberUniqueId(String subscriberRADataId);

	Optional<RASubscriberCertificates> findBycertificateSerialNumber(String serialNumber);

	@Query("SELECT sc FROM RASubscriberCertificates sc WHERE sc.certificateStatus = ?1 AND sc.subscriberUniqueId = ?2")
	List<RASubscriberCertificates> findByCertificateStatusAndsubscriberUniqueId(String certificateStatus,
                                                                                String subscriberUniqueId);

	Optional<RASubscriberCertificates> findFirstBySubscriberUniqueIdOrderByCreationDateDesc(String subscriberUid); // Corrected property name


	@Query("SELECT sc FROM RASubscriberCertificates sc WHERE sc.subscriberUniqueId = ?1 ORDER BY sc.creationDate DESC") // Corrected property name
	Optional<RASubscriberCertificates> findLatestBySubscriberUniqueIdJPQL(String subscriberUid);


	@Query("SELECT sc FROM RASubscriberCertificates sc WHERE sc.certificateStatus = ?1")
	List<RASubscriberCertificates> findByCertificateStatus(String certificateStatus);

	@Query("SELECT sc FROM RASubscriberCertificates sc WHERE sc.certificateStatus = ?1 AND sc.subscriberUniqueId = ?2 AND sc.certificateType = ?3")
    RASubscriberCertificates findByCertificateStatusAndsubscriberUniqueIdAndCertificateType(String certificateStatus,
                                                                                            String subscriberUniqueId, String certificateType);


	@Query("SELECT COUNT(sc) FROM RASubscriberCertificates sc")
	int getAllCertificateCount();


	@Query("SELECT COUNT(sc.certificateType) FROM RASubscriberCertificates sc WHERE sc.certificateStatus = 'ACTIVE'")
	int getIssuedCertificatesCount();


	@Query("SELECT sc FROM RASubscriberCertificates sc WHERE sc.certificateEndDate <= CURRENT_TIMESTAMP AND sc.certificateStatus = 'ACTIVE'")
	List<RASubscriberCertificates> findByCertificateStatusExpired();

	@Query("""
    SELECT 
        scv.subscriberCount,
        scv.activeSubscriberCount,
        scv.inactiveSubscriberCount,
        scv.disableSubscriberCount,
        scv.certRevokeSubscriberCount,
        scv.certExpiredSubscriberCount,
        scv.onboardedSubscriberCount,
        ccv.activeCertCount,
        ccv.revokeCertCount,
        ccv.expiredCertCount,
        ccv.certCount
    FROM RASubscriberCountView scv, RACertificateCountView ccv
""")
	Object[] getSubscriberAndCertCount();





	@Query("SELECT sc FROM RASubscriberCertificates sc WHERE sc.subscriberUniqueId = ?1")
	List<RASubscriberCertificates> findBySubscriberUniqueIdToExpireCert(String subscriberUid);



	RASubscriberCertificates findTopBySubscriberUniqueIdOrderByCreationDateDesc(String subscriberUniqueId);
}