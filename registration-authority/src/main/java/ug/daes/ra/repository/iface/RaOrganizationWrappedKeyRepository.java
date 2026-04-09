package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RAOrganizationWrappedKey;

@Repository
public interface RaOrganizationWrappedKeyRepository extends JpaRepository<RAOrganizationWrappedKey, String> {

	RAOrganizationWrappedKey findBycertificateSerialNumber(String paramString);
}
