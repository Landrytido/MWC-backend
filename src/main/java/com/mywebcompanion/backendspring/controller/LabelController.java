package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.LabelDto;
import com.mywebcompanion.backendspring.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelDto>> getAllLabels(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<LabelDto> labels = labelService.getAllLabelsByUserEmail(email);
        return ResponseEntity.ok(labels);
    }

    @PostMapping
    public ResponseEntity<LabelDto> createLabel(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String name) {
        String email = userDetails.getUsername();
        LabelDto label = labelService.createLabel(email, name);
        return ResponseEntity.ok(label);
    }

    @PutMapping("/{labelId}")
    public ResponseEntity<LabelDto> updateLabel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String labelId,
            @RequestParam String name) {
        String email = userDetails.getUsername();
        LabelDto label = labelService.updateLabel(email, labelId, name);
        return ResponseEntity.ok(label);
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> deleteLabel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String labelId,
            @RequestParam(defaultValue = "false") boolean forceDelete) {
        String email = userDetails.getUsername();
        labelService.deleteLabel(email, labelId, forceDelete);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{labelId}")
    public ResponseEntity<LabelDto> getLabelById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String labelId) {
        String email = userDetails.getUsername();
        LabelDto label = labelService.getLabelById(email, labelId);
        return ResponseEntity.ok(label);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LabelDto>> searchLabels(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        String email = userDetails.getUsername();
        List<LabelDto> labels = labelService.searchLabels(email, keyword);
        return ResponseEntity.ok(labels);
    }

}