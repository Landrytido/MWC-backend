package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.weather.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.url:http://api.weatherapi.com/v1}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Obtient la météo actuelle pour une localisation
     */
    public WeatherResponseDto getCurrentWeather(String location) {
        try {
            if (apiKey.isEmpty()) {
                throw new RuntimeException("WEATHER_API_KEY_MISSING");
            }

            String url = String.format("%s/current.json?key=%s&q=%s&aqi=no",
                    apiUrl, apiKey, location);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> response = responseEntity.getBody();
            return mapToCurrentWeather(response);

        } catch (HttpClientErrorException e) {
            log.error("Erreur API météo: {}", e.getMessage());
            if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("LOCATION_NOT_FOUND");
            }
            throw new RuntimeException("WEATHER_API_ERROR");
        }
    }

    /**
     * Obtient les prévisions météo pour plusieurs jours
     */
    public ForecastResponseDto getForecast(String location, int days) {
        try {
            if (apiKey.isEmpty()) {
                throw new RuntimeException("WEATHER_API_KEY_MISSING");
            }

            String url = String.format("%s/forecast.json?key=%s&q=%s&days=%d&aqi=no",
                    apiUrl, apiKey, location, days);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> response = responseEntity.getBody();
            return mapToForecast(response);

        } catch (HttpClientErrorException e) {
            log.error("Erreur API météo: {}", e.getMessage());
            if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("LOCATION_NOT_FOUND");
            }
            throw new RuntimeException("WEATHER_API_ERROR");
        }
    }

    /**
     * Recherche des villes
     */
    public List<LocationDto> searchLocations(String query) {
        try {
            if (apiKey.isEmpty()) {
                throw new RuntimeException("WEATHER_API_KEY_MISSING");
            }

            String url = String.format("%s/search.json?key=%s&q=%s",
                    apiUrl, apiKey, query);

            ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            List<Map<String, Object>> response = responseEntity.getBody();
            return mapToLocations(response);

        } catch (HttpClientErrorException e) {
            log.error("Erreur API météo: {}", e.getMessage());
            if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("LOCATION_NOT_FOUND");
            }
            throw new RuntimeException("WEATHER_API_ERROR");
        }
    }

    /**
     * Obtient la météo actuelle pour des coordonnées GPS
     */
    public WeatherResponseDto getCurrentWeatherByCoordinates(double lat, double lon) {
        try {
            if (apiKey.isEmpty()) {
                throw new RuntimeException("WEATHER_API_KEY_MISSING");
            }

            String coordinates = String.format(Locale.US, "%.6f,%.6f", lat, lon);
            String url = String.format("%s/current.json?key=%s&q=%s&aqi=no",
                    apiUrl, apiKey, coordinates);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> response = responseEntity.getBody();
            return mapToCurrentWeather(response);

        } catch (HttpClientErrorException e) {
            log.error("Erreur API météo avec coordonnées lat={}, lon={}: {}", lat, lon, e.getMessage());
            if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("INVALID_COORDINATES");
            }
            throw new RuntimeException("WEATHER_API_ERROR");
        }
    }

    /**
     * Obtient les prévisions météo pour des coordonnées GPS
     */
    public ForecastResponseDto getForecastByCoordinates(double lat, double lon, int days) {
        try {
            if (apiKey.isEmpty()) {
                throw new RuntimeException("WEATHER_API_KEY_MISSING");
            }

            String coordinates = String.format(Locale.US, "%.6f,%.6f", lat, lon);
            String url = String.format("%s/forecast.json?key=%s&q=%s&days=%d&aqi=no",
                    apiUrl, apiKey, coordinates, days);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> response = responseEntity.getBody();
            return mapToForecast(response);

        } catch (HttpClientErrorException e) {
            log.error("Erreur API météo avec coordonnées lat={}, lon={}: {}", lat, lon, e.getMessage());
            if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("INVALID_COORDINATES");
            }
            throw new RuntimeException("WEATHER_API_ERROR");
        }
    }

    // Méthodes de mapping depuis l'API WeatherAPI
    @SuppressWarnings("unchecked")
    private WeatherResponseDto mapToCurrentWeather(Map<String, Object> response) {
        WeatherResponseDto result = new WeatherResponseDto();
        result.setTimestamp(LocalDateTime.now());

        // Location
        Map<String, Object> locationData = (Map<String, Object>) response.get("location");
        LocationDto location = new LocationDto();
        location.setName((String) locationData.get("name"));
        location.setCountry((String) locationData.get("country"));
        location.setRegion((String) locationData.get("region"));
        location.setLat((Double) locationData.get("lat"));
        location.setLon((Double) locationData.get("lon"));
        result.setLocation(location);

        // Current weather
        Map<String, Object> currentData = (Map<String, Object>) response.get("current");
        CurrentWeatherDto current = new CurrentWeatherDto();
        current.setTemperature(((Number) currentData.get("temp_c")).doubleValue());
        current.setFeelsLike(((Number) currentData.get("feelslike_c")).doubleValue());
        current.setHumidity(((Number) currentData.get("humidity")).intValue());
        current.setWindSpeed(((Number) currentData.get("wind_kph")).doubleValue());
        current.setWindDirection((String) currentData.get("wind_dir"));
        current.setVisibility(((Number) currentData.get("vis_km")).doubleValue());
        current.setPressure(((Number) currentData.get("pressure_mb")).doubleValue());
        current.setUvIndex(((Number) currentData.get("uv")).intValue());

        Map<String, Object> conditionData = (Map<String, Object>) currentData.get("condition");
        current.setCondition((String) conditionData.get("text"));
        current.setConditionIcon(String.valueOf(conditionData.get("code")));

        result.setCurrent(current);
        return result;
    }

    @SuppressWarnings("unchecked")
    private ForecastResponseDto mapToForecast(Map<String, Object> response) {
        ForecastResponseDto result = new ForecastResponseDto();

        // Location (même logique que current)
        Map<String, Object> locationData = (Map<String, Object>) response.get("location");
        LocationDto location = new LocationDto();
        location.setName((String) locationData.get("name"));
        location.setCountry((String) locationData.get("country"));
        location.setRegion((String) locationData.get("region"));
        location.setLat((Double) locationData.get("lat"));
        location.setLon((Double) locationData.get("lon"));
        result.setLocation(location);

        // Forecast
        Map<String, Object> forecastData = (Map<String, Object>) response.get("forecast");
        List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecastData.get("forecastday");

        List<ForecastDayDto> forecast = new ArrayList<>();
        for (Map<String, Object> dayData : forecastDays) {
            ForecastDayDto day = new ForecastDayDto();
            day.setDate((String) dayData.get("date"));

            Map<String, Object> dayDetails = (Map<String, Object>) dayData.get("day");
            day.setMaxTemp(((Number) dayDetails.get("maxtemp_c")).doubleValue());
            day.setMinTemp(((Number) dayDetails.get("mintemp_c")).doubleValue());
            day.setChanceOfRain(((Number) dayDetails.get("daily_chance_of_rain")).intValue());
            day.setHumidity(((Number) dayDetails.get("avghumidity")).intValue());
            day.setWindSpeed(((Number) dayDetails.get("maxwind_kph")).doubleValue());

            Map<String, Object> conditionData = (Map<String, Object>) dayDetails.get("condition");
            day.setCondition((String) conditionData.get("text"));
            day.setConditionIcon(String.valueOf(conditionData.get("code")));

            forecast.add(day);
        }

        result.setForecast(forecast);
        return result;
    }

    private List<LocationDto> mapToLocations(List<Map<String, Object>> response) {
        List<LocationDto> locations = new ArrayList<>();

        for (Map<String, Object> locationData : response) {
            LocationDto location = new LocationDto();
            location.setName((String) locationData.get("name"));
            location.setCountry((String) locationData.get("country"));
            location.setRegion((String) locationData.get("region"));
            location.setLat((Double) locationData.get("lat"));
            location.setLon((Double) locationData.get("lon"));
            locations.add(location);
        }

        return locations;
    }
}
