package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for Fertilizer Recommendation System.
 *
 * N, P, K represent nutrient levels (e.g., from soil test / sensor / lab report).
 * Wrapper types (Double) are used so missing JSON fields are treated as "required"
 * via {@link NotNull} instead of defaulting to 0.0.
 */
public record FertilizerRequestDTO(
        @JsonProperty("nitrogen")
        @NotNull(message = "nitrogen is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "nitrogen must be non-negative")
        Double nitrogen,

        @JsonProperty("phosphorus")
        @NotNull(message = "phosphorus is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "phosphorus must be non-negative")
        Double phosphorus,

        @JsonProperty("potassium")
        @NotNull(message = "potassium is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "potassium must be non-negative")
        Double potassium,

        @JsonProperty("cropType")
        @NotBlank(message = "cropType is required")
        String cropType
) {
}

