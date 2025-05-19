package com.mywebcompanion.backendspring.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReorderDailyTasksDto {
    private List<Long> orderedIds;
}