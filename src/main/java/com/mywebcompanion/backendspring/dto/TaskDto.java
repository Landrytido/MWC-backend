package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer priority;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean carriedOver;
    private Integer orderIndex;

    private String status;
}