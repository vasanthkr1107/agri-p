package com.example.demo.service;

import com.example.demo.dto.CropSuggestionRequest;
import com.example.demo.dto.CropSuggestionResponse;
import com.example.demo.dto.MlRecommendCropResponse;
import com.example.demo.entity.Crop;
import com.example.demo.entity.CropPredictionHistory;
import com.example.demo.entity.User;
import com.example.demo.repository.CropPredictionHistoryRepository;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final CropMLService cropMLService;
    private final SoilSeasonWaterService soilSeasonWaterService;
    private final CropResolutionService cropResolutionService;
    private final CropPredictionHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public CropService(
            CropRepository cropRepository,
            CropMLService cropMLService,
            SoilSeasonWaterService soilSeasonWaterService,
            CropResolutionService cropResolutionService,
            CropPredictionHistoryRepository historyRepository,
            UserRepository userRepository) {
        this.cropRepository = cropRepository;
        this.cropMLService = cropMLService;
        this.soilSeasonWaterService = soilSeasonWaterService;
        this.cropResolutionService = cropResolutionService;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    public Crop saveCrop(Crop crop) {
        return cropRepository.save(crop);
    }

    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    public Crop getCropById(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found"));
    }

    public Crop updateCrop(Long id, Crop updatedCrop) {
        Crop crop = getCropById(id);
        crop.setName(updatedCrop.getName());
        crop.setSeason(updatedCrop.getSeason());
        crop.setSuitableSoil(updatedCrop.getSuitableSoil());
        crop.setCostPerAcre(updatedCrop.getCostPerAcre());
        crop.setExpectedYield(updatedCrop.getExpectedYield());
        crop.setMarketPrice(updatedCrop.getMarketPrice());
        crop.setPesticides(updatedCrop.getPesticides());
        crop.setDurationDays(updatedCrop.getDurationDays());
        return cropRepository.save(crop);
    }

    public void deleteCrop(Long id) {
        cropRepository.deleteById(id);
    }

    /**
     * Full ML pipeline:
     *  1. Send agronomic inputs to Python FastAPI → get predicted crop + confidence + reason + cost + profit
     *  2. Enrich with DB crop profile (pesticides, duration, water, calculated cost/profit)
     *  3. Save to prediction history (linked to user)
     *  4. Return enriched response
     */
    public CropSuggestionResponse suggestFromMl(CropSuggestionRequest request, Long userId) {
        // ── Step 1: Call ML service ───────────────────────────────────────────
        final MlRecommendCropResponse ml;
        try {
            ml = cropMLService.fetchRecommendation(request);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex);
        }

        String cropName = ml.predictedCrop();

        // ── Step 2: Resolve crop from DB ──────────────────────────────────────
        Crop crop = cropResolutionService.resolve(cropName).orElse(null);

        BigDecimal totalCost = null;
        BigDecimal profit = null;
        String pesticides = null;
        Integer duration = null;
        String soilSuitability = null;

        if (crop != null) {
            double area = request.landAreaOrDefault();
            BigDecimal areaDecimal = BigDecimal.valueOf(area);

            if (crop.getCostPerAcre() != null) {
                totalCost = crop.getCostPerAcre().multiply(areaDecimal).setScale(2, RoundingMode.HALF_UP);
            }
            if (crop.getExpectedYield() != null && crop.getMarketPrice() != null) {
                BigDecimal revenue = crop.getExpectedYield()
                        .multiply(crop.getMarketPrice())
                        .multiply(areaDecimal)
                        .setScale(2, RoundingMode.HALF_UP);
                if (totalCost != null) {
                    profit = revenue.subtract(totalCost).setScale(2, RoundingMode.HALF_UP);
                }
            }
            pesticides = crop.getPesticides();
            duration = crop.getDurationDays();
            soilSuitability = buildSoilSuitability(crop, request.soilType());
        }

        // Water guidance: use soil+season or fallback to climate-based
        String water = soilSeasonWaterService.resolveFromClimate(
                request.temperature(), request.humidity(), request.rainfall());

        String budgetNote = buildBudgetNote(request.budgetOrDefault(), cropName);
        String note = recommendationNote(ml.confidence());

        // ── Step 3: Save to history ───────────────────────────────────────────
        if (userId != null) {
            saveHistory(request, ml, userId);
        }

        // ── Step 4: Build response ────────────────────────────────────────────
        return new CropSuggestionResponse(
                cropName,
                ml.confidence(),
                ml.reason(),
                totalCost,
                profit,
                ml.expectedProfit(),
                ml.estimatedCost(),
                pesticides,
                duration,
                water,
                soilSuitability,
                budgetNote,
                note
        );
    }

    private void saveHistory(CropSuggestionRequest request, MlRecommendCropResponse ml, Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return;

            CropPredictionHistory history = new CropPredictionHistory();
            history.setUser(user);
            history.setNitrogen(request.nitrogen());
            history.setPhosphorus(request.phosphorus());
            history.setPotassium(request.potassium());
            history.setTemperature(request.temperature());
            history.setHumidity(request.humidity());
            history.setRainfall(request.rainfall());
            history.setPh(request.phOrDefault());
            history.setSoilType(request.soilType());
            history.setBudget(request.budgetOrDefault());
            history.setLandArea(request.landAreaOrDefault());
            history.setLocation(request.location());
            history.setPredictedCrop(ml.predictedCrop());
            history.setConfidence(ml.confidence());
            history.setReason(ml.reason());
            history.setEstimatedCost(ml.estimatedCost());
            history.setExpectedProfit(ml.expectedProfit());
            historyRepository.save(history);
        } catch (Exception e) {
            // Never fail the recommendation because history save failed
        }
    }

    private static String buildSoilSuitability(Crop crop, String requestedSoil) {
        if (crop.getSuitableSoil() == null || crop.getSuitableSoil().isBlank()) {
            return null;
        }
        String suitable = crop.getSuitableSoil();
        boolean matches = suitable.toLowerCase().contains(
                requestedSoil != null ? requestedSoil.toLowerCase() : "");
        if (matches) {
            return requestedSoil + " soil is among the suitable soil types for " + crop.getName()
                    + " (" + suitable + ").";
        }
        return crop.getName() + " typically grows best in " + suitable
                + ". Your " + requestedSoil + " soil may need amendments.";
    }

    private static String buildBudgetNote(String budget, String crop) {
        return switch (budget.toLowerCase()) {
            case "low" -> "Low budget: focus on minimal inputs. " + crop
                    + " can still be viable — prioritize seed quality and basic fertilizers.";
            case "high" -> "High budget: consider premium seeds, drip irrigation, and soil health investments for " + crop + ".";
            default -> "Medium budget: balanced investment in quality seeds, fertilizers, and basic pest management for " + crop + ".";
        };
    }

    private static String recommendationNote(Double confidence) {
        if (confidence == null) return null;
        if (confidence < 0.25) {
            return "Model confidence is very low — use as exploratory guidance only; validate with soil tests and local agronomy.";
        }
        if (confidence < 0.45) {
            return "Moderate model confidence — cross-check with regional advisories before committing inputs.";
        }
        return null;
    }
}
