package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabelDto {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optionnel : nombre de notes associées
    private Long noteCount;
}