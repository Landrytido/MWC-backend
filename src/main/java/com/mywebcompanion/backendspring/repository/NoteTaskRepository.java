package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.NoteTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface NoteTaskRepository extends JpaRepository<NoteTask, Long> {

    // Méthodes de base
    List<NoteTask> findByNoteIdOrderByCreatedAtAsc(Long noteId);

    Long countByNoteId(Long noteId);

    Long countByNoteIdAndCompletedTrue(Long noteId);

    // Méthodes pour NoteTaskService
    List<NoteTask> findByNoteIdAndParentIsNullOrderByCreatedAtAsc(Long noteId);

    List<NoteTask> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<NoteTask> findByUserIdAndCompletedFalseOrderByCreatedAtDesc(Long userId);

    Optional<NoteTask> findByIdAndUserId(Long id, Long userId);

    // AJOUTER CES MÉTHODES MANQUANTES pour les sous-tâches
    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId")
    Long countSubtasksByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId AND nt.completed = true")
    Long countCompletedSubtasksByParentId(@Param("parentId") Long parentId);

    // Méthodes d'optimisation pour les compteurs
    @Query("SELECT nt.note.id, COUNT(nt) FROM NoteTask nt WHERE nt.note.id IN :noteIds GROUP BY nt.note.id")
    List<Object[]> findTaskCountsByNoteIdsRaw(@Param("noteIds") List<Long> noteIds);

    @Query("SELECT nt.note.id, COUNT(nt) FROM NoteTask nt WHERE nt.note.id IN :noteIds AND nt.completed = true GROUP BY nt.note.id")
    List<Object[]> findCompletedTaskCountsByNoteIdsRaw(@Param("noteIds") List<Long> noteIds);

    // Méthodes helper pour convertir les résultats en Map
    default Map<Long, Long> findTaskCountsByNoteIds(List<Long> noteIds) {
        return findTaskCountsByNoteIdsRaw(noteIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]));
    }

    default Map<Long, Long> findCompletedTaskCountsByNoteIds(List<Long> noteIds) {
        return findCompletedTaskCountsByNoteIdsRaw(noteIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]));
    }
}