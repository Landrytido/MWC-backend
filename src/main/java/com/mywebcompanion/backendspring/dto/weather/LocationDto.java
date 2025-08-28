package com.mywebcompanion.backendspring.dto.weather;

import lombok.Data;

@Data
public class LocationDto {
    private String name;
    private String country;
    private String region;
    private Double lat;
    private Double lon;
}
