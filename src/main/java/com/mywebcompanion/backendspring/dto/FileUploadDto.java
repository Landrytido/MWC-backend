package com.mywebcompanion.backendspring.dto;

import lombok.Data;

@Data
public class FileUploadDto {
    private String filename;
    private String contentType;
    private Long fileSize;
    private String base64Data; // Pour upload via JSON
}
