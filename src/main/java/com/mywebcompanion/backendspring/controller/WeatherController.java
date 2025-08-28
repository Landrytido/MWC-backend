package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.weather.*;
import com.mywebcompanion.backendspring.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Obtient la météo actuelle pour une localisation
     * GET /api/weather/current?location=Paris
     * GET /api/weather/current?location=48.8566,2.3522
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String location) {

        try {
            log.info("Demande météo actuelle pour: {} par {}", location, userDetails.getUsername());
            WeatherResponseDto weather = weatherService.getCurrentWeather(location);
            return ResponseEntity.ok(weather);

        } catch (RuntimeException e) {
            log.error("Erreur météo actuelle: {}", e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    /**
     * Obtient les prévisions météo pour plusieurs jours
     * GET /api/weather/forecast?location=Paris&days=5
     */
    @GetMapping("/forecast")
    public ResponseEntity<?> getForecast(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String location,
            @RequestParam(defaultValue = "5") int days) {

        try {
            // Limiter entre 1 et 10 jours
            days = Math.max(1, Math.min(days, 10));

            log.info("Demande prévisions {} jours pour: {} par {}", days, location, userDetails.getUsername());
            ForecastResponseDto forecast = weatherService.getForecast(location, days);
            return ResponseEntity.ok(forecast);

        } catch (RuntimeException e) {
            log.error("Erreur prévisions météo: {}", e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    /**
     * Recherche des villes pour l'autocomplétion
     * GET /api/weather/search?q=Par
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchLocations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String q) {

        try {
            // Minimum 2 caractères pour la recherche
            if (q.length() < 2) {
                return ResponseEntity.badRequest()
                        .body(new WeatherErrorDto("Minimum 2 caractères requis", "QUERY_TOO_SHORT"));
            }

            log.info("Recherche de villes: '{}' par {}", q, userDetails.getUsername());
            List<LocationDto> locations = weatherService.searchLocations(q);
            return ResponseEntity.ok(locations);

        } catch (RuntimeException e) {
            log.error("Erreur recherche villes: {}", e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    /**
     * Endpoint de test pour vérifier le service météo
     * GET /api/weather/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getWeatherStatus(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Vérification statut météo par {}", userDetails.getUsername());

            // Test simple avec Paris
            WeatherResponseDto testWeather = weatherService.getCurrentWeather("Paris");

            return ResponseEntity.ok().body(Map.of(
                    "status", "OK",
                    "message", "Service météo fonctionnel",
                    "testLocation", testWeather.getLocation().getName(),
                    "timestamp", testWeather.getTimestamp()));

        } catch (Exception e) {
            log.error("Erreur statut météo: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "status", "ERROR",
                    "message", "Service météo indisponible",
                    "error", e.getMessage()));
        }
    }

    /**
     * Gestion centralisée des erreurs météo
     */
    private ResponseEntity<WeatherErrorDto> handleWeatherError(String errorMessage) {
        WeatherErrorDto error;

        switch (errorMessage) {
            case "LOCATION_NOT_FOUND":
                error = new WeatherErrorDto("Ville non trouvée", "LOCATION_NOT_FOUND");
                return ResponseEntity.badRequest().body(error);

            case "WEATHER_API_ERROR":
                error = new WeatherErrorDto("Service météo temporairement indisponible", "API_ERROR");
                return ResponseEntity.status(503).body(error);

            default:
                error = new WeatherErrorDto("Erreur interne", "INTERNAL_ERROR");
                return ResponseEntity.status(500).body(error);
        }
    }
}
