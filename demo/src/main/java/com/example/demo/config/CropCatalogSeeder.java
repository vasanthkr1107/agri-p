package com.example.demo.config;

import com.example.demo.entity.Crop;
import com.example.demo.repository.CropRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Ensures every standard ML dataset crop exists in MySQL (insert-if-missing).
 * Safe on every startup: does not duplicate or overwrite existing rows.
 */
@Component
@Order(10)
public class CropCatalogSeeder implements ApplicationRunner {

	private final CropRepository cropRepository;

	public CropCatalogSeeder(CropRepository cropRepository) {
		this.cropRepository = cropRepository;
	}

	@Override
	public void run(ApplicationArguments args) {
		for (Crop template : CropCatalog.defaultCrops()) {
			if (!cropRepository.existsByNameIgnoreCase(template.getName())) {
				cropRepository.save(copy(template));
			}
		}
	}

	private static Crop copy(Crop t) {
		Crop c = new Crop();
		c.setName(t.getName());
		c.setSuitableSoil(t.getSuitableSoil());
		c.setSeason(t.getSeason());
		c.setCostPerAcre(t.getCostPerAcre());
		c.setExpectedYield(t.getExpectedYield());
		c.setMarketPrice(t.getMarketPrice());
		c.setPesticides(t.getPesticides());
		c.setDurationDays(t.getDurationDays());
		return c;
	}
}
