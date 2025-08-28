package com.mywebcompanion.backendspring.dto.weather;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WeatherResponseDto {
    private LocationDto location;
    private CurrentWeatherDto current;
    private LocalDateTime timestamp;
}
