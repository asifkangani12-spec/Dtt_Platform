//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ug.daes.ra.repository.iface;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ug.daes.ra.model.RAVisitorCompleteDetails;

@Repository
public interface RaVisitorCompleteDetailsRepository extends JpaRepository<RAVisitorCompleteDetails, String> {
	@Query("SELECT v FROM RAVisitorCompleteDetails v WHERE v.subscriberUid = ?1")
    RAVisitorCompleteDetails fetchAllVisitors(String suid);

	@Query("SELECT v FROM RAVisitorCompleteDetails v WHERE v.idDocNumber = ?1 AND v.idDocType = ?2")
    RAVisitorCompleteDetails fetchVisitorByIdocNumber(String idDoc, String type);

	@Query("SELECT v FROM RAVisitorCompleteDetails v WHERE v.eMail = ?1")
    RAVisitorCompleteDetails fetchVisitorByEmail(String eMail);

	@Query("SELECT v FROM RAVisitorCompleteDetails v WHERE v.mobileNumber = ?1")
    RAVisitorCompleteDetails fetchVisitorByMobileNumber(String mobileNumber);

	@Query("SELECT v FROM RAVisitorCompleteDetails v WHERE v.subscriberType = ?1")
	List<RAVisitorCompleteDetails> fetchOnlyVisitors(String type);

	// JPQL-based query can be avoided for derived query
	@Query("SELECT v FROM RAVisitorCompleteDetails v WHERE v.idDocNumber = ?1")
    RAVisitorCompleteDetails idDocNumber(String passportNumber);
}
