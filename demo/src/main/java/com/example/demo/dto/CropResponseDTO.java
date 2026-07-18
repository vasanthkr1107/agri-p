package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Output from the Crop Recommendation Engine.
 * Designed to be stable even if the internal implementation changes later
 * (for example plugging in a real ML model).
 */
public record CropResponseDTO(
        @JsonProperty("recommendedCrop") String recommendedCrop,
        @JsonProperty("confidence") String confidence,
        @JsonProperty("message") String message
) {
}

