package com.dtt.organization.repository;

import com.dtt.organization.model.OrgBeneficiaryInfoView;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgBeneficiaryInfoViewRepo extends JpaRepository<OrgBeneficiaryInfoView,Integer> {


    @Query("SELECT b FROM OrgBeneficiaryInfoView b " +
            "WHERE b.beneficiaryStatus = 'ACTIVE' " +
            "AND b.validityStatus = 'ACTIVE' " +
            "AND (b.beneficiaryUgPassEmail = ?1 " +
            "OR b.beneficiaryPassport = ?2 " +
            "OR b.beneficiaryNin = ?3 " +
            "OR b.beneficiaryMobileNumber = ?4 " +
            "OR b.beneficiaryDigitalId = ?5)")
    List<OrgBeneficiaryInfoView> findByEmailOrPassportOrNinOrMobileNumberOrBeneficiaryDigitalId(
            String email, String passport, String nin, String mobileNumber, String beneficiaryDigitalId);

    @Query("SELECT b FROM OrgBeneficiaryInfoView b WHERE b.sponsorExternalId = ?1")
    List<OrgBeneficiaryInfoView> getVendorsByVendorId(String vendorId);



}
