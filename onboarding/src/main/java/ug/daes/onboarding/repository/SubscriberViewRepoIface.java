package ug.daes.onboarding.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberView;

@Repository
@Transactional
public interface SubscriberViewRepoIface extends JpaRepository<OnbSubscriberView, String> {


	@Query("SELECT sv FROM OnbSubscriberView sv WHERE sv.subscriberUid = :suid")
    OnbSubscriberView findSubscriberDetails(@Param("suid") String suid);


	@Query("SELECT sv FROM OnbSubscriberView sv " +
			"WHERE sv.idDocNumber = :documentId " +
			"AND sv.subscriberStatus NOT IN ('CERT_REVOKED', 'INACTIVE', 'CERT_EXPIRED')")
	List<OnbSubscriberView> findSubscriberByDocId(@Param("documentId") String documentId);

	@Query("SELECT sv FROM OnbSubscriberView sv " +
			"WHERE sv.idDocNumber = :documentId " +
			"AND sv.subscriberStatus NOT IN ('CERT_REVOKED', 'INACTIVE', 'CERT_EXPIRED')")
    OnbSubscriberView findSubscriberByDocIdCertRevoked(@Param("documentId") String documentId);
}
