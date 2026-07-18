package com.example.demo.controller;

import com.example.demo.service.PesticideRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pesticides")
public class PesticideController {

    private final PesticideRecommendationService pesticideRecommendationService;

    public PesticideController(PesticideRecommendationService pesticideRecommendationService) {
        this.pesticideRecommendationService = pesticideRecommendationService;
    }

    static class RecommendationRequest {
        public String crop_name;
        public String disease_name;
        public String severity;
    }

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommendPesticide(@RequestBody RecommendationRequest req) {
        Map<String, Object> result = pesticideRecommendationService.recommendPesticide(
                req.crop_name != null ? req.crop_name : "",
                req.disease_name != null ? req.disease_name : "",
                req.severity != null ? req.severity : "Medium"
        );
        return ResponseEntity.ok(result);
    }
}
