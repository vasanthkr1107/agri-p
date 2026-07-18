package com.example.demo.config;

import com.example.demo.entity.Pesticide;

import java.util.ArrayList;
import java.util.List;

public final class PesticideCatalog {

    private PesticideCatalog() {}

    public static List<Pesticide> defaultPesticides() {
        List<Pesticide> list = new ArrayList<>();

        // name, chemicalType, dosage, sprayInterval, estimatedCost, organicAlternative, safetyPrecautions

        add(list, "Mancozeb + Cymoxanil", "Fungicide", "2.5g per liter of water", "Every 5-7 days", "₹550 per 500g", "Copper Fungicide or Bacillus subtilis", "Wear full protective clothing; Avoid spraying near water bodies; Do not harvest within 14 days of spraying.");
        add(list, "Mancozeb or Chlorothalonil", "Fungicide", "2g per liter of water", "Every 7-10 days", "₹450 per 500g", "Copper soap (Octanoate)", "Wear gloves and mask; Spray during early morning or late evening.");
        add(list, "Chlorothalonil", "Fungicide", "1.5g per liter of water", "Every 10-14 days", "₹400 per 500g", "Neem oil / Compost Tea", "Wear gloves; Wash hands thoroughly after use.");
        
        add(list, "Copper Hydroxide or Copper Oxychloride", "Bactericide / Copper-based", "2g to 3g per liter of water", "Every 5-7 days", "₹350 per 500g", "Serenade (Bacillus subtilis)", "Harmful if swallowed; Avoid contact with eyes; Can be phytotoxic in extreme heat.");

        add(list, "Sulfur-based Fungicide or Chlorothalonil", "Fungicide", "2g per liter of water", "Every 7 days", "₹300 per 500g", "Potassium Bicarbonate or Neem Oil", "Do not apply sulfur when temperatures exceed 32°C; Wear eye protection.");
        add(list, "Myclobutanil or Azoxystrobin", "Fungicide", "1.5ml per liter of water", "Every 5-7 days", "₹650 per 250ml", "Potassium Bicarbonate", "Wear chemical-resistant gloves; Toxic to aquatic life.");

        add(list, "Abamectin or Spiromesifen", "Acaricide / Miticide", "1ml per liter of water", "Every 5-7 days", "₹850 per 100ml", "Horticultural Oil / Insecticidal Soap", "Highly toxic to bees; Do not spray when bees are foraging.");
        add(list, "Propargite or Sulfur", "Acaricide / Miticide", "2ml per liter of water", "Every 7-10 days", "₹500 per 250ml", "Neem Oil Extract", "Wear long sleeves and gloves; Keep away from children and pets.");

        add(list, "Imidacloprid or Thiamethoxam (for Whiteflies/Aphids)", "Insecticide (Vector Control)", "0.5ml per liter of water", "Every 7-10 days", "₹700 per 100ml", "Neem Oil / Pyrethrin", "Systemic insecticide; Highly toxic to pollinators; Avoid spraying during bloom.");

        add(list, "Mancozeb or Copper-based", "Fungicide", "2g to 2.5g per liter of water", "Every 7-10 days", "₹450 per 500g", "Bacillus subtilis", "Wear mask and gloves; Store in a cool, dry place.");

        add(list, "Broad-spectrum Fungicide/Bactericide", "General", "Refer to manufacturer label", "Every 10-14 days", "₹300 - ₹500", "Neem Oil Extract (Azadirachtin)", "Read label thoroughly before application; Use standard PPE.");

        return list;
    }

    private static void add(List<Pesticide> list, String name, String type, String dosage, String interval, String cost, String organic, String safety) {
        list.add(new Pesticide(name, type, dosage, interval, cost, organic, safety));
    }
}
