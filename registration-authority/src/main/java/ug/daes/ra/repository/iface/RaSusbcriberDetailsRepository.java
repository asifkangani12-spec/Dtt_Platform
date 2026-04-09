package ug.daes.ra.repository.iface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RASusbcriberDetails;



@Repository
public interface RaSusbcriberDetailsRepository extends JpaRepository<RASusbcriberDetails, String>{

	RASusbcriberDetails findBysubscriberUid(String subscriberUniqueId);

	@Query("SELECT s.subscriberUid FROM RASusbcriberDetails s WHERE s.mobileNumber = ?1")
	String findBymobileNumber(String mobileNumber);

	@Query("SELECT s.subscriberUid FROM RASusbcriberDetails s WHERE s.eMail = ?1")
	String findByemailId(String eMail);
}
