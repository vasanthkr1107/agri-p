package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Full enriched response returned to the frontend after:
 *  ML prediction → DB crop lookup → business enrichment
 */
public record CropSuggestionResponse(

        /** Predicted crop name from ML model */
        @JsonProperty("cropName")
        String cropName,

        /** Model confidence score (0.0 – 1.0) */
        @JsonProperty("modelConfidence")
        Double modelConfidence,

        /** Why this crop is suitable for the given conditions */
        @JsonProperty("reason")
        String reason,

        /** Total estimated cultivation cost (landArea × costPerAcre) */
        @JsonProperty("totalCost")
        BigDecimal totalCost,

        /** Expected profit (yield × marketPrice − totalCost) */
        @JsonProperty("expectedProfit")
        BigDecimal expectedProfit,

        /** Expected profit label from ML: Low / Medium / High */
        @JsonProperty("expectedProfitLabel")
        String expectedProfitLabel,

        /** Estimated cost string from ML e.g. "₹25,000 per acre" */
        @JsonProperty("estimatedCostLabel")
        String estimatedCostLabel,

        /** Recommended pesticides/IPM practices */
        @JsonProperty("pesticides")
        String pesticides,

        /** Growing duration in days */
        @JsonProperty("duration")
        Integer duration,

        /** Water and irrigation requirements */
        @JsonProperty("waterRequirements")
        String waterRequirements,

        /** Soil suitability note */
        @JsonProperty("soilSuitability")
        String soilSuitability,

        /** Budget-specific advice */
        @JsonProperty("budgetNote")
        String budgetNote,

        /** General recommendation note / fallback message */
        @JsonProperty("recommendationNote")
        String recommendationNote
) {}
