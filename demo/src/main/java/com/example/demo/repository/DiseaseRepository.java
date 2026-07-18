package com.example.demo.repository;

import com.example.demo.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Optional<Disease> findByPlantNameIgnoreCaseAndDiseaseNameIgnoreCase(String plantName, String diseaseName);
}