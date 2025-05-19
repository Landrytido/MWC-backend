package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotebookDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nombre de notes dans ce carnet
    private Long noteCount;
}