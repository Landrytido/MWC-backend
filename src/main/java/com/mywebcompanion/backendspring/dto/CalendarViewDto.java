package com.mywebcompanion.backendspring.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class CalendarViewDto {
    private LocalDate date;
    private List<EventDto> events;
    private List<TaskDto> tasks;
    private Integer totalItems;
}