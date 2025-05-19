package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyTaskDto {
    private Long id;
    private String uniqueTaskId;
    private String title;
    private String description;
    private LocalDate scheduledDate;
    private LocalDate originalDate;
    private Boolean carriedOver;
    private Integer orderIndex;
    private Integer priority;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}