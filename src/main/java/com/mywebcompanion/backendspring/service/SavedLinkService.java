package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.SavedLinkDto;
import com.mywebcompanion.backendspring.model.SavedLink;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.SavedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedLinkService {

    private final SavedLinkRepository savedLinkRepository;
    private final UserService userService;

    public List<SavedLinkDto> getAllSavedLinksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        List<SavedLink> savedLinks = savedLinkRepository.findByUserId(user.getId());

        return savedLinks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SavedLinkDto> getLinksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        return savedLinkRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SavedLinkDto createLink(String email, SavedLinkDto linkDto) {
        User user = userService.findByEmail(email);

        SavedLink link = new SavedLink();
        link.setUrl(linkDto.getUrl());
        link.setTitle(linkDto.getTitle());
        link.setDescription(linkDto.getDescription());
        link.setUser(user);

        SavedLink savedLink = savedLinkRepository.save(link);
        return convertToDto(savedLink);
    }

    public SavedLinkDto updateLink(String email, Long linkId, SavedLinkDto linkDto) {
        User user = userService.findByEmail(email);
        SavedLink link = savedLinkRepository.findByIdAndUserId(linkId, user.getId())
                .orElseThrow(() -> new RuntimeException("Lien non trouvé"));

        link.setUrl(linkDto.getUrl());
        link.setTitle(linkDto.getTitle());
        link.setDescription(linkDto.getDescription());

        SavedLink updatedLink = savedLinkRepository.save(link);
        return convertToDto(updatedLink);
    }

    public void deleteLink(String email, Long linkId) {
        User user = userService.findByEmail(email);
        SavedLink link = savedLinkRepository.findByIdAndUserId(linkId, user.getId())
                .orElseThrow(() -> new RuntimeException("Lien non trouvé"));

        savedLinkRepository.delete(link);
    }

    public SavedLinkDto getLinkById(String email, Long linkId) {
        User user = userService.findByEmail(email);
        SavedLink link = savedLinkRepository.findByIdAndUserId(linkId, user.getId())
                .orElseThrow(() -> new RuntimeException("Lien non trouvé"));

        return convertToDto(link);
    }

    public Long getLinkCount(String email) {
        User user = userService.findByEmail(email);
        return savedLinkRepository.countByUserId(user.getId());
    }

    private SavedLinkDto convertToDto(SavedLink link) {
        SavedLinkDto dto = new SavedLinkDto();
        dto.setId(link.getId());
        dto.setUrl(link.getUrl());
        dto.setTitle(link.getTitle());
        dto.setDescription(link.getDescription());
        dto.setCreatedAt(link.getCreatedAt());
        dto.setUpdatedAt(link.getUpdatedAt());
        return dto;
    }

    public List<SavedLinkDto> searchLinks(String email, String keyword) {
        User user = userService.findByEmail(email);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSavedLinksByUserEmail(email);
        }

        String trimmedKeyword = keyword.trim();

        List<SavedLink> linksByTitle = savedLinkRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(),
                trimmedKeyword);
        List<SavedLink> linksByUrl = savedLinkRepository.findByUserIdAndUrlContainingIgnoreCase(user.getId(),
                trimmedKeyword);

        Set<SavedLink> uniqueLinks = new HashSet<>();
        uniqueLinks.addAll(linksByTitle);
        uniqueLinks.addAll(linksByUrl);

        return uniqueLinks.stream()
                .map(this::convertToDto)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // Trier par date décroissante
                .collect(Collectors.toList());
    }
}