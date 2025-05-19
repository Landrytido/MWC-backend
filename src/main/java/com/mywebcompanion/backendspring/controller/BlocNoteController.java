package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.BlocNoteDto;
import com.mywebcompanion.backendspring.service.BlocNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bloc-note")
@RequiredArgsConstructor
public class BlocNoteController {

    private final BlocNoteService blocNoteService;

    @GetMapping
    public ResponseEntity<BlocNoteDto> getBlocNote(Authentication authentication) {
        String clerkId = authentication.getName();
        BlocNoteDto blocNote = blocNoteService.getBlocNoteByClerkId(clerkId);
        return ResponseEntity.ok(blocNote);
    }

    @PutMapping
    public ResponseEntity<BlocNoteDto> updateBlocNote(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String content = request.get("content");

        // Autoriser le contenu vide ou null
        if (content == null) {
            content = "";
        }

        BlocNoteDto updatedBlocNote = blocNoteService.upsertBlocNote(clerkId, content);
        return ResponseEntity.ok(updatedBlocNote);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBlocNote(Authentication authentication) {
        String clerkId = authentication.getName();
        blocNoteService.deleteBlocNote(clerkId);
        return ResponseEntity.noContent().build();
    }
}