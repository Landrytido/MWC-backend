package com.mywebcompanion.backendspring.dto.weather;

import lombok.Data;

@Data
public class WeatherErrorDto {
    private boolean error = true;
    private String message;
    private String code;

    public WeatherErrorDto(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
