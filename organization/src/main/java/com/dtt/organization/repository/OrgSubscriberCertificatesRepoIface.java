package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtt.organization.model.OrgSubscriberCertificate;
import org.springframework.data.jpa.repository.Query;

public interface OrgSubscriberCertificatesRepoIface extends JpaRepository<OrgSubscriberCertificate,String>{

    OrgSubscriberCertificate findTop1ByCertificateSerialNumberOrderByCreatedDateDesc(String serialNumber);

    default String findSuidByCertSerialNumber(String serialNumber) {
        OrgSubscriberCertificate cert = findTop1ByCertificateSerialNumberOrderByCreatedDateDesc(serialNumber);
        return cert != null ? cert.getSubscriberUid() : null;
    }

    @Query("SELECT s FROM OrgSubscriberCertificate s WHERE s.certificateSerialNumber = ?1")
    OrgSubscriberCertificate findByCertificateSerialNumber(String serialNumber);
}
