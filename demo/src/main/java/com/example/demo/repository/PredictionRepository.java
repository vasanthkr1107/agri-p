package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Prediction;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

	long countByUser_Id(Long userId);

	void deleteByUser_Id(Long userId);

	java.util.List<Prediction> findByUser_IdOrderByIdDesc(Long userId);
}