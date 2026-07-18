package com.example.demo.controller;

import com.example.demo.entity.Prediction;
import com.example.demo.service.PredictionService;   // <-- Missing import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping
    public Prediction savePrediction(@RequestBody Prediction prediction) {
        return predictionService.savePrediction(prediction);
    }

    @GetMapping
    public List<Prediction> getAllPredictions() {
        return predictionService.getAllPredictions();
    }

    @GetMapping("/{id}")
    public Prediction getPredictionById(@PathVariable Long id) {
        return predictionService.getPredictionById(id);
    }

    @PutMapping("/{id}")
    public Prediction updatePrediction(@PathVariable Long id, @RequestBody Prediction prediction) {
        return predictionService.updatePrediction(id, prediction);
    }

@DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePrediction(@PathVariable Long id) {
        predictionService.deletePrediction(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "🔮 Prediction VANISHED in mystic smoke! New visions await! ✨💨");
        return ResponseEntity.ok(response);
    }
}