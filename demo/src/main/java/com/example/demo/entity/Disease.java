package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plantName;

    @Column(nullable = false)
    private String diseaseName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String treatmentSteps;

    @Column(columnDefinition = "TEXT")
    private String recommendedPesticide;

    @Column(columnDefinition = "TEXT")
    private String preventionMethods;

    public Disease() {}

    public Disease(String plantName, String diseaseName, String description, String symptoms, String treatmentSteps, String recommendedPesticide, String preventionMethods) {
        this.plantName = plantName;
        this.diseaseName = diseaseName;
        this.description = description;
        this.symptoms = symptoms;
        this.treatmentSteps = treatmentSteps;
        this.recommendedPesticide = recommendedPesticide;
        this.preventionMethods = preventionMethods;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatmentSteps() {
        return treatmentSteps;
    }

    public void setTreatmentSteps(String treatmentSteps) {
        this.treatmentSteps = treatmentSteps;
    }

    public String getRecommendedPesticide() {
        return recommendedPesticide;
    }

    public void setRecommendedPesticide(String recommendedPesticide) {
        this.recommendedPesticide = recommendedPesticide;
    }

    public String getPreventionMethods() {
        return preventionMethods;
    }

    public void setPreventionMethods(String preventionMethods) {
        this.preventionMethods = preventionMethods;
    }
}