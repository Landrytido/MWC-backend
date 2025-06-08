package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.NoteTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteTaskRepository extends JpaRepository<NoteTask, Long> {

    List<NoteTask> findByNoteIdAndParentIsNullOrderByCreatedAtAsc(Long noteId);

    List<NoteTask> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<NoteTask> findByUserIdAndCompletedFalseOrderByCreatedAtDesc(Long userId);

    Optional<NoteTask> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId")
    Long countSubtasksByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId AND nt.completed = true")
    Long countCompletedSubtasksByParentId(@Param("parentId") Long parentId);

    List<NoteTask> findByNoteIdAndUserId(Long noteId, Long userId);

    // Trouver toutes les sous-tâches d'une tâche parent
    List<NoteTask> findByParentId(Long parentId);

    // Supprimer toutes les tâches d'une note
    void deleteByNoteId(Long noteId);

}