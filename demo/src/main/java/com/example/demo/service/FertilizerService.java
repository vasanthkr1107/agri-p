package com.example.demo.service;

import com.example.demo.dto.FertilizerRequestDTO;
import com.example.demo.dto.FertilizerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Fertilizer Recommendation System (rule-based).
 *
 * The rules are intentionally simple and explainable. This makes it easy to:
 * - extend with more crops/rules later
 * - swap the internals with an ML model while keeping the same public API
 */
@Service
public class FertilizerService {

    /**
     * Recommends fertilizer based on NPK values and crop type.
     */
    public FertilizerResponseDTO recommendFertilizer(FertilizerRequestDTO request) {
        double n = request.nitrogen();
        double p = request.phosphorus();
        double k = request.potassium();
        String crop = normalize(request.cropType());

        // Bonus: if all nutrients are extremely low, warn about soil testing.
        if (n < 10 && p < 10 && k < 10) {
            return response(
                    "Soil Test Recommended",
                    "Perform a soil test before applying fertilizers",
                    "All NPK values are extremely low. A soil test is recommended to avoid incorrect or unsafe fertilizer application."
            );
        }

        boolean lowN = n < 30;
        boolean lowP = p < 30;
        boolean lowK = k < 30;

        boolean highN = n > 80;
        boolean highP = p > 80;
        boolean highK = k > 80;

        // Crop-specific preference (customization layer)
        // Rice: generally higher nitrogen demand
        if (isCrop(crop, "rice", "paddy") && lowN) {
            return response(
                    "Urea",
                    "60 kg per hectare",
                    "Nitrogen deficiency detected for rice. Rice typically needs more nitrogen for vegetative growth."
            );
        }

        // Wheat: balanced nutrition is usually preferred
        if (isCrop(crop, "wheat") && (lowN || lowP || lowK)) {
            if (lowN && lowP && lowK) {
                return response("NPK (19:19:19)", "75 kg per hectare",
                        "Multiple nutrient deficiencies detected for wheat. A balanced NPK fertilizer can help restore levels.");
            }
            if (lowN) {
                return response("Urea", "45 kg per hectare",
                        "Nitrogen deficiency detected for wheat. Apply nitrogen to improve tillering and yield.");
            }
            if (lowP) {
                return response("DAP", "50 kg per hectare",
                        "Phosphorus deficiency detected for wheat. Phosphorus supports root development and early vigor.");
            }
            return response("MOP", "40 kg per hectare",
                    "Potassium deficiency detected for wheat. Potassium helps stress tolerance and grain filling.");
        }

        // 6–8+ meaningful conditions (general NPK-based rules)

        // 1) Balanced values -> balanced NPK fertilizer (or maintenance dose)
        if (between(n, 30, 80) && between(p, 30, 80) && between(k, 30, 80)) {
            return response(
                    "NPK (19:19:19)",
                    "50 kg per hectare",
                    "NPK values look balanced. A balanced NPK fertilizer is recommended as a maintenance dose."
            );
        }

        // 2) Low nitrogen only -> Urea
        if (lowN && !lowP && !lowK) {
            return response("Urea", quantityFor("urea", crop),
                    "Nitrogen deficiency detected. Urea is recommended to raise nitrogen levels.");
        }

        // 3) Low phosphorus only -> DAP
        if (lowP && !lowN && !lowK) {
            return response("DAP", quantityFor("dap", crop),
                    "Phosphorus deficiency detected. DAP is recommended to improve root development and early growth.");
        }

        // 4) Low potassium only -> MOP
        if (lowK && !lowN && !lowP) {
            return response("MOP", quantityFor("mop", crop),
                    "Potassium deficiency detected. MOP is recommended to improve stress tolerance and crop quality.");
        }

        // 5) Low N + Low P -> DAP + Urea (choose primary: DAP)
        if (lowN && lowP && !lowK) {
            return response("DAP + Urea", "50 kg DAP + 40 kg Urea per hectare",
                    "Nitrogen and phosphorus deficiencies detected. Use DAP for phosphorus and supplement with urea for nitrogen.");
        }

        // 6) Low N + Low K -> Urea + MOP (choose primary: Urea)
        if (lowN && lowK && !lowP) {
            return response("Urea + MOP", "40 kg Urea + 40 kg MOP per hectare",
                    "Nitrogen and potassium deficiencies detected. Apply urea for nitrogen and MOP for potassium.");
        }

        // 7) Low P + Low K -> DAP + MOP
        if (lowP && lowK && !lowN) {
            return response("DAP + MOP", "50 kg DAP + 40 kg MOP per hectare",
                    "Phosphorus and potassium deficiencies detected. Apply DAP and MOP to correct both nutrients.");
        }

        // 8) All three low -> balanced NPK higher dose
        if (lowN && lowP && lowK) {
            return response("NPK (19:19:19)", "80 kg per hectare",
                    "NPK deficiencies detected across all nutrients. A higher dose of balanced NPK is recommended.");
        }

        // 9) Excess nutrients -> advise caution
        if (highN || highP || highK) {
            StringBuilder msg = new StringBuilder("Some nutrients appear high (risk of over-fertilization). ");
            if (highN) msg.append("Nitrogen is high; ");
            if (highP) msg.append("Phosphorus is high; ");
            if (highK) msg.append("Potassium is high; ");
            msg.append("avoid heavy fertilizer application and consider soil testing / split dosing.");
            return response("Reduce Fertilizer", "Apply only after soil test", msg.toString());
        }

        // Fallback (bonus)
        return response(
                "NPK (General)",
                "40–60 kg per hectare",
                "No strong rule match found. Use a moderate balanced fertilizer dose and refine recommendations with a soil test."
        );
    }

    private static FertilizerResponseDTO response(String name, String qty, String message) {
        return new FertilizerResponseDTO(name, qty, message);
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

    private static boolean isCrop(String normalizedCrop, String... names) {
        for (String n : names) {
            if (normalizedCrop.equals(normalize(n))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crop-aware quantity heuristics.
     * Keeps "dose logic" separated so you can later replace it with an ML model or lookup tables.
     */
    private static String quantityFor(String fertilizer, String crop) {
        // Rice often needs more nitrogen than many crops.
        if ("urea".equalsIgnoreCase(fertilizer) && isCrop(crop, "rice", "paddy")) {
            return "60 kg per hectare";
        }
        if ("dap".equalsIgnoreCase(fertilizer) && isCrop(crop, "wheat")) {
            return "50 kg per hectare";
        }
        if ("mop".equalsIgnoreCase(fertilizer) && isCrop(crop, "cotton")) {
            return "45 kg per hectare";
        }
        // Default moderate dose when crop-specific guidance isn't defined yet.
        return switch (fertilizer.toLowerCase(Locale.ROOT)) {
            case "urea" -> "40 kg per hectare";
            case "dap" -> "45 kg per hectare";
            case "mop" -> "35 kg per hectare";
            default -> "40–60 kg per hectare";
        };
    }
}

