package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.LinkGroupDto;
import com.mywebcompanion.backendspring.service.LinkGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/link-groups")
@RequiredArgsConstructor
public class LinkGroupController {

    private final LinkGroupService linkGroupService;

    @GetMapping
    public ResponseEntity<List<LinkGroupDto>> getAllLinkGroups(Authentication authentication) {
        String clerkId = authentication.getName();
        List<LinkGroupDto> linkGroups = linkGroupService.getAllLinkGroupsByClerkId(clerkId);
        return ResponseEntity.ok(linkGroups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LinkGroupDto> getLinkGroupById(
            Authentication authentication,
            @PathVariable String id) {
        String clerkId = authentication.getName();
        LinkGroupDto linkGroup = linkGroupService.getLinkGroupById(clerkId, id);
        return ResponseEntity.ok(linkGroup);
    }

    @PostMapping
    public ResponseEntity<LinkGroupDto> createLinkGroup(
            Authentication authentication,
            @RequestBody LinkGroupDto linkGroupDto) {
        String clerkId = authentication.getName();

        if (linkGroupDto.getTitle() == null || linkGroupDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LinkGroupDto createdLinkGroup = linkGroupService.createLinkGroup(clerkId, linkGroupDto);
        return ResponseEntity.ok(createdLinkGroup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LinkGroupDto> updateLinkGroup(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody LinkGroupDto linkGroupDto) {
        String clerkId = authentication.getName();

        if (linkGroupDto.getTitle() == null || linkGroupDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LinkGroupDto updatedLinkGroup = linkGroupService.updateLinkGroup(clerkId, id, linkGroupDto);
        return ResponseEntity.ok(updatedLinkGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLinkGroup(
            Authentication authentication,
            @PathVariable String id) {
        String clerkId = authentication.getName();
        linkGroupService.deleteLinkGroup(clerkId, id);
        return ResponseEntity.noContent().build();
    }
}