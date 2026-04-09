package com.dtt.repo;


import com.dtt.model.GenericCard;
import com.dtt.requestdto.SubscriberDetailsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;


@Repository
public interface CardRepo extends JpaRepository<GenericCard,Integer> {


	GenericCard findByIdDocNumber(String idDocNumber);

	@Query("""
    SELECT new com.dtt.requestdto.SubscriberDetailsDto(
        s.subscriberUid,
        s.fullName,
        s.dateOfBirth,
        s.mobileNumber,
        s.emailId,
        s.createdDate,
        sd.selfieUri,
        sd.gender,
        sd.onboardingDataFieldsJson,
        sd.selfie
    )
    FROM GenericSubscriber s
    JOIN GenericSubscriberOnboardingData sd
        ON sd.subscriberUid = s.subscriberUid
    WHERE s.idDocNumber = :idDocNumber
      AND sd.createdDate = (
          SELECT MAX(sd2.createdDate)
          FROM GenericSubscriberOnboardingData sd2
          WHERE sd2.subscriberUid = s.subscriberUid
      )
""")
	SubscriberDetailsDto getSubscriberDetailsByDocumentNumber(@Param("idDocNumber") String idDocNumber);



}



