package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_task_history")
@Data
public class DailyTaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uniqueTaskId; // Copié depuis DailyTask

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column
    private LocalDate originalDate;

    @Column(nullable = false)
    private Boolean carriedOver = false;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime archivedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt; // Date de création originale
}