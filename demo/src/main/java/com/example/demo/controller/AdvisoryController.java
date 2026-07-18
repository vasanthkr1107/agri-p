package com.example.demo.controller;

import com.example.demo.entity.Advisory;
import com.example.demo.service.AdvisoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/advisories")
public class AdvisoryController {

    private final AdvisoryService advisoryService;

    public AdvisoryController(AdvisoryService advisoryService) {
        this.advisoryService = advisoryService;
    }

    @PostMapping
    public Advisory addAdvisory(@RequestBody Advisory advisory) {
        return advisoryService.saveAdvisory(advisory);
    }

    @GetMapping
    public List<Advisory> getAllAdvisories() {
        return advisoryService.getAllAdvisories();
    }

    @GetMapping("/{id}")
    public Advisory getAdvisoryById(@PathVariable Long id) {
        return advisoryService.getAdvisoryById(id);
    }

    @PutMapping("/{id}")
    public Advisory updateAdvisory(@PathVariable Long id, @RequestBody Advisory advisory) {
        return advisoryService.updateAdvisory(id, advisory);
    }

@DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAdvisory(@PathVariable Long id) {
        advisoryService.deleteAdvisory(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "📋 Advisory EXPLODED into wisdom dust! Farm superpowers incoming! 💥📚");
        return ResponseEntity.ok(response);
    }
}