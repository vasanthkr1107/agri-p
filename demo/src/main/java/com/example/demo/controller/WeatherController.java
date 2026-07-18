package com.example.demo.controller;

import com.example.demo.dto.WeatherResponseDTO;
import com.example.demo.entity.WeatherData;
import com.example.demo.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Persists manually captured weather data into the local database.
     */
    @PostMapping
    public WeatherData saveWeather(@RequestBody WeatherData data) {
        return weatherService.saveWeather(data);
    }

    /**
     * Returns all locally stored weather records.
     * This is mostly useful for internal dashboards or audits.
     */
    @GetMapping
    public List<WeatherData> getWeather() {
        return weatherService.getAllWeather();
    }

    /**
     * Real‑time weather endpoint backed by OpenWeatherMap.
     *
     * Example:
     *   GET /api/weather?city=Chennai
     *
     * Only requests that include the {@code city} query parameter are routed here,
     * to avoid conflicting with the existing "list all weather" endpoint.
     */
    @GetMapping(params = "city")
    public WeatherResponseDTO getCurrentWeather(@RequestParam("city") String city) {
        return weatherService.getCurrentWeatherByCity(city.trim());
    }

    /**
     * Prebuilt weather forecast (no model training here).
     *
     * Usage:
     * - /api/weather/forecast?location=Chennai
     * - /api/weather/forecast?latitude=13.0827&longitude=80.2707
     */
    @GetMapping("/forecast")
    public Map<String, Object> getForecast(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        if (location != null && !location.trim().isEmpty()) {
            return weatherService.getForecastByLocation(location.trim());
        }
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Provide either location, or latitude and longitude.");
        }
        return weatherService.getForecastByLatLon(latitude, longitude);
    }

    @GetMapping("/{id}")
    public WeatherData getWeatherById(@PathVariable Long id) {
        return weatherService.getWeatherById(id);
    }

    @PutMapping("/{id}")
    public WeatherData updateWeather(@PathVariable Long id, @RequestBody WeatherData data) {
        return weatherService.updateWeather(id, data);
    }

@DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteWeather(@PathVariable Long id) {
        weatherService.deleteWeather(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "⛅ Weather data SWOOSHED away! Fresh skies ahead! 🌤️🚀");
        return ResponseEntity.ok(response);
    }
}