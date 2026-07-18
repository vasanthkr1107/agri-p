package com.example.demo.config;

import com.example.demo.entity.Pesticide;
import com.example.demo.repository.PesticideRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PesticideCatalogSeeder {

    @Bean
    public CommandLineRunner seedPesticides(PesticideRepository pesticideRepository) {
        return args -> {
            if (pesticideRepository.count() == 0) {
                List<Pesticide> pesticides = PesticideCatalog.defaultPesticides();
                pesticideRepository.saveAll(pesticides);
                System.out.println("🧪 Seeded " + pesticides.size() + " standard pesticides into the catalog.");
            }
        };
    }
}
