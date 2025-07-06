package com.mywebcompanion.backendspring.dto;

import com.mywebcompanion.enums.ReminderType;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateReminderRequest {
    @NotNull
    private ReminderType type;

    @NotNull
    @Min(1)
    private Integer minutesBefore;
}