package com.mywebcompanion.backendspring.dto.weather;

import lombok.Data;
import java.util.List;

@Data
public class ForecastResponseDto {
    private LocationDto location;
    private List<ForecastDayDto> forecast;
}
