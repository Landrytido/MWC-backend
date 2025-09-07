package com.mywebcompanion.backendspring.dto;

import java.time.LocalDateTime;

import com.mywebcompanion.enums.EventMode;
import com.mywebcompanion.enums.EventType;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Le titre est requis")
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "La date de début est requise")
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est requise")
    private LocalDateTime endDate;

    @Size(max = 255)
    private String location;

    private EventMode mode;

    @Size(max = 500)
    private String meetingLink;

    private EventType type = EventType.EVENT;

    private Long relatedTaskId;
}
