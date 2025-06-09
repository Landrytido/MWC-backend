package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.LinkGroupDto;
import com.mywebcompanion.backendspring.service.LinkGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/link-groups")
@RequiredArgsConstructor
public class LinkGroupController {

    private final LinkGroupService linkGroupService;

    @GetMapping
    public ResponseEntity<List<LinkGroupDto>> getAllLinkGroups(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<LinkGroupDto> linkGroups = linkGroupService.getAllLinkGroupsByUserEmail(email);
        return ResponseEntity.ok(linkGroups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LinkGroupDto> getLinkGroupById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String email = userDetails.getUsername();
        LinkGroupDto linkGroup = linkGroupService.getLinkGroupById(email, id);
        return ResponseEntity.ok(linkGroup);
    }

    @PostMapping
    public ResponseEntity<LinkGroupDto> createLinkGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LinkGroupDto linkGroupDto) {
        String email = userDetails.getUsername();

        if (linkGroupDto.getTitle() == null || linkGroupDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LinkGroupDto createdLinkGroup = linkGroupService.createLinkGroup(email, linkGroupDto);
        return ResponseEntity.ok(createdLinkGroup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LinkGroupDto> updateLinkGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestBody LinkGroupDto linkGroupDto) {
        String email = userDetails.getUsername();

        if (linkGroupDto.getTitle() == null || linkGroupDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LinkGroupDto updatedLinkGroup = linkGroupService.updateLinkGroup(email, id, linkGroupDto);
        return ResponseEntity.ok(updatedLinkGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLinkGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String email = userDetails.getUsername();
        linkGroupService.deleteLinkGroup(email, id);
        return ResponseEntity.noContent().build();
    }
}