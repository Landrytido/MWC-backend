package com.mywebcompanion.backendspring.dto.weather;

import lombok.Data;

@Data
public class CurrentWeatherDto {
    private Double temperature;
    private String condition;
    private String conditionIcon;
    private Integer humidity;
    private Double windSpeed;
    private String windDirection;
    private Double feelsLike;
    private Integer uvIndex;
    private Double visibility;
    private Double pressure;
}
