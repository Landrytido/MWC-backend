package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "labels", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "user_id" })
})
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Label {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "labels")
    private Set<Note> notes = new HashSet<>();

    @PrePersist
    private void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}