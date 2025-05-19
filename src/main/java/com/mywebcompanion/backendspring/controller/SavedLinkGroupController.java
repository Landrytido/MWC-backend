package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.SavedLinkGroupDto;
import com.mywebcompanion.backendspring.service.SavedLinkGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/link-groups")
@RequiredArgsConstructor
public class SavedLinkGroupController {

    private final SavedLinkGroupService savedLinkGroupService;

    // Obtenir tous les liens d'un groupe
    @GetMapping("/{groupId}/links")
    public ResponseEntity<List<SavedLinkGroupDto>> getLinksInGroup(
            Authentication authentication,
            @PathVariable String groupId) {
        String clerkId = authentication.getName();
        List<SavedLinkGroupDto> links = savedLinkGroupService.getLinksByGroupId(groupId);
        return ResponseEntity.ok(links);
    }

    // Ajouter un lien à un groupe
    @PostMapping("/{groupId}/links/{linkId}")
    public ResponseEntity<SavedLinkGroupDto> addLinkToGroup(
            Authentication authentication,
            @PathVariable String groupId,
            @PathVariable Long linkId,
            @RequestBody(required = false) Map<String, String> request) {
        String clerkId = authentication.getName();
        String linkName = request != null ? request.get("linkName") : null;

        SavedLinkGroupDto result = savedLinkGroupService.addLinkToGroup(clerkId, groupId, linkId, linkName);
        return ResponseEntity.ok(result);
    }

    // Mettre à jour le nom d'un lien dans un groupe
    @PutMapping("/{groupId}/links/{linkId}")
    public ResponseEntity<SavedLinkGroupDto> updateLinkInGroup(
            Authentication authentication,
            @PathVariable String groupId,
            @PathVariable Long linkId,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String linkName = request.get("linkName");

        if (linkName == null || linkName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        SavedLinkGroupDto result = savedLinkGroupService.updateLinkInGroup(clerkId, groupId, linkId, linkName.trim());
        return ResponseEntity.ok(result);
    }

    // Incrémenter le compteur de clics d'un lien
    @PostMapping("/{groupId}/links/{linkId}/click")
    public ResponseEntity<SavedLinkGroupDto> incrementClickCounter(
            Authentication authentication,
            @PathVariable String groupId,
            @PathVariable Long linkId) {
        String clerkId = authentication.getName();
        SavedLinkGroupDto result = savedLinkGroupService.incrementClickCounter(clerkId, groupId, linkId);
        return ResponseEntity.ok(result);
    }

    // Supprimer un lien d'un groupe
    @DeleteMapping("/{groupId}/links/{linkId}")
    public ResponseEntity<Void> removeLinkFromGroup(
            Authentication authentication,
            @PathVariable String groupId,
            @PathVariable Long linkId) {
        String clerkId = authentication.getName();
        savedLinkGroupService.removeLinkFromGroup(clerkId, groupId, linkId);
        return ResponseEntity.noContent().build();
    }

    // Obtenir les liens les plus cliqués d'un groupe
    @GetMapping("/{groupId}/links/top-clicked")
    public ResponseEntity<List<SavedLinkGroupDto>> getTopClickedLinks(
            Authentication authentication,
            @PathVariable String groupId) {
        String clerkId = authentication.getName();
        List<SavedLinkGroupDto> topLinks = savedLinkGroupService.getTopClickedLinks(clerkId, groupId);
        return ResponseEntity.ok(topLinks);
    }

    // Obtenir les liens les plus cliqués globalement
    @GetMapping("/links/global-top-clicked")
    public ResponseEntity<List<SavedLinkGroupDto>> getGlobalTopClickedLinks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<SavedLinkGroupDto> topLinks = savedLinkGroupService.getGlobalTopClickedLinks(clerkId);
        return ResponseEntity.ok(topLinks);
    }
}