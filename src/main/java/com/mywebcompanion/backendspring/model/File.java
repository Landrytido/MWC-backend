package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String initialFilename; // Nom original du fichier uploadé

    @Column(nullable = false)
    private String path; // Chemin sur le serveur/stockage

    @Column(nullable = false)
    private String uri; // URL d'accès public

    @Column
    private String contentType; // MIME type (image/jpeg, application/pdf, etc.)

    @Column
    private Long fileSize; // Taille en bytes

    // Relations optionnelles (pour associer les fichiers)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Propriétaire du fichier

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}