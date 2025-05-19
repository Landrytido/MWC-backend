package com.mywebcompanion.backendspring.dto;

import lombok.Data;

@Data
public class MonthlyReportDto {
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer notCompletedTasks;
    private Double completionPercentage;
}