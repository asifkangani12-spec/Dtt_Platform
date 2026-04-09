package com.dtt.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dtt.model.GenericSubscriber;

import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface GenericSubscriberRepo extends JpaRepository<GenericSubscriber, String> {

	@Query("SELECT s FROM GenericSubscriber s WHERE s.idDocNumber = ?1")
    GenericSubscriber findbyDocumentNumber(String idDocument);


GenericSubscriber findBySubscriberUid(String suid);
GenericSubscriber findByPassportNumber(String passportNumber);
GenericSubscriber findByNationalIdNumber(String nationalIdNumber);



}
