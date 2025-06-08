package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByNoteIdOrderByCreatedAtAsc(Long noteId);

    // Remplacer les méthodes Clerk par des méthodes basées sur User ID
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Garder pour compatibilité temporaire si nécessaire
    @Deprecated
    List<Comment> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);
}