package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Trouver tous les commentaires d'une note, triés par date de création
    List<Comment> findByNoteIdOrderByCreatedAtAsc(Long noteId);

    // Trouver tous les commentaires d'un utilisateur
    List<Comment> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    // Trouver un commentaire par ID avec vérification de propriétaire
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND (c.user.clerkId = :clerkId OR c.note.user.clerkId = :clerkId)")
    Optional<Comment> findByIdAndUserCanAccess(@Param("commentId") Long commentId, @Param("clerkId") String clerkId);

    // Compter les commentaires d'une note
    Long countByNoteId(Long noteId);

    // Vérifier si un utilisateur peut modifier un commentaire (il en est l'auteur)
    boolean existsByIdAndUserClerkId(Long commentId, String clerkId);
}