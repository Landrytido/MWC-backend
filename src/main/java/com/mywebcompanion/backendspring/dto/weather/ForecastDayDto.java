package com.mywebcompanion.backendspring.dto.weather;

import lombok.Data;

@Data
public class ForecastDayDto {
    private String date;
    private Double maxTemp;
    private Double minTemp;
    private String condition;
    private String conditionIcon;
    private Integer chanceOfRain;
    private Integer humidity;
    private Double windSpeed;
}
