package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.weather.*;
import com.mywebcompanion.backendspring.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(@RequestParam String location) {

        try {
            log.info("Demande météo actuelle pour: {}", location);
            WeatherResponseDto weather = weatherService.getCurrentWeather(location);
            return ResponseEntity.ok(weather);

        } catch (RuntimeException e) {
            log.error("Erreur météo actuelle: {}", e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    @GetMapping("/forecast")
    public ResponseEntity<?> getForecast(
            @RequestParam String location,
            @RequestParam(defaultValue = "5") int days) {

        try {
            days = Math.max(1, Math.min(days, 10));

            log.info("Demande prévisions {} jours pour: {}", days, location);
            ForecastResponseDto forecast = weatherService.getForecast(location, days);
            return ResponseEntity.ok(forecast);

        } catch (RuntimeException e) {
            log.error("Erreur prévisions météo: {}", e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLocations(@RequestParam String q) {

        try {
            if (q.length() < 2) {
                return ResponseEntity.badRequest()
                        .body(new WeatherErrorDto("Minimum 2 caractères requis", "QUERY_TOO_SHORT"));
            }

            log.info("Recherche de villes: '{}'", q);
            List<LocationDto> locations = weatherService.searchLocations(q);
            return ResponseEntity.ok(locations);

        } catch (RuntimeException e) {
            log.error("Erreur recherche villes: {}", e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    @GetMapping("/current/coordinates")
    public ResponseEntity<?> getCurrentWeatherByCoordinates(
            @RequestParam double lat,
            @RequestParam double lon) {

        try {
            // Validation des coordonnées
            if (lat < -90 || lat > 90) {
                return ResponseEntity.badRequest()
                        .body(new WeatherErrorDto("Latitude invalide (doit être entre -90 et 90)", "INVALID_LATITUDE"));
            }
            if (lon < -180 || lon > 180) {
                return ResponseEntity.badRequest()
                        .body(new WeatherErrorDto("Longitude invalide (doit être entre -180 et 180)",
                                "INVALID_LONGITUDE"));
            }

            log.info("Demande météo actuelle pour coordonnées: lat={}, lon={}", lat, lon);
            WeatherResponseDto weather = weatherService.getCurrentWeatherByCoordinates(lat, lon);
            return ResponseEntity.ok(weather);

        } catch (RuntimeException e) {
            log.error("Erreur météo actuelle avec coordonnées lat={}, lon={}: {}", lat, lon, e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    @GetMapping("/forecast/coordinates")
    public ResponseEntity<?> getForecastByCoordinates(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") int days) {

        try {
            // Validation des coordonnées
            if (lat < -90 || lat > 90) {
                return ResponseEntity.badRequest()
                        .body(new WeatherErrorDto("Latitude invalide (doit être entre -90 et 90)", "INVALID_LATITUDE"));
            }
            if (lon < -180 || lon > 180) {
                return ResponseEntity.badRequest()
                        .body(new WeatherErrorDto("Longitude invalide (doit être entre -180 et 180)",
                                "INVALID_LONGITUDE"));
            }

            // Validation du nombre de jours
            days = Math.max(1, Math.min(days, 10));

            log.info("Demande prévisions {} jours pour coordonnées: lat={}, lon={}", days, lat, lon);
            ForecastResponseDto forecast = weatherService.getForecastByCoordinates(lat, lon, days);
            return ResponseEntity.ok(forecast);

        } catch (RuntimeException e) {
            log.error("Erreur prévisions météo avec coordonnées lat={}, lon={}: {}", lat, lon, e.getMessage());
            return handleWeatherError(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getWeatherStatus() {
        try {
            log.info("Vérification statut météo");

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

    private ResponseEntity<WeatherErrorDto> handleWeatherError(String errorMessage) {
        WeatherErrorDto error;

        switch (errorMessage) {
            case "LOCATION_NOT_FOUND":
                error = new WeatherErrorDto("Ville non trouvée", "LOCATION_NOT_FOUND");
                return ResponseEntity.badRequest().body(error);

            case "INVALID_COORDINATES":
                error = new WeatherErrorDto("Coordonnées GPS invalides", "INVALID_COORDINATES");
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
