package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

/**
 * Agronomic irrigation / water guidance keyed by soil type and season from the user request.
 * Complements ML crop choice with context-specific water management notes.
 */
@Service
public class SoilSeasonWaterService {

	private static final Map<String, Map<String, String>> GRID = Map.of(
			key("Loamy"), Map.of(
					key("Kharif"),
					"Loamy soil in Kharif (monsoon): holds moisture well. Rely on rainfall when adequate; "
							+ "supplement only if topsoil dries beyond ~10–12 cm between rains. "
							+ "Avoid over-irrigation—improve field drains to prevent waterlogging.",
					key("Rabi"),
					"Loamy soil in Rabi (cool dry): schedule irrigation to keep root zone near 60–80% of field capacity. "
							+ "Typically fewer, well-timed irrigations than sandy soils; prefer morning application to reduce disease pressure.",
					key("Summer"),
					"Loamy soil in Summer: high evapotranspiration. Irrigate in early morning or evening; "
							+ "maintain uniform moisture during flowering/grain-fill; use mulch where possible to cut surface evaporation."),
			key("Clay"), Map.of(
					key("Kharif"),
					"Clay soil in Kharif: slow infiltration and high waterlogging risk during heavy rain. "
							+ "Prioritize surface drainage and bund management; delay irrigation until field trafficability returns after storms.",
					key("Rabi"),
					"Clay soil in Rabi: high water-holding capacity—fewer, deeper irrigations often suffice. "
							+ "Avoid ponding; crack formation indicates long dry spells—re-wet gradually to reduce runoff.",
					key("Summer"),
					"Clay soil in Summer: surface cracks increase water loss; irrigate to refill the full root zone less often but thoroughly. "
							+ "Monitor salinity if using poor-quality groundwater."),
			key("Sandy"), Map.of(
					key("Kharif"),
					"Sandy soil in Kharif: drains fast—rain gaps may need short, frequent irrigations. "
							+ "Organic matter and mulch help hold moisture between monsoon pulses.",
					key("Rabi"),
					"Sandy soil in Rabi: low water retention—more frequent lighter irrigations. "
							+ "Drip or sprinkler improves efficiency; avoid deep percolation losses from flood irrigation.",
					key("Summer"),
					"Sandy soil in Summer: highest irrigation demand. Prefer drip/micro-sprinkler; irrigate often enough to prevent crop stress "
							+ "without leaching nutrients—split doses and combine with mulching."));

	private static String key(String s) {
		return s.toLowerCase(Locale.ROOT).trim();
	}

	public String resolve(String soilType, String season) {
		String soil = key(soilType == null ? "" : soilType);
		String sea = key(season == null ? "" : season);
		Map<String, String> row = GRID.get(soil);
		if (row == null) {
			return defaultGuidance(soilType, season);
		}
		String text = row.get(sea);
		if (text == null) {
			return defaultGuidance(soilType, season);
		}
		return text;
	}

	public String resolveFromClimate(double temperature, double humidity, double rainfall) {
		if (rainfall >= 180) {
			return "High-rainfall profile: prioritize drainage and avoid over-irrigation. "
					+ "Irrigate only when root-zone moisture drops, and watch foliar disease risk in humid conditions.";
		}
		if (rainfall <= 40 && temperature >= 32) {
			return "Hot and dry profile: irrigation demand is high. Use frequent, efficient irrigation "
					+ "(prefer drip/micro), reduce midday losses, and protect soil with mulch.";
		}
		if (humidity >= 75) {
			return "Humid profile: maintain moderate irrigation, avoid prolonged leaf wetness, "
					+ "and improve field airflow/drainage to reduce disease pressure.";
		}
		return "Moderate climate profile: schedule irrigation by crop stage and measured root-zone moisture. "
				+ "Avoid both water stress and waterlogging; adjust interval with local weather updates.";
	}

	private static String defaultGuidance(String soilType, String season) {
		return String.format(
				Locale.ROOT,
				"General guidance for %s soil in %s season: match irrigation to crop stage, "
						+ "check soil moisture at root depth (15–30 cm), prefer morning irrigation, "
						+ "and adjust for local rainfall and groundwater quality.",
				soilType != null && !soilType.isBlank() ? soilType : "your",
				season != null && !season.isBlank() ? season : "this");
	}
}
