package ug.daes.ra.repository.iface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RAOrganizationDetails;

@Repository
public interface RaOrganizationDetailsRepository extends JpaRepository<RAOrganizationDetails, Integer> {

	RAOrganizationDetails findByOrganizationUid(String paramString);


	@Query("SELECT o FROM RAOrganizationDetails o WHERE o.organizationUid = ?1")
	List<RAOrganizationDetails> findOrganizationByUid(String paramString);

	@Query("SELECT o FROM RAOrganizationDetails o WHERE o.organizationName LIKE CONCAT('%', :name, '%')")
	List<RAOrganizationDetails> getOrganizationByName(@Param("name") String name);


	@Query("SELECT o FROM RAOrganizationDetails o WHERE o.organizationName = ?1")
	List<RAOrganizationDetails> getOrgnizationDetails(String organizationName);

	@Query("SELECT COUNT(o) FROM RAOrganizationDetails o WHERE o.organizationName = ?1")
	int isOrgnizationExist(String orgName);




}
