package com.mywebcompanion.backendspring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Le titre est requis")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    private LocalDateTime dueDate; // Date/heure unifiée

    @Min(value = 1, message = "La priorité doit être entre 1 et 3")
    @Max(value = 3, message = "La priorité doit être entre 1 et 3")
    private Integer priority = 2; // Par défaut : moyenne

    private Integer orderIndex = 0; // Ordre d'affichage
}