package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ug.daes.onboarding.model.OnbPhotoFeatures;

@Repository
public interface PhotoFeaturesRepo extends JpaRepository<OnbPhotoFeatures,Integer> {

}
