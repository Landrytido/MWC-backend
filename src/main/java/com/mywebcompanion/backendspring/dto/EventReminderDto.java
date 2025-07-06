package com.mywebcompanion.backendspring.dto;

import java.time.LocalDateTime;

import com.mywebcompanion.enums.ReminderType;
import lombok.Data;

@Data
public class EventReminderDto {
    private Long id;
    private ReminderType type;
    private Integer minutesBefore;
    private Boolean sent;
    private LocalDateTime scheduledFor;
    private LocalDateTime createdAt;
}