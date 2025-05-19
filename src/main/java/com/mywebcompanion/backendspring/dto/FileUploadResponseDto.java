package com.mywebcompanion.backendspring.dto;

import lombok.Data;

@Data
public class FileUploadResponseDto {
    private Long id;
    private String filename;
    private String uri;
    private String message;
}
