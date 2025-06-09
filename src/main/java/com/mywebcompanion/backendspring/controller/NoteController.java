package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.NoteDto;
import com.mywebcompanion.backendspring.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getAllNotes(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<NoteDto> notes = noteService.getNotesByUserEmail(email);
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NoteDto noteDto) {
        String email = userDetails.getUsername();
        NoteDto createdNote = noteService.createNote(email, noteDto);
        return ResponseEntity.ok(createdNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody NoteDto noteDto) {
        String email = userDetails.getUsername();
        NoteDto updatedNote = noteService.updateNote(email, id, noteDto);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        noteService.deleteNote(email, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notebooks/{notebookId}")
    public ResponseEntity<NoteDto> createNoteInNotebook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notebookId,
            @RequestBody NoteDto noteDto) {
        String email = userDetails.getUsername();
        NoteDto createdNote = noteService.createNoteInNotebook(email, noteDto, notebookId);
        return ResponseEntity.ok(createdNote);
    }

    @PutMapping("/{id}/notebook")
    public ResponseEntity<NoteDto> moveNoteToNotebook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String email = userDetails.getUsername();
        Long notebookId = request.get("notebookId") != null
                ? Long.valueOf(request.get("notebookId").toString())
                : null;
        NoteDto updatedNote = noteService.moveNoteToNotebook(email, id, notebookId);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/notebooks/{notebookId}/notes")
    public ResponseEntity<List<NoteDto>> getNotesByNotebook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notebookId) {
        String email = userDetails.getUsername();
        List<NoteDto> notes = noteService.getNotesByNotebookId(email, notebookId);
        return ResponseEntity.ok(notes);
    }
}