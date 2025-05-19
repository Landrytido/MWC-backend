package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.NoteDto;
import com.mywebcompanion.backendspring.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getAllNotes(Authentication authentication) {
        String clerkId = authentication.getName();
        List<NoteDto> notes = noteService.getNotesByClerkId(clerkId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(Authentication authentication, @RequestBody NoteDto noteDto) {
        String clerkId = authentication.getName();
        NoteDto createdNote = noteService.createNote(clerkId, noteDto);
        return ResponseEntity.ok(createdNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody NoteDto noteDto) {
        String clerkId = authentication.getName();
        NoteDto updatedNote = noteService.updateNote(clerkId, id, noteDto);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(Authentication authentication, @PathVariable Long id) {
        String clerkId = authentication.getName();
        noteService.deleteNote(clerkId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notebooks/{notebookId}")
    public ResponseEntity<NoteDto> createNoteInNotebook(
            Authentication authentication,
            @PathVariable Long notebookId,
            @RequestBody NoteDto noteDto) {
        String clerkId = authentication.getName();
        NoteDto createdNote = noteService.createNoteInNotebook(clerkId, noteDto, notebookId);
        return ResponseEntity.ok(createdNote);
    }

    @PutMapping("/{id}/notebook")
    public ResponseEntity<NoteDto> moveNoteToNotebook(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String clerkId = authentication.getName();
        Long notebookId = request.get("notebookId") != null
                ? Long.valueOf(request.get("notebookId").toString())
                : null;
        NoteDto updatedNote = noteService.moveNoteToNotebook(clerkId, id, notebookId);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/notebooks/{notebookId}/notes")
    public ResponseEntity<List<NoteDto>> getNotesByNotebook(
            Authentication authentication,
            @PathVariable Long notebookId) {
        String clerkId = authentication.getName();
        List<NoteDto> notes = noteService.getNotesByNotebookId(clerkId, notebookId);
        return ResponseEntity.ok(notes);
    }
}