package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Advisory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String weatherCondition;
    private String recommendation;

    // Constructors
    public Advisory() {}

    public Advisory(String message, String weatherCondition, String recommendation) {
        this.message = message;
        this.weatherCondition = weatherCondition;
        this.recommendation = recommendation;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}