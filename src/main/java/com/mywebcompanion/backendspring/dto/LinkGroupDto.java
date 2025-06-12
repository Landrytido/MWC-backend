package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LinkGroupDto {
    private String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SavedLinkGroupDto> savedLinks;

    private Integer linkCount;
}