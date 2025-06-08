package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.LabelDto;
import com.mywebcompanion.backendspring.model.Label;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        User user = userService.findByEmail(email);

        // Vérifier si le label existe déjà
        if (labelRepository.findByNameAndUserId(name, user.getId()).isPresent()) {
            throw new RuntimeException("Un label avec ce nom existe déjà");
        }

        Label label = new Label();
        label.setName(name);
        label.setUser(user);

        try {
            Label savedLabel = labelRepository.save(label);
            return convertToDto(savedLabel);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Un label avec ce nom existe déjà");
        }
    }

    public LabelDto updateLabel(String email, String labelId, String name) {
        User user = userService.findByEmail(email);

        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new RuntimeException("Label non trouvé ou accès non autorisé"));

        // Vérifier si un autre label avec ce nom existe
        labelRepository.findByNameAndUserId(name, user.getId())
                .ifPresent(existingLabel -> {
                    if (!existingLabel.getId().equals(labelId)) {
                        throw new RuntimeException("Un label avec ce nom existe déjà");
                    }
                });

        label.setName(name);
        Label updatedLabel = labelRepository.save(label);
        return convertToDto(updatedLabel);
    }

    public void deleteLabel(String email, String labelId, boolean forceDelete) {
        User user = userService.findByEmail(email);

        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new RuntimeException("Label non trouvé ou accès non autorisé"));

        // Vérifier s'il y a des notes associées
        Long noteCount = labelRepository.countNotesByLabelIdAndUserId(labelId, user.getId());
        if (noteCount > 0 && !forceDelete) {
            throw new RuntimeException(
                    "Ce label est utilisé par " + noteCount + " note(s). Utilisez forceDelete=true pour le supprimer.");
        }

        labelRepository.delete(label);
    }

    public LabelDto getLabelById(String email, String labelId) {
        User user = userService.findByEmail(email);

        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new RuntimeException("Label non trouvé ou accès non autorisé"));

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

        // CORRECTION: labelId est maintenant un String (UUID)
        dto.setNoteCount(labelRepository.countNotesByLabelIdAndUserId(label.getId(), label.getUser().getId()));

        return dto;
    }
}