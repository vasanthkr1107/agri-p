package com.example.demo.service;

import com.example.demo.entity.Pesticide;
import com.example.demo.repository.PesticideRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PesticideRecommendationService {

    private final RestTemplate restTemplate;
    private final PesticideRepository pesticideRepository;

    @Value("${disease.ml.base-url:http://localhost:8000}")
    private String mlBaseUrl;

    public PesticideRecommendationService(RestTemplate restTemplate, PesticideRepository pesticideRepository) {
        this.restTemplate = restTemplate;
        this.pesticideRepository = pesticideRepository;
    }

    public Map<String, Object> recommendPesticide(String cropName, String diseaseName, String severity) {
        String url = mlBaseUrl.replaceAll("/$", "") + "/recommend-pesticide";

        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("crop_name", cropName);
        requestPayload.put("disease_name", diseaseName);
        requestPayload.put("severity", severity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> mlResult = response.getBody();

        if (mlResult == null || !mlResult.containsKey("recommended_pesticide")) {
            throw new RuntimeException("ML service failed to return a pesticide recommendation.");
        }

        String recommendedPesticide = (String) mlResult.get("recommended_pesticide");

        // Lookup exact pesticide info from DB to enrich with estimated_cost and safety_precautions
        Pesticide dbPesticide = pesticideRepository.findByNameIgnoreCase(recommendedPesticide).orElse(null);

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("recommended_pesticide", recommendedPesticide);
        finalResult.put("chemical_type", mlResult.get("chemical_type"));
        finalResult.put("dosage", mlResult.get("dosage"));
        finalResult.put("spray_interval", mlResult.get("spray_interval"));
        finalResult.put("organic_alternative", mlResult.get("organic_alternative"));

        if (dbPesticide != null) {
            finalResult.put("estimated_cost", dbPesticide.getEstimatedCost());
            
            // Convert safety precautions string to list for the frontend
            String safety = dbPesticide.getSafetyPrecautions();
            if (safety != null && !safety.isBlank()) {
                finalResult.put("safety_precautions", safety.split(";\\s*"));
            } else {
                finalResult.put("safety_precautions", new String[]{"Wear standard protective gear."});
            }
        } else {
            finalResult.put("estimated_cost", "Price varies by region");
            finalResult.put("safety_precautions", new String[]{"Read the manufacturer's label carefully.", "Use standard Personal Protective Equipment (PPE)."});
        }

        return finalResult;
    }
}
