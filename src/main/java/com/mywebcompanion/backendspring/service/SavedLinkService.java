package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.SavedLinkDto;
import com.mywebcompanion.backendspring.model.SavedLink;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.SavedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedLinkService {

    private final SavedLinkRepository savedLinkRepository;
    private final UserService userService;

    public List<SavedLinkDto> getLinksByClerkId(String clerkId) {
        return savedLinkRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SavedLinkDto createLink(String clerkId, SavedLinkDto linkDto) {
        User user = userService.findByClerkId(clerkId);

        SavedLink link = new SavedLink();
        link.setUrl(linkDto.getUrl());
        link.setTitle(linkDto.getTitle());
        link.setDescription(linkDto.getDescription());
        link.setUser(user);

        SavedLink savedLink = savedLinkRepository.save(link);
        return convertToDto(savedLink);
    }

    public SavedLinkDto updateLink(String clerkId, Long linkId, SavedLinkDto linkDto) {
        SavedLink link = savedLinkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Link not found"));

        // Vérifier que le lien appartient à l'utilisateur
        if (!link.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Not authorized to update this link");
        }

        link.setUrl(linkDto.getUrl());
        link.setTitle(linkDto.getTitle());
        link.setDescription(linkDto.getDescription());

        SavedLink updatedLink = savedLinkRepository.save(link);
        return convertToDto(updatedLink);
    }

    public void deleteLink(String clerkId, Long linkId) {
        SavedLink link = savedLinkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Link not found"));

        // Vérifier que le lien appartient à l'utilisateur
        if (!link.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Not authorized to delete this link");
        }

        savedLinkRepository.delete(link);
    }

    private SavedLinkDto convertToDto(SavedLink link) {
        SavedLinkDto dto = new SavedLinkDto();
        dto.setId(link.getId());
        dto.setUrl(link.getUrl());
        dto.setTitle(link.getTitle());
        dto.setDescription(link.getDescription());
        dto.setCreatedAt(link.getCreatedAt());
        return dto;
    }
}