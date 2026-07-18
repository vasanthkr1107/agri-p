package com.example.demo.service;

import com.example.demo.entity.Prediction;
import com.example.demo.repository.PredictionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;

    public PredictionService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    // Save prediction
    public Prediction savePrediction(Prediction prediction) {
        return predictionRepository.save(prediction);
    }

    // Get all predictions
    public List<Prediction> getAllPredictions() {
        return predictionRepository.findAll();
    }

    // Get prediction by ID
    public Prediction getPredictionById(Long id) {
        return predictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prediction not found"));
    }

    // Update prediction
    public Prediction updatePrediction(Long id, Prediction updatedPrediction) {

        Prediction prediction = getPredictionById(id);

        prediction.setImagePath(updatedPrediction.getImagePath());
        prediction.setPredictedDisease(updatedPrediction.getPredictedDisease());
        prediction.setConfidence(updatedPrediction.getConfidence());
        prediction.setUser(updatedPrediction.getUser());

        return predictionRepository.save(prediction);
    }

    // Delete prediction
    public void deletePrediction(Long id) {
        predictionRepository.deleteById(id);
    }
}