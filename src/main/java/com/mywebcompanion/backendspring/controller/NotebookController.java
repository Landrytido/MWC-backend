package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.NotebookDto;
import com.mywebcompanion.backendspring.service.NotebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notebooks")
@RequiredArgsConstructor
public class NotebookController {

    private final NotebookService notebookService;

    @GetMapping
    public ResponseEntity<List<NotebookDto>> getAllNotebooks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<NotebookDto> notebooks = notebookService.getAllNotebooksByUserEmail(email);
        return ResponseEntity.ok(notebooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotebookDto> getNotebookById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        NotebookDto notebook = notebookService.getNotebookById(email, id);
        return ResponseEntity.ok(notebook);
    }

    @PostMapping
    public ResponseEntity<NotebookDto> createNotebook(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NotebookDto notebookDto) {
        String email = userDetails.getUsername();

        if (notebookDto.getTitle() == null || notebookDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        NotebookDto createdNotebook = notebookService.createNotebook(email, notebookDto);
        return ResponseEntity.ok(createdNotebook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotebookDto> updateNotebook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody NotebookDto notebookDto) {
        String email = userDetails.getUsername();

        if (notebookDto.getTitle() == null || notebookDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        NotebookDto updatedNotebook = notebookService.updateNotebook(email, id, notebookDto);
        return ResponseEntity.ok(updatedNotebook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean forceDelete) {
        String email = userDetails.getUsername();
        notebookService.deleteNotebook(email, id, forceDelete);
        return ResponseEntity.noContent().build();
    }
}