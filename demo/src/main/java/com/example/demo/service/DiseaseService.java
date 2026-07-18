package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Disease;
import com.example.demo.repository.DiseaseRepository;

@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    public DiseaseService(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    public Disease saveDisease(Disease disease) {
        return diseaseRepository.save(disease);
    }

    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }

    public Disease getDiseaseById(Long id) {
        return diseaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disease not found"));
    }

    public Disease updateDisease(Long id, Disease updatedDisease) {

        Disease disease = getDiseaseById(id);

        disease.setPlantName(updatedDisease.getPlantName());
        disease.setDiseaseName(updatedDisease.getDiseaseName());
        disease.setSymptoms(updatedDisease.getSymptoms());
        disease.setTreatmentSteps(updatedDisease.getTreatmentSteps());
        disease.setDescription(updatedDisease.getDescription());
        disease.setRecommendedPesticide(updatedDisease.getRecommendedPesticide());
        disease.setPreventionMethods(updatedDisease.getPreventionMethods());
        return diseaseRepository.save(disease);
    }

    public void deleteDisease(Long id) {
        diseaseRepository.deleteById(id);
    }
}