package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Input for the rule-based Crop Recommendation Engine.
 *
 * Note: wrapper types (Double) are used instead of primitives (double)
 * so that missing JSON fields are validated as "required" via {@link NotNull}.
 */
public record CropRequestDTO(
        @JsonProperty("soilType")
        @NotBlank(message = "soilType is required")
        String soilType,

        @JsonProperty("temperature")
        @NotNull(message = "temperature is required")
        @DecimalMin(value = "-10.0", message = "temperature must be >= -10°C")
        @DecimalMax(value = "60.0", message = "temperature must be <= 60°C")
        Double temperature,

        @JsonProperty("humidity")
        @NotNull(message = "humidity is required")
        @DecimalMin(value = "0.0", message = "humidity must be between 0 and 100")
        @DecimalMax(value = "100.0", message = "humidity must be between 0 and 100")
        Double humidity,

        @JsonProperty("rainfall")
        @NotNull(message = "rainfall is required")
        @DecimalMin(value = "0.0", message = "rainfall must be >= 0")
        Double rainfall
) {
}

