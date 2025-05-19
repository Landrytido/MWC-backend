package com.mywebcompanion.backendspring.dto;

import lombok.Data;

@Data
public class SavedLinkDetailsDto {
    private Long id;
    private String url;
    private String title;
    private String description;
    private java.time.LocalDateTime createdAt;
}
