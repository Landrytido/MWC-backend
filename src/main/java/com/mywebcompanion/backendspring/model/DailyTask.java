package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_tasks")
@Data
public class DailyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uniqueTaskId; // UUID pour tracking

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column
    private LocalDate originalDate; // Date originale si reportée

    @Column(nullable = false)
    private Boolean carriedOver = false;

    @Column(nullable = false)
    private Integer orderIndex = 0; // Pour l'ordre d'affichage

    @Column(nullable = false)
    private Integer priority = 1; // 1=basse, 2=moyenne, 3=haute

    @Column(nullable = false)
    private Boolean completed = false;

    @Column
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void generateUniqueTaskId() {
        if (this.uniqueTaskId == null) {
            this.uniqueTaskId = java.util.UUID.randomUUID().toString();
        }
    }
}