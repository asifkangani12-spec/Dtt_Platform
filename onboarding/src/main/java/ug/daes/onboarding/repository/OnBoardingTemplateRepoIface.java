/**
 * 
 */
package ug.daes.onboarding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ug.daes.onboarding.model.OnbSubscriberOnboardingTemplate;

/**
 * @author Raxit Dubey
 *
 */
@Repository
public interface OnBoardingTemplateRepoIface extends JpaRepository<OnbSubscriberOnboardingTemplate, Integer>{

	OnbSubscriberOnboardingTemplate findBytemplateId(int id);

	OnbSubscriberOnboardingTemplate findBytemplateMethod(String methodName);

	@Query("SELECT t FROM OnbSubscriberOnboardingTemplate t " +
			"WHERE t.templateMethod = :methodName AND t.publishedStatus = :status AND t.state = 'ACTIVE'")
    OnbSubscriberOnboardingTemplate getPublishTemplate(String methodName, String status);





	@Procedure(name = "OnbSubscriberOnboardingTemplate.updatePublishedStatus")
	void updateTemplateStatus(@Param("status") String status, @Param("id") int id);

	@Procedure(procedureName = "delete_map_method_onboarding_step_id")
	void deleteTemplateById(@Param("id") int id);

	@Query("SELECT COUNT(t) FROM OnbSubscriberOnboardingTemplate t WHERE t.templateName = :templateName")
	int isTemplateExist(String templateName);

	@Query("SELECT COUNT(t) FROM OnbSubscriberOnboardingTemplate t " +
			"WHERE t.templateName = :templateName OR t.templateMethod = :method")
	int isTemplateExistWithMethod(String templateName, String method);

	@Query("SELECT s FROM OnbSubscriberOnboardingTemplate s")
	List<OnbSubscriberOnboardingTemplate> getAllTemplate();

}
