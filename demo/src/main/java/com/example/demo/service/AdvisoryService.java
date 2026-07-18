package com.example.demo.service;

import com.example.demo.entity.Advisory;
import com.example.demo.repository.AdvisoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvisoryService {

    private final AdvisoryRepository advisoryRepository;

    public AdvisoryService(AdvisoryRepository advisoryRepository) {
        this.advisoryRepository = advisoryRepository;
    }

    public Advisory saveAdvisory(Advisory advisory) {
        return advisoryRepository.save(advisory);
    }

    public List<Advisory> getAllAdvisories() {
        return advisoryRepository.findAll();
    }

    public Advisory getAdvisoryById(Long id) {
        return advisoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advisory not found"));
    }

    public Advisory updateAdvisory(Long id, Advisory updatedAdvisory) {
        Advisory advisory = getAdvisoryById(id);

        advisory.setMessage(updatedAdvisory.getMessage());
        advisory.setWeatherCondition(updatedAdvisory.getWeatherCondition());
        advisory.setRecommendation(updatedAdvisory.getRecommendation());

        return advisoryRepository.save(advisory);
    }

    public void deleteAdvisory(Long id) {
        advisoryRepository.deleteById(id);
    }
}