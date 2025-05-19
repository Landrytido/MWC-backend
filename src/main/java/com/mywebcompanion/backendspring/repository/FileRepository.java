package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    // Trouver tous les fichiers d'un utilisateur
    List<File> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    // Trouver un fichier par ID et utilisateur (sécurité)
    Optional<File> findByIdAndUserClerkId(Long id, String clerkId);

    // Trouver par nom de fichier et utilisateur
    Optional<File> findByFilenameAndUserClerkId(String filename, String clerkId);

    // Trouver par type de contenu
    List<File> findByUserClerkIdAndContentTypeStartingWithOrderByCreatedAtDesc(String clerkId,
            String contentTypePrefix);

    // Statistiques - taille totale des fichiers d'un utilisateur
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM File f WHERE f.user.clerkId = :clerkId")
    Long getTotalFileSizeByUserClerkId(@Param("clerkId") String clerkId);

    // Compter les fichiers par type
    @Query("SELECT f.contentType, COUNT(f) FROM File f WHERE f.user.clerkId = :clerkId GROUP BY f.contentType")
    List<Object[]> countFilesByTypeAndUserClerkId(@Param("clerkId") String clerkId);

    // Supprimer un fichier par ID et utilisateur (sécurité)
    void deleteByIdAndUserClerkId(Long id, String clerkId);
}