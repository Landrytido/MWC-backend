package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteTaskDto {
    private Long id;
    private String title;
    private Boolean completed;
    private Long noteId;
    private Long parentId;
    private List<NoteTaskDto> subtasks;
    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Statistiques pour les tâches avec sous-tâches
    private Long totalSubtasks;
    private Long completedSubtasks;
}