package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.NoteTaskDto;
import com.mywebcompanion.backendspring.service.NoteTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/note-tasks")
@RequiredArgsConstructor
public class NoteTaskController {

    private final NoteTaskService noteTaskService;

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<NoteTaskDto>> getNoteTasksByNoteId(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        String email = userDetails.getUsername();
        List<NoteTaskDto> tasks = noteTaskService.getNoteTasksByNoteId(email, noteId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping
    public ResponseEntity<List<NoteTaskDto>> getAllNoteTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<NoteTaskDto> tasks = noteTaskService.getAllNoteTasksByUserEmail(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<NoteTaskDto>> getPendingNoteTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<NoteTaskDto> tasks = noteTaskService.getPendingNoteTasksByUserEmail(email);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/note/{noteId}")
    public ResponseEntity<NoteTaskDto> createNoteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @RequestParam String title,
            @RequestParam(required = false) Long parentId) {
        String email = userDetails.getUsername();
        NoteTaskDto task = noteTaskService.createNoteTask(email, noteId, title, parentId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<NoteTaskDto> updateNoteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId,
            @RequestBody NoteTaskDto request) {
        String email = userDetails.getUsername();
        NoteTaskDto task = noteTaskService.updateNoteTask(email, taskId, request);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/toggle")
    public ResponseEntity<NoteTaskDto> toggleNoteTaskCompletion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId) {
        String email = userDetails.getUsername();
        NoteTaskDto task = noteTaskService.toggleNoteTaskCompletion(email, taskId);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteNoteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId) {
        String email = userDetails.getUsername();
        noteTaskService.deleteNoteTask(email, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<NoteTaskDto> getNoteTaskById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId) {
        String email = userDetails.getUsername();
        NoteTaskDto task = noteTaskService.getNoteTaskById(email, taskId);
        return ResponseEntity.ok(task);
    }
}