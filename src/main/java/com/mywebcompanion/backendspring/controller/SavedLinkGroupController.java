package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.SavedLinkGroupDto;
import com.mywebcompanion.backendspring.service.SavedLinkGroupService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/link-groups")
@RequiredArgsConstructor
public class SavedLinkGroupController {

    private final SavedLinkGroupService savedLinkGroupService;

    @GetMapping("/{groupId}/links")
    public ResponseEntity<List<SavedLinkGroupDto>> getLinksInGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId) {
        String email = userDetails.getUsername();
        List<SavedLinkGroupDto> links = savedLinkGroupService.getLinksByGroupId(email, groupId);
        return ResponseEntity.ok(links);
    }

    @PostMapping("/{groupId}/links/{linkId}")
    public ResponseEntity<SavedLinkGroupDto> addLinkToGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId,
            @PathVariable Long linkId,
            @RequestBody(required = false) Map<String, String> request) {
        String email = userDetails.getUsername();
        String linkName = request != null ? request.get("linkName") : null;

        SavedLinkGroupDto result = savedLinkGroupService.addLinkToGroup(email, groupId, linkId, linkName);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{groupId}/links/{linkId}")
    public ResponseEntity<SavedLinkGroupDto> updateLinkInGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId,
            @PathVariable Long linkId,
            @RequestBody Map<String, String> request) {
        String email = userDetails.getUsername();
        String linkName = request.get("linkName");

        if (linkName == null || linkName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        SavedLinkGroupDto result = savedLinkGroupService.updateLinkInGroup(email, groupId, linkId, linkName.trim());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{groupId}/links/{linkId}/click")
    public ResponseEntity<SavedLinkGroupDto> incrementClickCounter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId,
            @PathVariable Long linkId) {
        String email = userDetails.getUsername();
        SavedLinkGroupDto result = savedLinkGroupService.incrementClickCounter(email, groupId, linkId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{groupId}/links/{linkId}")
    public ResponseEntity<Void> removeLinkFromGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId,
            @PathVariable Long linkId) {
        String email = userDetails.getUsername();
        savedLinkGroupService.removeLinkFromGroup(email, groupId, linkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/links/top-clicked")
    public ResponseEntity<List<SavedLinkGroupDto>> getTopClickedLinks(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId) {
        String email = userDetails.getUsername();
        List<SavedLinkGroupDto> topLinks = savedLinkGroupService.getTopClickedLinks(email, groupId);
        return ResponseEntity.ok(topLinks);
    }

    @GetMapping("/links/global-top-clicked")
    public ResponseEntity<List<SavedLinkGroupDto>> getGlobalTopClickedLinks(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<SavedLinkGroupDto> topLinks = savedLinkGroupService.getGlobalTopClickedLinks(email);
        return ResponseEntity.ok(topLinks);
    }

}