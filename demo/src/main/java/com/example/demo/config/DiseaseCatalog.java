package com.example.demo.config;

import com.example.demo.entity.Disease;

import java.util.ArrayList;
import java.util.List;

public final class DiseaseCatalog {

    private DiseaseCatalog() {}

    public static List<Disease> defaultDiseases() {
        List<Disease> list = new ArrayList<>();

        // ── Tomato Diseases ───────────────────────────────────────────────────
        add(list, "Tomato", "Early blight",
                "Fungal disease affecting tomato leaves, characterized by dark concentric rings.",
                "Dark, concentric rings on older leaves, yellowing of surrounding tissue.",
                "1. Remove and destroy infected leaves. 2. Apply fungicide. 3. Ensure good airflow.",
                "Chlorothalonil, Mancozeb, or Copper-based fungicides.",
                "Rotate crops, avoid overhead watering, ensure adequate spacing.");

        add(list, "Tomato", "Late blight",
                "Severe disease caused by Phytophthora infestans that can destroy entire crops quickly.",
                "Large, irregular water-soaked spots on leaves; white fungal growth on undersides in humid conditions.",
                "1. Immediately remove infected plants. 2. Apply targeted systemic fungicide.",
                "Chlorothalonil, Copper sprays, or Mancozeb.",
                "Plant resistant varieties, avoid overhead watering, space plants properly.");

        add(list, "Tomato", "Bacterial spot",
                "Bacterial disease causing spotting on leaves and fruit, reducing yield.",
                "Small, water-soaked spots that turn black and angular. Leaves may yellow and drop.",
                "1. Prune affected parts. 2. Spray copper bactericides.",
                "Copper-based bactericides.",
                "Use certified disease-free seeds, avoid working in wet fields.");

        add(list, "Tomato", "Leaf Mold",
                "Fungal disease mostly affecting plants in high humidity environments (greenhouses).",
                "Pale green or yellow spots on upper leaf surfaces, olive-green to brown mold on undersides.",
                "1. Improve ventilation. 2. Reduce humidity. 3. Apply appropriate fungicides.",
                "Chlorothalonil or Copper fungicides.",
                "Provide adequate spacing, stake plants, avoid wetting leaves.");

        add(list, "Tomato", "Septoria leaf spot",
                "Common fungal disease causing numerous spots on leaves, leading to defoliation.",
                "Small, circular spots with dark borders and gray centers on lower leaves.",
                "1. Remove infected lower leaves. 2. Apply fungicide regularly.",
                "Chlorothalonil or Mancozeb.",
                "Mulch to prevent soil splash, rotate crops, water at the base.");

        add(list, "Tomato", "Spider mites Two spotted spider mite",
                "Tiny pests that suck sap from plant cells, causing stippling and webbing.",
                "Tiny yellow or white speckles on leaves, fine webbing under leaves, leaves turning bronze or yellow.",
                "1. Spray with water to dislodge mites. 2. Apply insecticidal soap or neem oil.",
                "Neem oil, Insecticidal soaps, or specific miticides.",
                "Keep plants adequately watered, introduce beneficial predatory mites.");

        add(list, "Tomato", "Target Spot",
                "Fungal disease causing target-like lesions on leaves and fruit.",
                "Dark brown lesions with concentric rings, often resembling a target.",
                "1. Remove affected leaves. 2. Apply fungicide.",
                "Chlorothalonil, Mancozeb.",
                "Improve air circulation, avoid overhead watering.");

        add(list, "Tomato", "Tomato YellowLeaf Curl Virus",
                "Devastating viral disease transmitted by whiteflies.",
                "Upward curling of leaves, yellowing (chlorosis) of leaf margins, severe stunting.",
                "1. Remove and destroy infected plants immediately. There is no cure.",
                "Insecticides for whitefly control (e.g., Imidacloprid, Neem oil).",
                "Control whitefly populations, use reflective mulches, plant resistant varieties.");

        add(list, "Tomato", "Tomato mosaic virus",
                "Highly contagious viral disease affecting tomatoes and related crops.",
                "Mottled light and dark green leaves, stunted growth, sometimes fern-like leaves.",
                "1. Remove infected plants. 2. Wash hands and tools thoroughly.",
                "None (viral disease).",
                "Disinfect tools, avoid tobacco use near plants, use resistant varieties.");

        add(list, "Tomato", "healthy",
                "The tomato plant is healthy with no visible signs of disease.",
                "Vibrant green leaves, robust growth, no unusual spots or discoloration.",
                "Continue routine care and monitoring.",
                "None needed. Use organic preventative sprays if desired.",
                "Maintain proper watering, nutrition, and sunlight.");

        // ── Potato Diseases ───────────────────────────────────────────────────
        add(list, "Potato", "Early blight",
                "Fungal disease causing target-like spots on potato leaves.",
                "Dark brown to black spots with concentric rings on older leaves.",
                "1. Apply fungicides when symptoms first appear. 2. Ensure good crop nutrition.",
                "Chlorothalonil, Mancozeb.",
                "Crop rotation, eradicate weed hosts, harvest mature tubers carefully.");

        add(list, "Potato", "Late blight",
                "Destructive disease caused by water mold, same as Irish potato famine.",
                "Irregular dark lesions on leaves, often with a white moldy ring in wet conditions; rotting tubers.",
                "1. Destroy infected plants immediately. 2. Apply systemic fungicides.",
                "Mancozeb, Copper-based sprays.",
                "Plant certified seed potatoes, hill up soil around stems, avoid overhead irrigation.");

        add(list, "Potato", "healthy",
                "The potato plant is healthy with no visible signs of disease.",
                "Evenly green foliage, upright stems, no lesions.",
                "Continue routine care and monitoring.",
                "None needed.",
                "Proper hilling, consistent watering, regular scouting.");

        // ── Pepper Diseases ───────────────────────────────────────────────────
        add(list, "Pepper bell", "Bacterial spot",
                "Bacterial infection causing spotting and defoliation on bell peppers.",
                "Small, water-soaked spots on leaves that turn dark brown; leaves may yellow and drop.",
                "1. Spray with copper bactericide. 2. Remove severely infected plants.",
                "Copper fungicides.",
                "Use pathogen-free seeds, avoid overhead watering, practice crop rotation.");

        add(list, "Pepper bell", "healthy",
                "The bell pepper plant is healthy with no visible signs of disease.",
                "Glossy green leaves, strong stems, no spots or yellowing.",
                "Continue routine care and monitoring.",
                "None needed.",
                "Provide adequate sunlight, consistent moisture, and balanced fertilizer.");

        return list;
    }

    private static void add(List<Disease> list, String plant, String disease, String desc, String sym, String treat, String pest, String prev) {
        list.add(new Disease(plant, disease, desc, sym, treat, pest, prev));
    }
}
