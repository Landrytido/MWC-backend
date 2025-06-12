package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReorderTasksRequest {
    private List<Long> taskIds;
    private LocalDate scheduledDate;
}