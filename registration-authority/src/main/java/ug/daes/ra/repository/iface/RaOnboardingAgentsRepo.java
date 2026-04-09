package ug.daes.ra.repository.iface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ug.daes.ra.model.RAOnboardingAgents;

@Repository
public interface RaOnboardingAgentsRepo extends JpaRepository<RAOnboardingAgents,Integer> {

	@Query("SELECT o FROM RAOnboardingAgents o WHERE o.deviceId = ?1 ORDER BY o.updatedOn DESC")
	List<RAOnboardingAgents> findByAgentdeviceUid(String deviceId);

}
