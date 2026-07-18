package com.example.demo.repository;

import com.example.demo.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

	/**
	 * Case-insensitive match for ML crop labels (e.g. "Rice" vs "rice").
	 */
	@Query("SELECT c FROM Crop c WHERE LOWER(TRIM(c.name)) = LOWER(TRIM(:name))")
	Optional<Crop> findByName(@Param("name") String name);

	boolean existsByNameIgnoreCase(String name);
}
