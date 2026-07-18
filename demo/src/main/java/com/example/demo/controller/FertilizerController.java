package com.example.demo.controller;

import com.example.demo.dto.FertilizerRequestDTO;
import com.example.demo.dto.FertilizerResponseDTO;
import com.example.demo.service.FertilizerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for Fertilizer Recommendation System.
 *
 * Endpoint:
 *  POST /api/fertilizer/recommend
 */
@RestController
@RequestMapping("/api/fertilizer")
public class FertilizerController {

    private final FertilizerService fertilizerService;

    public FertilizerController(FertilizerService fertilizerService) {
        this.fertilizerService = fertilizerService;
    }

    /**
     * Accepts NPK values + crop type and returns a fertilizer recommendation.
     */
    @PostMapping("/recommend")
    public ResponseEntity<FertilizerResponseDTO> recommend(@Valid @RequestBody FertilizerRequestDTO request) {
        return ResponseEntity.ok(fertilizerService.recommendFertilizer(request));
    }
}

