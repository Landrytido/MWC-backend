package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private LocalDateTime dueDate; // Échéance flexible (optionnelle)

    @Column
    private LocalDate scheduledDate; // Planification quotidienne (aujourd'hui/demain)

    @Column(nullable = false)
    private Integer priority = 2; // 1=basse, 2=moyenne, 3=haute

    @Column(nullable = false)
    private Boolean completed = false;

    @Column
    private LocalDateTime completedAt; // Quand la tâche a été complétée

    // Champs pour le système de planification quotidienne
    @Column(nullable = false)
    private Boolean carriedOver = false; // Si la tâche a été reportée

    @Column
    private LocalDate originalDate; // Date originale si reportée

    @Column(nullable = false)
    private Integer orderIndex = 0; // Ordre d'affichage dans la journée

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "relatedTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private Event calendarEvent;

    public boolean hasCalendarEvent() {
        return this.calendarEvent != null;
    }

    // Champs pour les notifications (optionnels pour l'instant)
    @Column(nullable = false)
    private Boolean notificationSent = false;

    @Column(unique = true)
    private String token; // Token unique pour les notifications

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Méthodes utilitaires
    public void markAsCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    public void markAsIncomplete() {
        this.completed = false;
        this.completedAt = null;
    }

    public void carryOverTo(LocalDate newDate) {
        this.originalDate = this.originalDate != null ? this.originalDate : this.scheduledDate;
        this.scheduledDate = newDate;
        this.carriedOver = true;
    }

    public String getStatus() {
        if (this.completed) {
            return "completed";
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        if (this.scheduledDate != null) {
            if (this.scheduledDate.equals(today)) {
                return "today";
            }
            if (this.scheduledDate.equals(tomorrow)) {
                return "tomorrow";
            }
        }

        if (this.dueDate != null && this.dueDate.isBefore(LocalDateTime.now())) {
            return "overdue";
        }

        return "upcoming";
    }

    public boolean isOverdue() {
        if (this.completed) {
            return false;
        }
        return this.dueDate != null && this.dueDate.isBefore(LocalDateTime.now());
    }

    public boolean isScheduledForToday() {
        return this.scheduledDate != null && this.scheduledDate.equals(LocalDate.now());
    }

    public boolean isScheduledForTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return this.scheduledDate != null && this.scheduledDate.equals(tomorrow);
    }
}