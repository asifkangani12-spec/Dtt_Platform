package com.dtt.organization.repository;

import com.dtt.organization.model.OrgOrganisationCategories;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgOrganisationCategoryRepo extends JpaRepository<OrgOrganisationCategories, Integer> {
    Optional<OrgOrganisationCategories> findByCategoryName(String categoryName);

    @Modifying
    @Transactional
    @Query("UPDATE OrgOrganisationCategories c " +
            "SET c.labelName = :labelName, c.updatedOn = :updatedOn " +
            "WHERE c.id = :id")
    int updateLabelNameById(
            @Param("id") int id,
            @Param("labelName") String labelName,
            @Param("updatedOn") String updatedOn
    );

}
