package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Pesticide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String chemicalType;

    @Column(columnDefinition = "TEXT")
    private String dosage;

    @Column(columnDefinition = "TEXT")
    private String sprayInterval;

    private String estimatedCost;

    @Column(columnDefinition = "TEXT")
    private String organicAlternative;

    @Column(columnDefinition = "TEXT")
    private String safetyPrecautions;

    public Pesticide() {}

    public Pesticide(String name, String chemicalType, String dosage, String sprayInterval, String estimatedCost, String organicAlternative, String safetyPrecautions) {
        this.name = name;
        this.chemicalType = chemicalType;
        this.dosage = dosage;
        this.sprayInterval = sprayInterval;
        this.estimatedCost = estimatedCost;
        this.organicAlternative = organicAlternative;
        this.safetyPrecautions = safetyPrecautions;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChemicalType() {
        return chemicalType;
    }

    public void setChemicalType(String chemicalType) {
        this.chemicalType = chemicalType;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getSprayInterval() {
        return sprayInterval;
    }

    public void setSprayInterval(String sprayInterval) {
        this.sprayInterval = sprayInterval;
    }

    public String getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(String estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getOrganicAlternative() {
        return organicAlternative;
    }

    public void setOrganicAlternative(String organicAlternative) {
        this.organicAlternative = organicAlternative;
    }

    public String getSafetyPrecautions() {
        return safetyPrecautions;
    }

    public void setSafetyPrecautions(String safetyPrecautions) {
        this.safetyPrecautions = safetyPrecautions;
    }
}
