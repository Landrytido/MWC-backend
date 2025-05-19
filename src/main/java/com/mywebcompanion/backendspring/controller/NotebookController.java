package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.NotebookDto;
import com.mywebcompanion.backendspring.service.NotebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notebooks")
@RequiredArgsConstructor
public class NotebookController {

    private final NotebookService notebookService;

    @GetMapping
    public ResponseEntity<List<NotebookDto>> getAllNotebooks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<NotebookDto> notebooks = notebookService.getAllNotebooksByClerkId(clerkId);
        return ResponseEntity.ok(notebooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotebookDto> getNotebookById(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        NotebookDto notebook = notebookService.getNotebookById(clerkId, id);
        return ResponseEntity.ok(notebook);
    }

    @PostMapping
    public ResponseEntity<NotebookDto> createNotebook(
            Authentication authentication,
            @RequestBody NotebookDto notebookDto) {
        String clerkId = authentication.getName();

        if (notebookDto.getTitle() == null || notebookDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        NotebookDto createdNotebook = notebookService.createNotebook(clerkId, notebookDto);
        return ResponseEntity.ok(createdNotebook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotebookDto> updateNotebook(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody NotebookDto notebookDto) {
        String clerkId = authentication.getName();

        if (notebookDto.getTitle() == null || notebookDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        NotebookDto updatedNotebook = notebookService.updateNotebook(clerkId, id, notebookDto);
        return ResponseEntity.ok(updatedNotebook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebook(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        notebookService.deleteNotebook(clerkId, id);
        return ResponseEntity.noContent().build();
    }
}