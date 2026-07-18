package com.example.demo.service;

import com.example.demo.entity.Disease;
import com.example.demo.entity.Prediction;
import com.example.demo.entity.User;
import com.example.demo.repository.DiseaseRepository;
import com.example.demo.repository.PredictionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DiseaseDetectionService {

    private final RestTemplate restTemplate;
    private final DiseaseRepository diseaseRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;

    @Value("${disease.ml.base-url:http://localhost:8000}")
    private String mlBaseUrl;

    public DiseaseDetectionService(RestTemplate restTemplate, DiseaseRepository diseaseRepository, PredictionRepository predictionRepository, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.diseaseRepository = diseaseRepository;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> detectDisease(MultipartFile file, Long userId) {
        // 1. Call FastAPI ML service
        String url = mlBaseUrl.replaceAll("/$", "") + "/predict";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource());
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> mlResult = response.getBody();
        
        if (mlResult == null || !mlResult.containsKey("disease_name")) {
            throw new RuntimeException("ML service failed to return a prediction.");
        }
        
        String plantName = (String) mlResult.get("plant_name");
        String diseaseName = (String) mlResult.get("disease_name");
        Double confidence = (Double) mlResult.get("confidence");
        
        // 2. Lookup Disease info from DB
        Optional<Disease> dbDisease = diseaseRepository.findByPlantNameIgnoreCaseAndDiseaseNameIgnoreCase(plantName, diseaseName);
        
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("plant_name", plantName);
        finalResult.put("disease_name", diseaseName);
        finalResult.put("confidence", confidence);
        
        if (dbDisease.isPresent()) {
            Disease d = dbDisease.get();
            finalResult.put("description", d.getDescription());
            finalResult.put("recommended_pesticide", d.getRecommendedPesticide());
            finalResult.put("prevention", d.getPreventionMethods());
            finalResult.put("treatment_steps", d.getTreatmentSteps());
        } else {
            // Fallback if not found in catalog
            finalResult.put("description", "Specific details not found in database.");
            finalResult.put("recommended_pesticide", "Consult local expert.");
            finalResult.put("prevention", "Maintain general field hygiene.");
            finalResult.put("treatment_steps", "Remove affected parts.");
        }
        
        // 3. Save Prediction to History
        Prediction prediction = new Prediction();
        prediction.setImagePath(file.getOriginalFilename());
        prediction.setPredictedDisease(plantName + " " + diseaseName);
        prediction.setConfidence(confidence);
        
        if (userId != null) {
            userRepository.findById(userId).ifPresent(prediction::setUser);
        }
        
        predictionRepository.save(prediction);
        
        return finalResult;
    }
}
