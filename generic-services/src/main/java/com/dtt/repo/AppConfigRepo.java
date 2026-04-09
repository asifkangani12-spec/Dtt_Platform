package com.dtt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dtt.model.GenericAppconfigModel;

@Repository
public interface AppConfigRepo extends JpaRepository<GenericAppconfigModel, Integer>{

	@Query("SELECT a FROM GenericAppconfigModel a WHERE a.osVersion = :osVersion")
    GenericAppconfigModel getAppConfig(@Param("osVersion") String osVersion);

}