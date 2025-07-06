package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import com.mywebcompanion.enums.ReminderType;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_reminders")
@Data
public class EventReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    private ReminderType type;

    @Column(nullable = false)
    private Integer minutesBefore;

    @Column(nullable = false)
    private Boolean sent = false;

    @Column
    private LocalDateTime scheduledFor;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
