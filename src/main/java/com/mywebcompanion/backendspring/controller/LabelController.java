package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.LabelDto;
import com.mywebcompanion.backendspring.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelDto>> getAllLabels(Authentication authentication) {
        String clerkId = authentication.getName();
        List<LabelDto> labels = labelService.getAllLabelsByUserId(clerkId);
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDto> getLabelById(
            Authentication authentication,
            @PathVariable String id) {
        String clerkId = authentication.getName();
        LabelDto label = labelService.getLabelById(clerkId, id);
        return ResponseEntity.ok(label);
    }

    @PostMapping
    public ResponseEntity<LabelDto> createLabel(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String name = request.get("name");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LabelDto createdLabel = labelService.createLabel(clerkId, name.trim());
        return ResponseEntity.ok(createdLabel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDto> updateLabel(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String clerkId = authentication.getName();
        String name = request.get("name");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LabelDto updatedLabel = labelService.updateLabel(clerkId, id, name.trim());
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(
            Authentication authentication,
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean forceDelete) {
        String clerkId = authentication.getName();
        labelService.deleteLabel(clerkId, id, forceDelete);
        return ResponseEntity.noContent().build();
    }
}