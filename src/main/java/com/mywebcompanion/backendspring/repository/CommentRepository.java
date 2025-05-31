package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByNoteIdOrderByCreatedAtAsc(Long noteId);

    List<Comment> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND (c.user.clerkId = :clerkId OR c.note.user.clerkId = :clerkId)")
    Optional<Comment> findByIdAndUserCanAccess(@Param("commentId") Long commentId, @Param("clerkId") String clerkId);

    Long countByNoteId(Long noteId);

    @Query("SELECT c.note.id, COUNT(c) FROM Comment c WHERE c.note.id IN :noteIds GROUP BY c.note.id")
    List<Object[]> countCommentsByNoteIds(@Param("noteIds") List<Long> noteIds);

    default Map<Long, Long> findCommentCountsByNoteIds(List<Long> noteIds) {
        return countCommentsByNoteIds(noteIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
    }

    boolean existsByIdAndUserClerkId(Long commentId, String clerkId);
}