package com.example.demo.controller;

import com.example.demo.entity.CropPredictionHistory;
import com.example.demo.repository.CropPredictionHistoryRepository;
import com.example.demo.security.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for crop prediction history.
 * GET /api/crop-history/me        — current user's history (requires auth token)
 * GET /api/crop-history/all       — all history (admin only)
 */
@RestController
@RequestMapping("/api/crop-history")
public class CropPredictionHistoryController {

    private final CropPredictionHistoryRepository historyRepository;
    private final AuthTokenService authTokenService;

    public CropPredictionHistoryController(
            CropPredictionHistoryRepository historyRepository,
            AuthTokenService authTokenService) {
        this.historyRepository = historyRepository;
        this.authTokenService = authTokenService;
    }

    /**
     * Returns prediction history for the currently logged-in user.
     * Requires a valid Bearer token in the Authorization header.
     */
    @GetMapping("/me")
    public ResponseEntity<List<CropPredictionHistory>> myHistory(HttpServletRequest request) {
        Long userId = extractUserId(request);
        List<CropPredictionHistory> history = historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Returns all prediction history (admin only).
     */
    @GetMapping("/all")
    public ResponseEntity<List<CropPredictionHistory>> allHistory(HttpServletRequest request) {
        AuthTokenService.TokenClaims claims = extractClaims(request);
        if (!"ADMIN".equalsIgnoreCase(claims.role())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(historyRepository.findAllByOrderByCreatedAtDesc());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Long extractUserId(HttpServletRequest request) {
        return extractClaims(request).userId();
    }

    private AuthTokenService.TokenClaims extractClaims(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String token = header.substring(7);
        try {
            return authTokenService.parseAndValidate(token);
        } catch (IllegalArgumentException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
