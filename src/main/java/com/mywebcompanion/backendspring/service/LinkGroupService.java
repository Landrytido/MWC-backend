package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.LinkGroupDto;
import com.mywebcompanion.backendspring.dto.SavedLinkGroupDto;
import com.mywebcompanion.backendspring.model.LinkGroup;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.LinkGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LinkGroupService {

    private final LinkGroupRepository linkGroupRepository;
    private final UserService userService;
    private final SavedLinkGroupService savedLinkGroupService;

    public List<LinkGroupDto> getAllLinkGroupsByUserEmail(String email) {
        User user = userService.findByEmail(email);
        return linkGroupRepository.findByUserIdOrderByTitleAsc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LinkGroupDto createLinkGroup(String email, LinkGroupDto linkGroupDto) {
        User user = userService.findByEmail(email);

        LinkGroup linkGroup = new LinkGroup();
        linkGroup.setTitle(linkGroupDto.getTitle());
        linkGroup.setDescription(linkGroupDto.getDescription());
        linkGroup.setUser(user);

        LinkGroup savedLinkGroup = linkGroupRepository.save(linkGroup);
        return convertToDto(savedLinkGroup);
    }

    public LinkGroupDto updateLinkGroup(String email, Long linkGroupId, LinkGroupDto linkGroupDto) {
        User user = userService.findByEmail(email);
        LinkGroup linkGroup = linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé ou accès non autorisé"));

        linkGroup.setTitle(linkGroupDto.getTitle());
        linkGroup.setDescription(linkGroupDto.getDescription());

        LinkGroup updatedLinkGroup = linkGroupRepository.save(linkGroup);
        return convertToDto(updatedLinkGroup);
    }

    public void deleteLinkGroup(String email, Long linkGroupId) {
        User user = userService.findByEmail(email);
        LinkGroup linkGroup = linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé ou accès non autorisé"));

        // Les relations SavedLinkGroup sont supprimées automatiquement (cascade)
        linkGroupRepository.delete(linkGroup);
    }

    public LinkGroupDto getLinkGroupById(String email, Long linkGroupId) {
        User user = userService.findByEmail(email);
        LinkGroup linkGroup = linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé ou accès non autorisé"));

        return convertToDtoWithLinks(linkGroup, email);
    }

    private LinkGroupDto convertToDto(LinkGroup linkGroup) {
        LinkGroupDto dto = new LinkGroupDto();
        dto.setId(linkGroup.getId());
        dto.setTitle(linkGroup.getTitle());
        dto.setDescription(linkGroup.getDescription());
        dto.setCreatedAt(linkGroup.getCreatedAt());
        dto.setUpdatedAt(linkGroup.getUpdatedAt());

        // CORRECTION: Le paramètre doit être de type Long, pas String
        dto.setLinkCount(linkGroupRepository.countLinksByGroupId(linkGroup.getId()).intValue());

        return dto;
    }

    private LinkGroupDto convertToDtoWithLinks(LinkGroup linkGroup, String email) {
        LinkGroupDto dto = convertToDto(linkGroup);

        // CORRECTION: Appeler la méthode avec email et linkGroupId comme String
        List<SavedLinkGroupDto> links = savedLinkGroupService.getLinksByGroupId(email, linkGroupId.toString());
        dto.setSavedLinks(links);

        return dto;
    }
}