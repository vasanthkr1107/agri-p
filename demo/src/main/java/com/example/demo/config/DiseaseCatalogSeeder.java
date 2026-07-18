package com.example.demo.config;

import com.example.demo.entity.Disease;
import com.example.demo.repository.DiseaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DiseaseCatalogSeeder {

    @Bean
    public CommandLineRunner seedDiseases(DiseaseRepository diseaseRepository) {
        return args -> {
            if (diseaseRepository.count() == 0) {
                List<Disease> diseases = DiseaseCatalog.defaultDiseases();
                diseaseRepository.saveAll(diseases);
                System.out.println("🌱 Seeded " + diseases.size() + " standard diseases into the catalog.");
            }
        };
    }
}
