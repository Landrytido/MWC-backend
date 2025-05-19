package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.CommentDto;
import com.mywebcompanion.backendspring.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Obtenir tous les commentaires d'une note
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<List<CommentDto>> getCommentsByNote(
            Authentication authentication,
            @PathVariable Long noteId) {
        String clerkId = authentication.getName();
        List<CommentDto> comments = commentService.getCommentsByNoteId(clerkId, noteId);
        return ResponseEntity.ok(comments);
    }

    // Créer un commentaire sur une note
    @PostMapping("/notes/{noteId}")
    public ResponseEntity<CommentDto> createComment(
            Authentication authentication,
            @PathVariable Long noteId,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String content = request.get("content");

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CommentDto createdComment = commentService.createComment(clerkId, noteId, content.trim());
        return ResponseEntity.ok(createdComment);
    }

    // Modifier un commentaire
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String content = request.get("content");

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CommentDto updatedComment = commentService.updateComment(clerkId, id, content.trim());
        return ResponseEntity.ok(updatedComment);
    }

    // Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        commentService.deleteComment(clerkId, id);
        return ResponseEntity.noContent().build();
    }

    // Obtenir tous mes commentaires
    @GetMapping("/my-comments")
    public ResponseEntity<List<CommentDto>> getMyComments(Authentication authentication) {
        String clerkId = authentication.getName();
        List<CommentDto> comments = commentService.getMyComments(clerkId);
        return ResponseEntity.ok(comments);
    }
}