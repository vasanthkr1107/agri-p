package com.example.demo.service;

import com.example.demo.dto.WeatherResponseDTO;
import com.example.demo.entity.WeatherData;
import com.example.demo.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * OpenWeatherMap API base URL, injected from application properties.
     * Example: https://api.openweathermap.org/data/2.5
     */
    @Value("${weather.api.base-url}")
    private String weatherApiBaseUrl;

    /**
     * OpenWeatherMap API key, injected from application properties.
     */
    @Value("${weather.api.key}")
    private String weatherApiKey;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public WeatherData saveWeather(WeatherData data) {
        return weatherRepository.save(data);
    }

    public List<WeatherData> getAllWeather() {
        return weatherRepository.findAll();
    }

    public WeatherData getWeatherById(Long id) {
        return weatherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Weather data not found"));
    }

    public WeatherData updateWeather(Long id, WeatherData updatedData) {

        WeatherData weather = getWeatherById(id);

        weather.setLocation(updatedData.getLocation());
        weather.setTemperature(updatedData.getTemperature());
        weather.setHumidity(updatedData.getHumidity());
        weather.setCondition(updatedData.getCondition());

        return weatherRepository.save(weather);
    }

    public void deleteWeather(Long id) {
        weatherRepository.deleteById(id);
    }

    /**
     * Calls OpenWeatherMap "current weather" API for the given city and maps
     * the response into a strongly‑typed {@link WeatherResponseDTO}.
     * <p>
     * This is the primary entry point for the Crop Advisory System to obtain
     * real‑time weather data in a clean, API‑agnostic format.
     */
    public WeatherResponseDTO getCurrentWeatherByCity(String city) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(weatherApiBaseUrl + "/weather")
                    .queryParam("q", city)
                    .queryParam("appid", weatherApiKey)
                    .queryParam("units", "metric")
                    .toUriString();

            // Use a generic Map to avoid coupling to the external API's full schema.
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.isEmpty()) {
                throw new IllegalStateException("Empty weather response for city: " + city);
            }

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");

            if (main == null || weatherList == null || weatherList.isEmpty()) {
                throw new IllegalStateException("Incomplete weather data returned for city: " + city);
            }

            double temperature = toDouble(main.get("temp"));
            double humidity = toDouble(main.get("humidity"));
            String description = String.valueOf(weatherList.get(0).get("description"));
            String resolvedCityName = response.get("name") != null
                    ? String.valueOf(response.get("name"))
                    : city;

            List<String> advisories = buildWeatherAdvisories(temperature, humidity, description);

            return new WeatherResponseDTO(
                    resolvedCityName,
                    temperature,
                    humidity,
                    description,
                    advisories
            );
        } catch (RestClientException ex) {
            // Wrap low‑level HTTP errors into a domain‑specific exception message.
            throw new IllegalStateException("Failed to fetch weather for city: " + city, ex);
        } catch (ClassCastException ex) {
            // Handles unexpected response structure from the external provider.
            throw new IllegalStateException("Unexpected weather API response format for city: " + city, ex);
        }
    }

    /**
     * Simple advisory logic that turns raw weather conditions into actionable
     * farming guidance. This is where domain rules for the Crop Advisory System
     * are encoded based on temperature, humidity, and rain expectations.
     */
    private List<String> buildWeatherAdvisories(double temperature, double humidity, String description) {
        List<String> advisories = new ArrayList<>();

        // If temperature is very high, prefer crops that tolerate drought conditions.
        if (temperature > 35.0) {
            advisories.add("Temperature is above 35°C. Prefer drought-resistant crops and schedule irrigation carefully.");
        }

        // High humidity increases fungal disease risk; warn farmers accordingly.
        if (humidity > 80.0) {
            advisories.add("Humidity is above 80%. Monitor for fungal diseases and consider preventive fungicide applications.");
        }

        // If the provider expects rain, irrigation strategy should be adapted.
        if (description != null) {
            String lowerDesc = description.toLowerCase();
            if (lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || lowerDesc.contains("thunderstorm")) {
                advisories.add("Rain is expected. Reduce or postpone irrigation to avoid waterlogging and nutrient leaching.");
            }
        }

        if (advisories.isEmpty()) {
            advisories.add("Weather conditions are normal. Maintain standard crop and irrigation practices.");
        }

        return advisories;
    }

    /**
     * Utility to safely convert different numeric types returned by JSON parsing
     * into a primitive double value.
     */
    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value == null) {
            throw new IllegalStateException("Numeric value was null in weather API response");
        }
        return Double.parseDouble(value.toString());
    }

    /**
     * Uses an external, prebuilt weather forecasting model/API (Open-Meteo).
     * No ML training is performed in this project.
     *
     * Returns the raw Open-Meteo JSON (Map) so frontend/backend can decide what to display.
     */
    public Map<String, Object> getForecastByLatLon(double latitude, double longitude) {
        String url = UriComponentsBuilder
                .fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("hourly", "temperature_2m,relativehumidity_2m")
                .queryParam("daily", "weathercode,temperature_2m_max,temperature_2m_min")
                .queryParam("timezone", "auto")
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Convenience: resolve a place name to coordinates, then fetch forecast.
     */
    public Map<String, Object> getForecastByLocation(String location) {
        String geoUrl = UriComponentsBuilder
                .fromUriString("https://geocoding-api.open-meteo.com/v1/search")
                .queryParam("name", location)
                .queryParam("count", 1)
                .queryParam("language", "en")
                .queryParam("format", "json")
                .toUriString();

        Map<String, Object> geo = restTemplate.getForObject(geoUrl, Map.class);
        if (geo == null || !geo.containsKey("results")) {
            throw new RuntimeException("Unable to geocode location: " + location);
        }

        Object resultsObj = geo.get("results");
        if (!(resultsObj instanceof List<?> results) || results.isEmpty()) {
            throw new RuntimeException("Location not found: " + location);
        }

        Object firstObj = results.get(0);
        if (!(firstObj instanceof Map<?, ?> firstRaw)) {
            throw new RuntimeException("Unexpected geocoding response for: " + location);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> first = (Map<String, Object>) firstRaw;
        Object latObj = first.get("latitude");
        Object lonObj = first.get("longitude");
        if (!(latObj instanceof Number) || !(lonObj instanceof Number)) {
            throw new RuntimeException("Could not read coordinates for: " + location);
        }

        return getForecastByLatLon(((Number) latObj).doubleValue(), ((Number) lonObj).doubleValue());
    }
}