package com.mywebcompanion.backendspring.dto;

import java.time.LocalDateTime;

import com.mywebcompanion.enums.EventMode;
import com.mywebcompanion.enums.EventType;

import lombok.Data;

@Data
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private EventMode mode;
    private String meetingLink;
    private EventType type;
    private LocalDateTime dueDate;
    private Integer taskPriority;
    private Long relatedTaskId;
    private String relatedTaskTitle;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}