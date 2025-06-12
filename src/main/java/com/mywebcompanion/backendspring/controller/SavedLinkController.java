package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.SavedLinkDto;
import com.mywebcompanion.backendspring.service.SavedLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class SavedLinkController {

    private final SavedLinkService savedLinkService;

    @GetMapping
    public ResponseEntity<List<SavedLinkDto>> getAllLinks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<SavedLinkDto> links = savedLinkService.getLinksByUserEmail(email);
        return ResponseEntity.ok(links);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedLinkDto> getLinkById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        SavedLinkDto link = savedLinkService.getLinkById(email, id);
        return ResponseEntity.ok(link);
    }

    @PostMapping
    public ResponseEntity<SavedLinkDto> createLink(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SavedLinkDto linkDto) {
        String email = userDetails.getUsername();
        SavedLinkDto createdLink = savedLinkService.createLink(email, linkDto);
        return ResponseEntity.ok(createdLink);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedLinkDto> updateLink(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody SavedLinkDto linkDto) {
        String email = userDetails.getUsername();
        SavedLinkDto updatedLink = savedLinkService.updateLink(email, id, linkDto);
        return ResponseEntity.ok(updatedLink);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        savedLinkService.deleteLink(email, id);
        return ResponseEntity.noContent().build();
    }
}