package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.entity.Disease;
import com.example.demo.service.DiseaseService;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @PostMapping
    public Disease addDisease(@RequestBody Disease disease) {
        return diseaseService.saveDisease(disease);
    }

    @GetMapping
    public List<Disease> getAllDiseases() {
        return diseaseService.getAllDiseases();
    }

    @GetMapping("/{id}")
    public Disease getDiseaseById(@PathVariable Long id) {
        return diseaseService.getDiseaseById(id);
    }

    @PutMapping("/{id}")
    public Disease updateDisease(@PathVariable Long id, @RequestBody Disease disease) {
        return diseaseService.updateDisease(id, disease);
    }

@DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDisease(@PathVariable Long id) {
        diseaseService.deleteDisease(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "🩺 Disease OBLITERATED! Your crops are INVINCIBLE now! 💥🔥");
        return ResponseEntity.ok(response);
    }
}