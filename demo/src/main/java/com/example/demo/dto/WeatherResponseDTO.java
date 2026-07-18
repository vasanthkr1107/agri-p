package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO returned by the weather endpoint.
 * It exposes only the fields the Crop Advisory System needs,
 * and hides the raw external API response details.
 */
public record WeatherResponseDTO(
        @JsonProperty("city") String city,
        @JsonProperty("temperature") double temperature,
        @JsonProperty("humidity") double humidity,
        @JsonProperty("description") String description,
        /**
         * High‑level advisory messages derived from the weather data
         * (e.g. drought‑resistant crops, fungal disease warnings).
         */
        @JsonProperty("advisories") List<String> advisories
) {
}

