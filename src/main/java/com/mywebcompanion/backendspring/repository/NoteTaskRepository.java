package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.NoteTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteTaskRepository extends JpaRepository<NoteTask, Long> {

    // Trouver toutes les tâches d'une note (seulement les tâches parentes)
    List<NoteTask> findByNoteIdAndParentIsNullOrderByCreatedAtAsc(Long noteId);

    // Trouver toutes les tâches d'un utilisateur
    List<NoteTask> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    // Trouver toutes les tâches non complétées d'un utilisateur
    List<NoteTask> findByUserClerkIdAndCompletedFalseOrderByCreatedAtDesc(String clerkId);

    // Trouver une tâche par ID et utilisateur (sécurité)
    Optional<NoteTask> findByIdAndUserClerkId(Long id, String clerkId);

    // Compter les tâches d'une note
    Long countByNoteId(Long noteId);

    // Compter les tâches complétées d'une note
    Long countByNoteIdAndCompletedTrue(Long noteId);

    // Compter les sous-tâches d'une tâche parent
    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId")
    Long countSubtasksByParentId(@Param("parentId") Long parentId);

    // Compter les sous-tâches complétées d'une tâche parent
    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId AND nt.completed = true")
    Long countCompletedSubtasksByParentId(@Param("parentId") Long parentId);

    // Supprimer une tâche par ID et utilisateur (sécurité)
    void deleteByIdAndUserClerkId(Long id, String clerkId);
}