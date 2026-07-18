package com.example.demo.controller;

import com.example.demo.dto.CropRequestDTO;
import com.example.demo.dto.CropResponseDTO;
import com.example.demo.service.CropRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the rule-based Crop Recommendation Engine.
 *
 * Endpoint:
 *  POST /api/crop/recommend
 */
@RestController
@RequestMapping("/api/crop")
public class CropRecommendationController {

    private final CropRecommendationService cropRecommendationService;

    public CropRecommendationController(CropRecommendationService cropRecommendationService) {
        this.cropRecommendationService = cropRecommendationService;
    }

    /**
     * Accepts soil + weather signals and returns a recommended crop with
     * a confidence level and human-readable explanation.
     */
    @PostMapping("/recommend")
    public ResponseEntity<CropResponseDTO> recommend(@Valid @RequestBody CropRequestDTO request) {
        return ResponseEntity.ok(cropRecommendationService.recommendCrop(request));
    }
}

