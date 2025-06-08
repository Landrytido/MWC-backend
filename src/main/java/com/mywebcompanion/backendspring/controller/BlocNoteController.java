package com.mywebcompanion.backendspring.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.mywebcompanion.backendspring.dto.BlocNoteDto;
import com.mywebcompanion.backendspring.service.BlocNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bloc-notes")
@RequiredArgsConstructor
public class BlocNoteController {

    private final BlocNoteService blocNoteService;

    @GetMapping
    public ResponseEntity<BlocNoteDto> getBlocNote(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        BlocNoteDto blocNote = blocNoteService.getBlocNoteByUserEmail(email);
        return ResponseEntity.ok(blocNote);
    }

    @PostMapping
    public ResponseEntity<BlocNoteDto> upsertBlocNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BlocNoteDto request) {
        String email = userDetails.getUsername();
        BlocNoteDto blocNote = blocNoteService.upsertBlocNote(email, request.getContent());
        return ResponseEntity.ok(blocNote);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBlocNote(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        blocNoteService.deleteBlocNote(email);
        return ResponseEntity.ok().build();
    }
}