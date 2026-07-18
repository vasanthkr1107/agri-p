package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Stores every crop recommendation request made by a logged-in user.
 * Enables history tracking, analytics, and repeat-recommendation patterns.
 */
@Entity
@Table(name = "crop_prediction_history")
public class CropPredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Linked User ───────────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ── Soil Parameters ───────────────────────────────────────────────────────
    @Column(nullable = false)
    private Double nitrogen;

    @Column(nullable = false)
    private Double phosphorus;

    @Column(nullable = false)
    private Double potassium;

    @Column(nullable = false)
    private Double ph;

    @Column(name = "soil_type", length = 64)
    private String soilType;

    // ── Climate Parameters ────────────────────────────────────────────────────
    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(nullable = false)
    private Double rainfall;

    // ── Farm Details ──────────────────────────────────────────────────────────
    @Column(name = "land_area")
    private Double landArea;

    @Column(length = 32)
    private String budget;

    @Column(length = 128)
    private String location;

    // ── Prediction Result ─────────────────────────────────────────────────────
    @Column(name = "predicted_crop", nullable = false, length = 128)
    private String predictedCrop;

    @Column(nullable = false)
    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "estimated_cost", length = 128)
    private String estimatedCost;

    @Column(name = "expected_profit", length = 32)
    private String expectedProfit;

    // ── Timestamp ─────────────────────────────────────────────────────────────
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public CropPredictionHistory() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getNitrogen() { return nitrogen; }
    public void setNitrogen(Double nitrogen) { this.nitrogen = nitrogen; }

    public Double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(Double phosphorus) { this.phosphorus = phosphorus; }

    public Double getPotassium() { return potassium; }
    public void setPotassium(Double potassium) { this.potassium = potassium; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public Double getLandArea() { return landArea; }
    public void setLandArea(Double landArea) { this.landArea = landArea; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPredictedCrop() { return predictedCrop; }
    public void setPredictedCrop(String predictedCrop) { this.predictedCrop = predictedCrop; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(String estimatedCost) { this.estimatedCost = estimatedCost; }

    public String getExpectedProfit() { return expectedProfit; }
    public void setExpectedProfit(String expectedProfit) { this.expectedProfit = expectedProfit; }

    public Instant getCreatedAt() { return createdAt; }
}
