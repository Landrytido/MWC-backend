package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDailyTaskDto {
    private String title;
    private String description;
    private LocalDate scheduledDate;
    private Integer priority = 1;
}