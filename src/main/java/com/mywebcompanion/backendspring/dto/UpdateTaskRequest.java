package com.mywebcompanion.backendspring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    private LocalDateTime dueDate;

    private LocalDate scheduledDate;

    @Min(value = 1, message = "La priorité doit être entre 1 et 3")
    @Max(value = 3, message = "La priorité doit être entre 1 et 3")
    private Integer priority;

    private Boolean completed;

    private Boolean carriedOver;

    private LocalDate originalDate;

    private Integer orderIndex;
}