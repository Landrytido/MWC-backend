package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDate scheduledDate;
    private Integer priority;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean carriedOver;
    private LocalDate originalDate;
    private Integer orderIndex;

    private String status;
}