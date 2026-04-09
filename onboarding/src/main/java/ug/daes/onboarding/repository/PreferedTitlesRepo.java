package ug.daes.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ug.daes.onboarding.model.OnbPreferedTitles;

import java.util.List;

public interface PreferedTitlesRepo extends JpaRepository<OnbPreferedTitles,Integer> {


    @Query("SELECT p.preferedTitles FROM OnbPreferedTitles p ORDER BY CASE WHEN p.preferedTitles = 'None' THEN 1 ELSE 0 END, p.preferedTitles")
    List<String> getPreferedTitles();


}
