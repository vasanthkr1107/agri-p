package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response returned by Fertilizer Recommendation System.
 */
public record FertilizerResponseDTO(
        @JsonProperty("fertilizerName") String fertilizerName,
        @JsonProperty("quantityRecommendation") String quantityRecommendation,
        @JsonProperty("message") String message
) {
}

