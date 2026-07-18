package com.example.demo.repository;

import com.example.demo.entity.CropPredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropPredictionHistoryRepository extends JpaRepository<CropPredictionHistory, Long> {

    /** All predictions for a specific user, newest first */
    List<CropPredictionHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** All predictions newest first (admin use) */
    List<CropPredictionHistory> findAllByOrderByCreatedAtDesc();
}
