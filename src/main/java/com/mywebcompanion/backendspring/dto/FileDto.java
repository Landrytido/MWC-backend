package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileDto {
    private Long id;
    private String filename;
    private String initialFilename;
    private String uri;
    private String contentType;
    private Long fileSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
