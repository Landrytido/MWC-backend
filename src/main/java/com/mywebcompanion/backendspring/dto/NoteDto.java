package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;
    private Long notebookId;
    private String notebookTitle;
    private List<LabelDto> labels;

}