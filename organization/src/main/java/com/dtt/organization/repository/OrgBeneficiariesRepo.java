package com.dtt.organization.repository;


import com.dtt.organization.model.OrgBenificiaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface OrgBeneficiariesRepo extends JpaRepository<OrgBenificiaries, Integer> {





    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryType = 'INDIVIDUAL' ORDER BY b.id DESC")
    List<OrgBenificiaries> getAllBeneficiariesBySponsor(String sponsorId);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 ORDER BY b.id DESC")
    List<OrgBenificiaries> getAllBeneficiariesBySponsorByDigitalId(String sponsorId);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND (b.beneficiaryMobileNumber = ?2 OR b.beneficiaryPassport = ?3 OR b.beneficiaryOfficeEmail = ?4)")
    List<OrgBenificiaries> findDuplicateBeneficiariesByPassport(String sponsorId, String number, String passport, String officeEmail);




    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryUgPassEmail = ?2")
    OrgBenificiaries findDuplicateBeneficiariesByUgPassEmail(String sponsorId, String ugPassEmail);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryMobileNumber = ?2")
    OrgBenificiaries findDuplicateBeneficiariesByMobileNumber(String sponsorId, String mobileNumber);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryPassport = ?2")
    OrgBenificiaries findDuplicateBeneficiariesByPassport(String sponsorId, String passport);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryNin = ?2")
    OrgBenificiaries findDuplicateBeneficiariesByNIN(String sponsorId, String nin);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryOfficeEmail = ?2")
    OrgBenificiaries findDuplicateBeneficiariesByOfficeEmail(String sponsorId, String officeEmail);


    @Query("SELECT b FROM OrgBenificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryDigitalId = ?2")
    OrgBenificiaries findDuplicateBeneficiariesByBeneficiaryDigitalId(String sponsorId, String beneficiaryDigitalId);


    @Modifying
    @Transactional
    @Query("UPDATE OrgBenificiaries b SET b.status = 'INACTIVE' WHERE b.id = ?1")
    void changeStatusById(int id);


//    @Query("SELECT e FROM OrgBenificiaries e " +
//            "JOIN OrgBeneficiaryValidity v ON v.beneficiaryId = e.id " +
//            "WHERE (e.beneficiaryUgPassEmail = :email " +
//            "       OR e.beneficiaryPassport = :passport " +
//            "       OR e.beneficiaryNin = :nin " +
//            "       OR e.beneficiaryMobileNumber = :mobileNumber " +
//            "       OR e.beneficiaryDigitalId = :beneficiaryDigitalId) " +
//            "AND e.status = 'ACTIVE' " +
//            "AND ((v.validityApplicable = TRUE AND CURRENT_DATE BETWEEN " +
//            "       FUNCTION('TO_DATE', v.validFrom, 'YYYY-MM-DD') AND FUNCTION('TO_DATE', v.validUpTo, 'YYYY-MM-DD')) " +
//            "       OR v.validityApplicable = FALSE) " +
//            "AND v.privilegeServiceId = 3 " +
//            "AND v.status = 'ACTIVE'")

//    @Query("SELECT e FROM OrgBenificiaries e " +
//            "JOIN com.dtt.organization.model.OrgBeneficiaryValidity v " +
//            "ON v.beneficiaryId = e.id " +
//            "WHERE (e.beneficiaryUgPassEmail = :email " +
//            "       OR e.beneficiaryPassport = :passport " +
//            "       OR e.beneficiaryNin = :nin " +
//            "       OR e.beneficiaryMobileNumber = :mobileNumber " +
//            "       OR e.beneficiaryDigitalId = :beneficiaryDigitalId) " +
//            "AND e.status = 'ACTIVE' " +
//            "AND ((v.validityApplicable = TRUE AND CURRENT_DATE BETWEEN " +
//            "       FUNCTION('TO_DATE', v.validFrom, 'YYYY-MM-DD') AND FUNCTION('TO_DATE', v.validUpTo, 'YYYY-MM-DD')) " +
//            "       OR v.validityApplicable = FALSE) " +
//            "AND v.privilegeServiceId = 3 " +
//            "AND v.status = 'ACTIVE'")

    @Query("SELECT e FROM OrgBenificiaries e, OrgBeneficiaryValidity v " +
            "WHERE v.beneficiaryId = e.id " +
            "AND (e.beneficiaryUgPassEmail = :email " +
            "       OR e.beneficiaryPassport = :passport " +
            "       OR e.beneficiaryNin = :nin " +
            "       OR e.beneficiaryMobileNumber = :mobileNumber " +
            "       OR e.beneficiaryDigitalId = :beneficiaryDigitalId) " +
            "AND e.status = 'ACTIVE' " +
            "AND ((v.validityApplicable = TRUE AND CURRENT_DATE BETWEEN " +
            "       FUNCTION('TO_DATE', v.validFrom, 'YYYY-MM-DD') AND FUNCTION('TO_DATE', v.validUpTo, 'YYYY-MM-DD')) " +
            "       OR v.validityApplicable = FALSE) " +
            "AND v.privilegeServiceId = 3 " +
            "AND v.status = 'ACTIVE'")
    List<OrgBenificiaries> findByEmailOrPassportOrNinOrMobileNumberorBeneficiaryDigitalId(
            @Param("email") String email,
            @Param("passport") String passport,
            @Param("nin") String nin,
            @Param("mobileNumber") String mobileNumber,
            @Param("beneficiaryDigitalId") String beneficiaryDigitalId);


    @Query("SELECT b FROM OrgBenificiaries b " +
            "WHERE b.status = 'ACTIVE' " +
            "AND (b.beneficiaryUgPassEmail = :email " +
            "     OR b.beneficiaryPassport = :passport " +
            "     OR b.beneficiaryNin = :nin " +
            "     OR b.beneficiaryMobileNumber = :mobileNumber " +
            "     OR b.beneficiaryDigitalId = :beneficiaryDigitalId)")
    List<OrgBenificiaries> findAllSponsor(@Param("email") String email,
                                          @Param("passport") String passport,
                                          @Param("nin") String nin,
                                          @Param("mobileNumber") String mobileNumber,
                                          @Param("beneficiaryDigitalId") String beneficiaryDigitalId);

    @Modifying
    @Transactional
    @Query("UPDATE OrgBenificiaries b SET b.status = 'ACTIVE' WHERE b.id = ?1")
    int changeStatusForSSP(int id);




}