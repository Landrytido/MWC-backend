package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.LabelDto;
import com.mywebcompanion.backendspring.model.Label;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.LabelRepository;
import com.mywebcompanion.backendspring.exception.ResourceNotFoundException;
import com.mywebcompanion.backendspring.exception.DuplicateResourceException;
import com.mywebcompanion.backendspring.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;
    private final UserService userService;

    public List<LabelDto> getAllLabelsByUserEmail(String email) {
        User user = userService.findByEmail(email);
        return labelRepository.findByUserIdOrderByNameAsc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LabelDto createLabel(String email, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Le nom du label ne peut pas être vide");
        }

        if (name.trim().length() > 50) {
            throw new ValidationException("Le nom du label ne peut pas dépasser 50 caractères");
        }

        String cleanName = name.trim();
        User user = userService.findByEmail(email);

        if (labelRepository.findByNameAndUserId(cleanName, user.getId()).isPresent()) {
            throw new DuplicateResourceException("Label", "name", cleanName);
        }

        Label label = new Label();
        label.setName(cleanName);
        label.setUser(user);

        Label savedLabel = labelRepository.save(label);
        return convertToDto(savedLabel);
    }

    public LabelDto updateLabel(String email, String labelId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Le nom du label ne peut pas être vide");
        }

        if (name.trim().length() > 50) {
            throw new ValidationException("Le nom du label ne peut pas dépasser 50 caractères");
        }

        String cleanName = name.trim();
        User user = userService.findByEmail(email);

        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));

        labelRepository.findByNameAndUserId(cleanName, user.getId())
                .ifPresent(existingLabel -> {
                    if (!existingLabel.getId().equals(labelId)) {
                        throw new DuplicateResourceException("Label", "name", cleanName);
                    }
                });

        label.setName(cleanName);
        Label updatedLabel = labelRepository.save(label);
        return convertToDto(updatedLabel);
    }

    public void deleteLabel(String email, String labelId, boolean forceDelete) {
        User user = userService.findByEmail(email);

        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));

        Long noteCount = labelRepository.countNotesByLabelIdAndUserId(labelId, user.getId());
        if (noteCount > 0 && !forceDelete) {
            throw new ValidationException(
                    "Ce label est utilisé par " + noteCount + " note(s). Utilisez forceDelete=true pour le supprimer.");
        }

        labelRepository.delete(label);
    }

    public LabelDto getLabelById(String email, String labelId) {
        User user = userService.findByEmail(email);
        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));
        return convertToDto(label);
    }

    public List<LabelDto> searchLabels(String email, String keyword) {
        User user = userService.findByEmail(email);
        return labelRepository.findByUserIdAndNameContainingIgnoreCase(user.getId(), keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private LabelDto convertToDto(Label label) {
        LabelDto dto = new LabelDto();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        dto.setUpdatedAt(label.getUpdatedAt());
        dto.setNoteCount(labelRepository.countNotesByLabelIdAndUserId(label.getId(), label.getUser().getId()));
        return dto;
    }
}