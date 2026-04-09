package com.dtt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dtt.model.GenericConsentModel;



@Repository
public interface GenericConsentRepo extends JpaRepository<GenericConsentModel, Integer> {

	@Query("SELECT c FROM GenericConsentModel c WHERE c.consentType = :consentType")
    GenericConsentModel getConsentByConsentType(@Param("consentType") String consentType);

}
