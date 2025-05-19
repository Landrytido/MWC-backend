package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlocNoteDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}