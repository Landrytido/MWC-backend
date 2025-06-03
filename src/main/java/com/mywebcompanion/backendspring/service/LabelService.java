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

    public List<LabelDto> getAllLabelsByUserId(String clerkId) {
        User user = userService.findByClerkIdMinimal(clerkId);
        return labelRepository.findByUserIdOrderByNameAsc(user.getId().intValue())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LabelDto createLabel(String clerkId, String name) {
        User user = userService.findByClerkIdMinimal(clerkId);

        // Vérifier si le label existe déjà
        if (labelRepository.findByNameAndUserId(name, user.getId().intValue()).isPresent()) {
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

    public LabelDto updateLabel(String clerkId, String labelId, String name) {
        User user = userService.findByClerkIdMinimal(clerkId);

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label non trouvé"));

        // Vérifier que le label appartient à l'utilisateur
        if (!label.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n'avez pas le droit de modifier ce label");
        }

        // Vérifier si un autre label avec ce nom existe
        labelRepository.findByNameAndUserId(name, user.getId().intValue())
                .ifPresent(existingLabel -> {
                    if (!existingLabel.getId().equals(labelId)) {
                        throw new RuntimeException("Un label avec ce nom existe déjà");
                    }
                });

        label.setName(name);
        Label updatedLabel = labelRepository.save(label);
        return convertToDto(updatedLabel);
    }

    public void deleteLabel(String clerkId, String labelId, boolean forceDelete) {
        User user = userService.findByClerkIdMinimal(clerkId);

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label non trouvé"));

        // Vérifier que le label appartient à l'utilisateur
        if (!label.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n'avez pas le droit de supprimer ce label");
        }

        // Vérifier s'il y a des notes associées
        Long noteCount = labelRepository.countNotesByLabelId(labelId);
        if (noteCount > 0 && !forceDelete) {
            throw new RuntimeException(
                    "Ce label est utilisé par " + noteCount + " note(s). Utilisez forceDelete=true pour le supprimer.");
        }

        labelRepository.deleteById(labelId);
    }

    public LabelDto getLabelById(String clerkId, String labelId) {
        User user = userService.findByClerkIdMinimal(clerkId);

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label non trouvé"));

        // Vérifier que le label appartient à l'utilisateur
        if (!label.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n'avez pas le droit d'accéder à ce label");
        }

        return convertToDto(label);
    }

    private LabelDto convertToDto(Label label) {
        LabelDto dto = new LabelDto();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        dto.setUpdatedAt(label.getUpdatedAt());

        // Compter les notes associées
        dto.setNoteCount(labelRepository.countNotesByLabelId(label.getId()));

        return dto;
    }
}