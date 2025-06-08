package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.CommentDto;
import com.mywebcompanion.backendspring.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<CommentDto>> getCommentsByNoteId(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        String email = userDetails.getUsername();
        List<CommentDto> comments = commentService.getCommentsByNoteId(email, noteId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/note/{noteId}")
    public ResponseEntity<CommentDto> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @RequestBody CommentDto request) {
        String email = userDetails.getUsername();
        CommentDto comment = commentService.createComment(email, noteId, request.getContent());
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId,
            @RequestBody CommentDto request) {
        String email = userDetails.getUsername();
        CommentDto comment = commentService.updateComment(email, commentId, request.getContent());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId) {
        String email = userDetails.getUsername();
        commentService.deleteComment(email, commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<CommentDto>> getMyComments(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<CommentDto> comments = commentService.getMyComments(email);
        return ResponseEntity.ok(comments);
    }
}