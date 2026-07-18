package com.example.demo.controller;

import com.example.demo.security.AuthTokenService;
import com.example.demo.service.DiseaseDetectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseDetectionController {

    private final DiseaseDetectionService detectionService;
    private final AuthTokenService authTokenService;

    public DiseaseDetectionController(DiseaseDetectionService detectionService, AuthTokenService authTokenService) {
        this.detectionService = detectionService;
        this.authTokenService = authTokenService;
    }

    @PostMapping("/detect")
    public ResponseEntity<Map<String, Object>> detectDisease(
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request) {
        
        Long userId = extractUserIdSilently(request);
        Map<String, Object> result = detectionService.detectDisease(image, userId);
        return ResponseEntity.ok(result);
    }

    private Long extractUserIdSilently(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) return null;
            String token = header.substring(7);
            AuthTokenService.TokenClaims claims = authTokenService.parseAndValidate(token);
            return claims.userId();
        } catch (Exception e) {
            return null;
        }
    }
}
