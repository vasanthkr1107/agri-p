package com.example.demo.service;

import com.example.demo.dto.CropRequestDTO;
import com.example.demo.dto.CropResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Rule-based Crop Recommendation Engine.
 *
 * This service intentionally keeps the logic deterministic and transparent
 * (easy to validate, debug, and explain). Later, you can replace the internals
 * with a real ML model while keeping the same {@link #recommendCrop(CropRequestDTO)}
 * contract and {@link CropResponseDTO} output.
 */
@Service
public class CropRecommendationService {

    /**
     * Recommends a crop using "ML-like" rules based on soil + weather signals.
     *
     * @param request validated input (see {@link CropRequestDTO})
     * @return recommended crop + confidence + explanation
     */
    public CropResponseDTO recommendCrop(CropRequestDTO request) {
        // Normalize inputs once so rules stay clean and consistent.
        String soil = normalize(request.soilType());
        double t = request.temperature();
        double h = request.humidity();
        double r = request.rainfall();

        // 1) Example rule: Loamy + 20–30°C + high rainfall -> Rice
        if (isSoil(soil, "loamy") && between(t, 20, 30) && r > 100) {
            return response("Rice", "High",
                    "Loamy soil with warm temperature and high rainfall is ideal for paddy cultivation.");
        }

        // 2) Sandy + low rainfall -> Millet (drought tolerant)
        if (isSoil(soil, "sandy") && r < 50) {
            return response("Millet", "High",
                    "Sandy soil with low rainfall favors drought-tolerant cereals like millet.");
        }

        // 3) Temperature < 20 -> Wheat (cool-season preference)
        if (t < 20) {
            return response("Wheat", "Medium",
                    "Cooler temperatures generally suit wheat; ensure adequate soil moisture and nutrition.");
        }

        // 4) High humidity -> Sugarcane (also warn about disease risk elsewhere in your system)
        if (h > 80) {
            return response("Sugarcane", "Medium",
                    "High humidity supports sugarcane growth; monitor for fungal diseases due to humidity.");
        }

        // Additional meaningful rules (8–10+ total):

        // 5) Clayey + heavy rainfall -> Rice (water-retentive soil)
        if (isSoil(soil, "clayey", "clay") && r > 120) {
            return response("Rice", "High",
                    "Clayey soil retains water well; combined with heavy rainfall, rice becomes a strong choice.");
        }

        // 6) Black soil + hot temperature + moderate rainfall -> Cotton
        if (isSoil(soil, "black", "black soil") && t >= 25 && t <= 40 && between(r, 50, 120)) {
            return response("Cotton", "High",
                    "Black soil with warm weather and moderate rainfall is commonly suitable for cotton.");
        }

        // 7) Red soil + moderate temperature + moderate rainfall -> Groundnut
        if (isSoil(soil, "red", "red soil") && between(t, 22, 32) && between(r, 40, 100)) {
            return response("Groundnut", "Medium",
                    "Red soils with moderate warmth and rainfall often favor groundnut; maintain good drainage.");
        }

        // 8) Loamy + moderate temp + moderate rainfall -> Maize
        if (isSoil(soil, "loamy") && between(t, 18, 30) && between(r, 50, 100)) {
            return response("Maize", "High",
                    "Loamy soil with balanced temperature and rainfall is well-suited for maize.");
        }

        // 9) Sandy loam + warm temperature + low-to-moderate rainfall -> Chickpea
        if (isSoil(soil, "sandy loam", "sandyloam") && between(t, 20, 30) && between(r, 20, 60)) {
            return response("Chickpea", "Medium",
                    "Sandy loam with warm temperatures and lower rainfall is often suitable for chickpea.");
        }

        // 10) Very high temperature + low rainfall -> Sorghum (heat/drought tolerant)
        if (t > 35 && r < 60) {
            return response("Sorghum", "High",
                    "High heat with limited rainfall favors hardy crops like sorghum; plan moisture conservation.");
        }

        // 11) Moderate temperature + high rainfall + very high humidity -> Banana
        if (between(t, 22, 32) && r > 120 && h > 85) {
            return response("Banana", "Medium",
                    "Warm, humid conditions with high rainfall can support banana; ensure drainage to prevent root issues.");
        }

        // 12) Moderate temperature + moderate rainfall + mid humidity -> Tomato (horticulture)
        if (between(t, 18, 30) && between(r, 30, 80) && between(h, 40, 70)) {
            return response("Tomato", "Low",
                    "Conditions can support tomato; use this as a baseline and validate with local season/pest pressure.");
        }

        // Bonus: fallback when no rule matches
        return response("Mixed Cropping", "Low",
                "No strong rule match found. Consider mixed cropping and consult local agronomy guidance; refine inputs for better precision.");
    }

    private static CropResponseDTO response(String crop, String confidence, String message) {
        return new CropResponseDTO(crop, confidence, message);
    }

    private static boolean between(double value, double minInclusive, double maxInclusive) {
        return value >= minInclusive && value <= maxInclusive;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isSoil(String normalizedSoil, String... matches) {
        if (normalizedSoil == null || normalizedSoil.isBlank()) {
            return false;
        }
        for (String m : matches) {
            if (normalizedSoil.equals(normalize(m))) {
                return true;
            }
        }
        return false;
    }
}

