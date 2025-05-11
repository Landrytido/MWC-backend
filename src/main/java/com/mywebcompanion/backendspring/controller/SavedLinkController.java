package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.SavedLinkDto;
import com.mywebcompanion.backendspring.service.SavedLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class SavedLinkController {

    private final SavedLinkService savedLinkService;

    @GetMapping
    public ResponseEntity<List<SavedLinkDto>> getAllLinks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<SavedLinkDto> links = savedLinkService.getLinksByClerkId(clerkId);
        return ResponseEntity.ok(links);
    }

    @PostMapping
    public ResponseEntity<SavedLinkDto> createLink(Authentication authentication, @RequestBody SavedLinkDto linkDto) {
        String clerkId = authentication.getName();
        SavedLinkDto createdLink = savedLinkService.createLink(clerkId, linkDto);
        return ResponseEntity.ok(createdLink);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedLinkDto> updateLink(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody SavedLinkDto linkDto) {
        String clerkId = authentication.getName();
        SavedLinkDto updatedLink = savedLinkService.updateLink(clerkId, id, linkDto);
        return ResponseEntity.ok(updatedLink);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(Authentication authentication, @PathVariable Long id) {
        String clerkId = authentication.getName();
        savedLinkService.deleteLink(clerkId, id);
        return ResponseEntity.noContent().build();
    }
}