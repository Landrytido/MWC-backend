package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.NoteTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NoteTaskRepository extends JpaRepository<NoteTask, Long> {

    List<NoteTask> findByNoteIdAndParentIsNullOrderByCreatedAtAsc(Long noteId);

    List<NoteTask> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    List<NoteTask> findByUserClerkIdAndCompletedFalseOrderByCreatedAtDesc(String clerkId);

    Optional<NoteTask> findByIdAndUserClerkId(Long id, String clerkId);

    Long countByNoteId(Long noteId);

    Long countByNoteIdAndCompletedTrue(Long noteId);

    @Query("SELECT nt.note.id, COUNT(nt) FROM NoteTask nt WHERE nt.note.id IN :noteIds GROUP BY nt.note.id")
    List<Object[]> countTasksByNoteIds(@Param("noteIds") List<Long> noteIds);

    @Query("SELECT nt.note.id, COUNT(nt) FROM NoteTask nt WHERE nt.note.id IN :noteIds AND nt.completed = true GROUP BY nt.note.id")
    List<Object[]> countCompletedTasksByNoteIds(@Param("noteIds") List<Long> noteIds);

    default Map<Long, Long> findTaskCountsByNoteIds(List<Long> noteIds) {
        return countTasksByNoteIds(noteIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
    }

    default Map<Long, Long> findCompletedTaskCountsByNoteIds(List<Long> noteIds) {
        return countCompletedTasksByNoteIds(noteIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
    }

    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId")
    Long countSubtasksByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(nt) FROM NoteTask nt WHERE nt.parent.id = :parentId AND nt.completed = true")
    Long countCompletedSubtasksByParentId(@Param("parentId") Long parentId);

    void deleteByIdAndUserClerkId(Long id, String clerkId);
}