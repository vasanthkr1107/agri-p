package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

/**
 * Full input for the AI Crop Recommendation pipeline.
 * Mirrors the FastAPI ML service request body.
 */
public record CropSuggestionRequest(

        /** Nitrogen content in soil (kg/ha) */
        @JsonProperty("nitrogen")
        @NotNull(message = "nitrogen is required")
        @DecimalMin(value = "0.0", message = "nitrogen must be >= 0")
        @DecimalMax(value = "140.0", message = "nitrogen must be <= 140")
        Double nitrogen,

        /** Phosphorus content in soil (kg/ha) */
        @JsonProperty("phosphorus")
        @NotNull(message = "phosphorus is required")
        @DecimalMin(value = "0.0", message = "phosphorus must be >= 0")
        @DecimalMax(value = "145.0", message = "phosphorus must be <= 145")
        Double phosphorus,

        /** Potassium content in soil (kg/ha) */
        @JsonProperty("potassium")
        @NotNull(message = "potassium is required")
        @DecimalMin(value = "0.0", message = "potassium must be >= 0")
        @DecimalMax(value = "205.0", message = "potassium must be <= 205")
        Double potassium,

        /** Ambient temperature (°C) */
        @JsonProperty("temperature")
        @NotNull(message = "temperature is required")
        @DecimalMin(value = "-10.0", message = "temperature must be >= -10°C")
        @DecimalMax(value = "60.0", message = "temperature must be <= 60°C")
        Double temperature,

        /** Relative humidity (%) */
        @JsonProperty("humidity")
        @NotNull(message = "humidity is required")
        @DecimalMin(value = "0.0", message = "humidity must be between 0 and 100")
        @DecimalMax(value = "100.0", message = "humidity must be between 0 and 100")
        Double humidity,

        /** Annual rainfall (mm) */
        @JsonProperty("rainfall")
        @NotNull(message = "rainfall is required")
        @DecimalMin(value = "0.0", message = "rainfall must be >= 0")
        Double rainfall,

        /** Soil pH (4.0 – 9.0) — defaults to 6.5 if null */
        @JsonProperty("ph")
        @DecimalMin(value = "4.0", message = "ph must be >= 4.0")
        @DecimalMax(value = "9.0", message = "ph must be <= 9.0")
        Double ph,

        /** Soil type: Clay, Loamy, Sandy, Black, Red, Sandy Loam */
        @JsonProperty("soilType")
        @NotBlank(message = "soilType is required")
        String soilType,

        /** Farmer budget level: Low, Medium, High */
        @JsonProperty("budget")
        String budget,

        /** Optional location/region for future weather API integration */
        @JsonProperty("location")
        String location,

        /** Land area in acres (optional, used for cost calculation) */
        @JsonProperty("landArea")
        @DecimalMin(value = "0.01", message = "landArea must be positive")
        Double landArea
) {
    /** Returns ph or default 6.5 if not provided */
    public double phOrDefault() {
        return ph != null ? ph : 6.5;
    }

    /** Returns landArea or default 1.0 acre */
    public double landAreaOrDefault() {
        return landArea != null ? landArea : 1.0;
    }

    /** Returns budget or default Medium */
    public String budgetOrDefault() {
        return budget != null && !budget.isBlank() ? budget : "Medium";
    }
}
