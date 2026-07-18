package com.example.demo.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.CropSuggestionRequest;
import com.example.demo.dto.MlRecommendCropResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Calls the Python FastAPI ML service (POST /recommend-crop).
 * Maps the full {@link CropSuggestionRequest} to the ML API payload.
 */
@Service
public class CropMLService {

    private final RestTemplate cropMlRestTemplate;
    private final String recommendCropUrl;

    public CropMLService(
            @Qualifier("cropMlRestTemplate") RestTemplate cropMlRestTemplate,
            @Value("${crop.ml.base-url:http://localhost:8000}") String mlBaseUrl) {
        this.cropMlRestTemplate = cropMlRestTemplate;
        this.recommendCropUrl = mlBaseUrl.replaceAll("/$", "") + "/recommend-crop";
    }

    /**
     * Sends the full agronomic request to the Python ML service and returns
     * the enriched prediction (crop name, confidence, reason, cost, profit).
     */
    public MlRecommendCropResponse fetchRecommendation(CropSuggestionRequest request) {
        // Build the ML payload matching FastAPI's RecommendRequest model
        Map<String, Object> payload = new HashMap<>();
        payload.put("nitrogen",    request.nitrogen());
        payload.put("phosphorus",  request.phosphorus());
        payload.put("potassium",   request.potassium());
        payload.put("temperature", request.temperature());
        payload.put("humidity",    request.humidity());
        payload.put("rainfall",    request.rainfall());
        payload.put("ph",          request.phOrDefault());
        payload.put("soil_type",   request.soilType());
        payload.put("budget",      request.budgetOrDefault());

        if (request.location() != null && !request.location().isBlank()) {
            payload.put("location", request.location());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<MlRecommendCropResponse> response = cropMlRestTemplate.postForEntity(
                    recommendCropUrl,
                    entity,
                    MlRecommendCropResponse.class);

            MlRecommendCropResponse body = response.getBody();
            if (body == null || body.predictedCrop() == null || body.predictedCrop().isBlank()) {
                throw new IllegalStateException("ML service returned an empty crop name");
            }
            return body;
        } catch (RestClientException e) {
            String rootCause = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            throw new IllegalStateException(
                    "Could not reach ML service at " + recommendCropUrl
                            + ". Is FastAPI running? Cause: " + rootCause, e);
        }
    }
}
