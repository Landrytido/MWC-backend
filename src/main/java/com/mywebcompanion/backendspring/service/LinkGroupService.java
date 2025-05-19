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

    public List<LinkGroupDto> getAllLinkGroupsByClerkId(String clerkId) {
        return linkGroupRepository.findByUserClerkIdOrderByTitleAsc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LinkGroupDto createLinkGroup(String clerkId, LinkGroupDto linkGroupDto) {
        User user = userService.findByClerkId(clerkId);

        LinkGroup linkGroup = new LinkGroup();
        linkGroup.setTitle(linkGroupDto.getTitle());
        linkGroup.setDescription(linkGroupDto.getDescription());
        linkGroup.setUser(user);

        LinkGroup savedLinkGroup = linkGroupRepository.save(linkGroup);
        return convertToDto(savedLinkGroup);
    }

    public LinkGroupDto updateLinkGroup(String clerkId, String linkGroupId, LinkGroupDto linkGroupDto) {
        LinkGroup linkGroup = linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

        linkGroup.setTitle(linkGroupDto.getTitle());
        linkGroup.setDescription(linkGroupDto.getDescription());

        LinkGroup updatedLinkGroup = linkGroupRepository.save(linkGroup);
        return convertToDto(updatedLinkGroup);
    }

    public void deleteLinkGroup(String clerkId, String linkGroupId) {
        LinkGroup linkGroup = linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

        // Les relations SavedLinkGroup sont supprimées automatiquement (cascade)
        linkGroupRepository.delete(linkGroup);
    }

    public LinkGroupDto getLinkGroupById(String clerkId, String linkGroupId) {
        LinkGroup linkGroup = linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

        return convertToDtoWithLinks(linkGroup);
    }

    private LinkGroupDto convertToDto(LinkGroup linkGroup) {
        LinkGroupDto dto = new LinkGroupDto();
        dto.setId(linkGroup.getId());
        dto.setTitle(linkGroup.getTitle());
        dto.setDescription(linkGroup.getDescription());
        dto.setCreatedAt(linkGroup.getCreatedAt());
        dto.setUpdatedAt(linkGroup.getUpdatedAt());

        // Compter les liens dans ce groupe
        dto.setLinkCount(linkGroupRepository.countLinksByGroupId(linkGroup.getId()).intValue());

        return dto;
    }

    private LinkGroupDto convertToDtoWithLinks(LinkGroup linkGroup) {
        LinkGroupDto dto = convertToDto(linkGroup);

        // Ajouter les liens du groupe
        List<SavedLinkGroupDto> links = savedLinkGroupService.getLinksByGroupId(linkGroup.getId());
        dto.setSavedLinks(links);

        return dto;
    }
}