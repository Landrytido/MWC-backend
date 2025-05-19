package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long commentCount;
    private Long taskCount;
    private Long completedTaskCount;
    private Long notebookId;
    private String notebookTitle;
}