package com.example.demo.config;

import com.example.demo.entity.Crop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Full set of crop rows aligned with the Kaggle crop-recommendation dataset labels.
 * Covers all 22 crops the Random Forest model can predict, plus common extras.
 * Used by CropCatalogSeeder to backfill MySQL on first startup.
 */
public final class CropCatalog {

    private CropCatalog() {}

    public static List<Crop> defaultCrops() {
        List<Crop> list = new ArrayList<>();
        // name, suitableSoil, season, cost/acre, yield/acre, price/unit, pesticides, durationDays

        // ── Cereals & Staples ─────────────────────────────────────────────────
        add(list, "Rice",       "Loamy, Clay",         "Kharif",  "18500", "22",   "2100",
                "Neem-based spray as needed; monitor blast and sheath blight per local advisory.", 120);
        add(list, "Maize",      "Loamy, Sandy",        "Kharif",  "16000", "28",   "1650",
                "Stem borer IPM; avoid spraying during peak pollinator activity.", 100);
        add(list, "Wheat",      "Loamy, Sandy Loam",   "Rabi",    "15000", "20",   "2200",
                "Rust and aphid monitoring; seed treatment with fungicide before sowing.", 120);
        add(list, "Sugarcane",  "Loamy, Clay",         "Kharif",  "35000", "350",  "350",
                "Pyrilla / top borer management; trash mulching to conserve moisture.", 365);

        // ── Pulses ────────────────────────────────────────────────────────────
        add(list, "Chickpea",   "Loamy, Sandy",        "Rabi",    "12500", "9",    "5200",
                "Pod borer IPM; botanicals where feasible.", 130);
        add(list, "Kidneybeans","Loamy, Clay",         "Kharif",  "14000", "8",    "6800",
                "Bean fly / pod borer monitoring; rotate chemistries per label.", 95);
        add(list, "Pigeonpeas", "Sandy Loam",          "Kharif",  "13500", "7",    "7200",
                "Pod borer and wilt monitoring; avoid waterlogging.", 180);
        add(list, "Mothbeans",  "Sandy",               "Kharif",  "11000", "5",    "8500",
                "IPM for pod borer; harvest at right moisture.", 75);
        add(list, "Mungbean",   "Loamy",               "Kharif",  "12000", "6",    "7800",
                "Yellow mosaic management; seed treatment.", 70);
        add(list, "Blackgram",  "Loamy, Sandy",        "Kharif",  "11800", "6",    "8000",
                "Pod borer traps; follow local pulse IPM.", 80);
        add(list, "Lentil",     "Loamy, Sandy",        "Rabi",    "13000", "8",    "6000",
                "Rust / wilt scouting; seed treatment.", 110);

        // ── Cash Crops ────────────────────────────────────────────────────────
        add(list, "Cotton",     "Black, Clay",         "Kharif",  "55000", "12",   "6200",
                "Bollworm rotation; follow label for biotech refuge rules.", 180);
        add(list, "Jute",       "Alluvial, Loamy",     "Kharif",  "42000", "18",   "5500",
                "Stem rot / fibre quality; retting water hygiene.", 120);
        add(list, "Coffee",     "Laterite, Loamy",     "Kharif",  "180000","1.2",  "380000",
                "White stem borer / leaf rust IPM; shade management.", 730);

        // ── Fruits ────────────────────────────────────────────────────────────
        add(list, "Pomegranate","Sandy Loam",          "Kharif",  "120000","8",    "180000",
                "Fruit borer IPM; bacterial blight sanitation.", 1095);
        add(list, "Banana",     "Loamy",               "Kharif",  "95000", "28",   "18000",
                "Sigatoka / bunchy top programs; nematode management.", 300);
        add(list, "Mango",      "Loamy, Clay",         "Kharif",  "85000", "6",    "65000",
                "Hopper and powdery mildew calendar sprays per stage.", 1095);
        add(list, "Grapes",     "Sandy Loam",          "Kharif",  "180000","12",   "55000",
                "Downy mildew / thrips IPM; growth regulator per variety.", 365);
        add(list, "Watermelon", "Sandy, Loamy",        "Summer",  "78000", "180",  "18",
                "Mildew and anthracnose; respect PHI.", 90);
        add(list, "Muskmelon",  "Sandy Loam",          "Summer",  "72000", "150",  "22",
                "Powdery mildew / fruit fly IPM.", 85);
        add(list, "Apple",      "Loamy, Clay",         "Rabi",    "250000","15",   "85000",
                "Scab / codling moth program per chill hours region.", 1095);
        add(list, "Orange",     "Loamy",               "Kharif",  "140000","12",   "42000",
                "Citrus psyllid / greening region protocols; micronutrient foliars.", 1095);
        add(list, "Papaya",     "Loamy, Sandy",        "Kharif",  "95000", "40",   "4500",
                "Ringspot management; neem oil rotations.", 270);
        add(list, "Coconut",    "Laterite, Sandy Loam","Kharif",  "45000", "4500", "12",
                "Rhino beetle / red palm weevil IPM; basin irrigation discipline.", 1095);

        // ── Oilseeds & Others ─────────────────────────────────────────────────
        add(list, "Groundnut",  "Sandy Loam, Red",     "Kharif",  "22000", "15",   "4500",
                "Tikka disease / bud necrosis; seed treatment and row spacing.", 120);
        add(list, "Soybean",    "Loamy, Black",        "Kharif",  "20000", "12",   "3800",
                "Yellow mosaic / pod borer; avoid high humidity at flowering.", 100);
        add(list, "Sunflower",  "Loamy, Sandy Loam",   "Kharif",  "18000", "10",   "5500",
                "Downy mildew / necrosis; seed treatment mandatory.", 90);

        return list;
    }

    private static void add(
            List<Crop> list,
            String name, String soil, String season,
            String cost, String yield, String price,
            String pesticides, int days) {
        Crop c = new Crop();
        c.setName(name);
        c.setSuitableSoil(soil);
        c.setSeason(season);
        c.setCostPerAcre(new BigDecimal(cost));
        c.setExpectedYield(new BigDecimal(yield));
        c.setMarketPrice(new BigDecimal(price));
        c.setPesticides(pesticides);
        c.setDurationDays(days);
        list.add(c);
    }
}
