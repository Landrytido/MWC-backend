package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Note> findByIdAndUserId(Long id, Long userId);

    List<Note> findByUserIdAndNotebookId(Long userId, Long notebookId);

    List<Note> findByUserIdAndNotebookIsNull(Long userId);

    List<Note> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    @Query("SELECT n FROM Note n JOIN n.labels l WHERE l.id = :labelId AND n.user.id = :userId")
    List<Note> findByLabelsIdAndUserId(@Param("labelId") String labelId, @Param("userId") Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> findByUserIdAndContentContaining(@Param("userId") Long userId, @Param("keyword") String keyword);

    Long countByUserId(Long userId);

    Long countByUserIdAndNotebookId(Long userId, Long notebookId);

}