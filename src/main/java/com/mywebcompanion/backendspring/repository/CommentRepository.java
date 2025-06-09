package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

        List<Comment> findByNoteIdOrderByCreatedAtAsc(Long noteId);

        List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

        Long countByNoteId(Long noteId);

        @Query("SELECT c.note.id, COUNT(c) FROM Comment c WHERE c.note.id IN :noteIds GROUP BY c.note.id")
        List<Object[]> findCommentCountsByNoteIdsRaw(@Param("noteIds") List<Long> noteIds);

        default Map<Long, Long> findCommentCountsByNoteIds(List<Long> noteIds) {
                return findCommentCountsByNoteIdsRaw(noteIds).stream()
                                .collect(java.util.stream.Collectors.toMap(
                                                result -> (Long) result[0],
                                                result -> (Long) result[1]));
        }
}