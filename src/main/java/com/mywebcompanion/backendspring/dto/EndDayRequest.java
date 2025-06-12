package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EndDayRequest {
    private LocalDate date;
    private List<Long> taskIdsToCarryOver;
    private Boolean markDayAsCompleted = true;
}
