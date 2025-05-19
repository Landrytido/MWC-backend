package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyPlanDto {
    private Long id;
    private LocalDate date;
    private Boolean confirmed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}