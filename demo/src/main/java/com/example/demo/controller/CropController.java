package com.example.demo.controller;

import com.example.demo.dto.CropSuggestionRequest;
import com.example.demo.dto.CropSuggestionResponse;
import com.example.demo.entity.Crop;
import com.example.demo.security.AuthTokenService;
import com.example.demo.service.CropService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;
    private final AuthTokenService authTokenService;

    public CropController(CropService cropService, AuthTokenService authTokenService) {
        this.cropService = cropService;
        this.authTokenService = authTokenService;
    }

    /**
     * AI pipeline: Full agronomic input → FastAPI ML → MySQL crop profile → enriched response.
     * If a valid Bearer token is present, the prediction is saved to history.
     */
    @PostMapping("/suggest-ai")
    public ResponseEntity<CropSuggestionResponse> suggestAi(
            @Valid @RequestBody CropSuggestionRequest body,
            HttpServletRequest httpRequest) {
        Long userId = extractUserIdSilently(httpRequest);
        return ResponseEntity.ok(cropService.suggestFromMl(body, userId));
    }

    @PostMapping
    public Crop addCrop(@RequestBody Crop crop) {
        return cropService.saveCrop(crop);
    }

    @GetMapping
    public List<Crop> getAllCrops() {
        return cropService.getAllCrops();
    }

    @GetMapping("/{id}")
    public Crop getCropById(@PathVariable Long id) {
        return cropService.getCropById(id);
    }

    @PutMapping("/{id}")
    public Crop updateCrop(@PathVariable Long id, @RequestBody Crop crop) {
        return cropService.updateCrop(id, crop);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCrop(@PathVariable Long id) {
        cropService.deleteCrop(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "🌱 Crop removed successfully.");
        return ResponseEntity.ok(response);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Extracts the userId from the Bearer token without throwing if missing/invalid.
     * Returns null if no valid token is present (anonymous suggestion still works,
     * it just won't be saved to history).
     */
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
