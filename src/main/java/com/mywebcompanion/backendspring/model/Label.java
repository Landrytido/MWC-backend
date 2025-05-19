package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "labels")
@Data
public class Label {

    @Id
    private String id; // UUID comme dans Prisma

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relation Many-to-Many avec Note
    @ManyToMany(mappedBy = "labels")
    private Set<Note> notes = new HashSet<>();

    // Contrainte unique sur (name, user) au niveau base de données
    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = { "name", "user_id" })
    })
    static class UniqueConstraint {
    }

    @PrePersist
    private void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}