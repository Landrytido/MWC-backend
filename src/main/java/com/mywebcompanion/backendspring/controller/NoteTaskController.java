package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.NoteTaskDto;
import com.mywebcompanion.backendspring.service.NoteTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/note-tasks")
@RequiredArgsConstructor
public class NoteTaskController {

    private final NoteTaskService noteTaskService;

    // Obtenir toutes les tâches d'une note
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<List<NoteTaskDto>> getNoteTasksByNote(
            Authentication authentication,
            @PathVariable Long noteId) {
        String clerkId = authentication.getName();
        List<NoteTaskDto> noteTasks = noteTaskService.getNoteTasksByNoteId(clerkId, noteId);
        return ResponseEntity.ok(noteTasks);
    }

    // Obtenir toutes mes tâches de notes
    @GetMapping("/my-tasks")
    public ResponseEntity<List<NoteTaskDto>> getAllMyNoteTasks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<NoteTaskDto> noteTasks = noteTaskService.getAllNoteTasksByUserId(clerkId);
        return ResponseEntity.ok(noteTasks);
    }

    // Obtenir mes tâches de notes en attente
    @GetMapping("/my-tasks/pending")
    public ResponseEntity<List<NoteTaskDto>> getPendingNoteTasks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<NoteTaskDto> noteTasks = noteTaskService.getPendingNoteTasksByUserId(clerkId);
        return ResponseEntity.ok(noteTasks);
    }

    // Obtenir une tâche de note par ID
    @GetMapping("/{id}")
    public ResponseEntity<NoteTaskDto> getNoteTaskById(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        NoteTaskDto noteTask = noteTaskService.getNoteTaskById(clerkId, id);
        return ResponseEntity.ok(noteTask);
    }

    // Créer une nouvelle tâche de note
    @PostMapping("/notes/{noteId}")
    public ResponseEntity<NoteTaskDto> createNoteTask(
            Authentication authentication,
            @PathVariable Long noteId,
            @RequestBody Map<String, Object> request) {
        String clerkId = authentication.getName();
        String title = (String) request.get("title");
        Long parentId = request.get("parentId") != null ? Long.valueOf(request.get("parentId").toString()) : null;

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        NoteTaskDto createdTask = noteTaskService.createNoteTask(clerkId, noteId, title.trim(), parentId);
        return ResponseEntity.ok(createdTask);
    }

    // Créer une sous-tâche
    @PostMapping("/{parentId}/subtasks")
    public ResponseEntity<NoteTaskDto> createSubtask(
            Authentication authentication,
            @PathVariable Long parentId,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String title = request.get("title");

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Récupérer la tâche parent pour obtenir l'ID de la note
        NoteTaskDto parentTask = noteTaskService.getNoteTaskById(clerkId, parentId);

        NoteTaskDto createdSubtask = noteTaskService.createNoteTask(clerkId, parentTask.getNoteId(), title.trim(),
                parentId);
        return ResponseEntity.ok(createdSubtask);
    }

    // Mettre à jour une tâche de note
    @PutMapping("/{id}")
    public ResponseEntity<NoteTaskDto> updateNoteTask(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody NoteTaskDto dto) {
        String clerkId = authentication.getName();

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        NoteTaskDto updatedTask = noteTaskService.updateNoteTask(clerkId, id, dto);
        return ResponseEntity.ok(updatedTask);
    }

    // Basculer l'état de completion d'une tâche de note
    @PutMapping("/{id}/toggle")
    public ResponseEntity<NoteTaskDto> toggleNoteTaskCompletion(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        NoteTaskDto updatedTask = noteTaskService.toggleNoteTaskCompletion(clerkId, id);
        return ResponseEntity.ok(updatedTask);
    }

    // Supprimer une tâche de note
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteTask(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        noteTaskService.deleteNoteTask(clerkId, id);
        return ResponseEntity.noContent().build();
    }
}