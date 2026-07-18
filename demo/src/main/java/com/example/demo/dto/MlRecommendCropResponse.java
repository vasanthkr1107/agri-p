package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Full JSON returned by the FastAPI ML service:
 * {
 *   "predicted_crop": "Rice",
 *   "confidence": 0.94,
 *   "reason": "...",
 *   "estimated_cost": "₹25,000 per acre",
 *   "expected_profit": "High"
 * }
 */
public record MlRecommendCropResponse(

        /** Primary predicted crop name */
        @JsonProperty("predicted_crop")
        @JsonAlias("crop")
        String predictedCrop,

        /** Max class probability from the Random Forest (0.0 – 1.0) */
        @JsonProperty("confidence")
        Double confidence,

        /** Human-readable reason why this crop is suitable */
        @JsonProperty("reason")
        String reason,

        /** Estimated cultivation cost string e.g. "₹25,000 per acre" */
        @JsonProperty("estimated_cost")
        String estimatedCost,

        /** Expected profit level: "Low", "Medium", "High" */
        @JsonProperty("expected_profit")
        String expectedProfit
) {
    /** Convenience alias for backward-compat callers that use .crop() */
    public String crop() {
        return predictedCrop;
    }
}
