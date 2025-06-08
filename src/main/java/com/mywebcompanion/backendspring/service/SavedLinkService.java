package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.SavedLinkDto;
import com.mywebcompanion.backendspring.model.SavedLink;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.SavedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedLinkService {

    private final SavedLinkRepository savedLinkRepository;
    private final UserService userService;

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
}