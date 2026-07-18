package com.example.demo.service;

import com.example.demo.entity.Crop;
import com.example.demo.repository.CropRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Resolves ML crop strings to {@link Crop} rows even if spacing/casing differs slightly.
 */
@Service
public class CropResolutionService {

	private final CropRepository cropRepository;

	public CropResolutionService(CropRepository cropRepository) {
		this.cropRepository = cropRepository;
	}

	public Optional<Crop> resolve(String mlCropLabel) {
		if (mlCropLabel == null || mlCropLabel.isBlank()) {
			return Optional.empty();
		}
		String trimmed = mlCropLabel.trim();
		Optional<Crop> direct = cropRepository.findByName(trimmed);
		if (direct.isPresent()) {
			return direct;
		}
		String target = normalize(trimmed);
		return cropRepository.findAll().stream()
				.filter(c -> normalize(c.getName()).equals(target))
				.findFirst();
	}

	private static String normalize(String s) {
		return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
	}
}
