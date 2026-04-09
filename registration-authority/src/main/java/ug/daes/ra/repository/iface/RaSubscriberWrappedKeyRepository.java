/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ug.daes.ra.model.RASubscriberWrappedKey;

/**
 * The Interface SubscriberWrappedKeyRepository.
 */
public interface RaSubscriberWrappedKeyRepository extends JpaRepository<RASubscriberWrappedKey, String> {

	/**
	 * Find bycertificate serial number.
	 *
	 * @param certificateSerialNumber
	 *            the certificate serial number
	 * @return the subscriber wrapped key
	 */
	@Query("SELECT s FROM RASubscriberWrappedKey s WHERE s.certificateSerialNumber = :certificateSerialNumber")
    RASubscriberWrappedKey findBycertificateSerialNumber(@Param("certificateSerialNumber") String certificateSerialNumber);


}
