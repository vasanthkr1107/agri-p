package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private double temperature;
    private double humidity;
    @Column(name = "weather_condition")
    private String condition; // Rain, Sunny, Cloudy

    public WeatherData() {}

    public WeatherData(String location, double temperature, double humidity, String condition) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
    }

    // ✅ Getters & Setters

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}