package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class TaskStatsDto {
    private Integer month;
    private Integer year;
    private Long totalTasks;
    private Long completedTasks;
    private Long notCompletedTasks;
    private Double completionPercentage;

    // Statistiques par priorité
    private Map<Integer, PriorityStats> tasksByPriority;

    // Données pour graphiques quotidiens
    private Map<LocalDate, DailyStats> dailyStats;

    @Data
    public static class PriorityStats {
        private Long total;
        private Long completed;
        private Double completionRate;
    }

    @Data
    public static class DailyStats {
        private LocalDate date;
        private Long total;
        private Long completed;
        private Double completionRate;
    }
}