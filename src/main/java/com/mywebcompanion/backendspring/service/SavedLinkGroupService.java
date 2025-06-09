package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.SavedLinkDetailsDto;
import com.mywebcompanion.backendspring.dto.SavedLinkGroupDto;
import com.mywebcompanion.backendspring.model.SavedLink;
import com.mywebcompanion.backendspring.model.SavedLinkGroup;
import com.mywebcompanion.backendspring.model.SavedLinkGroupId;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.LinkGroupRepository;
import com.mywebcompanion.backendspring.repository.SavedLinkGroupRepository;
import com.mywebcompanion.backendspring.repository.SavedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedLinkGroupService {

        private final SavedLinkGroupRepository savedLinkGroupRepository;
        private final LinkGroupRepository linkGroupRepository;
        private final SavedLinkRepository savedLinkRepository;
        private final UserService userService;

        // CHANGÉ: Long linkGroupId → String linkGroupId
        public List<SavedLinkGroupDto> getLinksByGroupId(String email, String linkGroupId) {
                User user = userService.findByEmail(email);

                // Maintenant les types correspondent : String, Long
                linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                return savedLinkGroupRepository.findByLinkGroupIdOrderByLinkNameAsc(linkGroupId)
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        // CHANGÉ: Long linkGroupId → String linkGroupId
        public SavedLinkGroupDto addLinkToGroup(String email, String linkGroupId, Long savedLinkId, String linkName) {
                User user = userService.findByEmail(email);

                linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                SavedLink savedLink = savedLinkRepository.findByIdAndUserId(savedLinkId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Lien sauvegardé non trouvé"));

                if (savedLinkGroupRepository.existsByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)) {
                        throw new RuntimeException("Ce lien est déjà dans ce groupe");
                }

                SavedLinkGroup savedLinkGroup = new SavedLinkGroup();
                savedLinkGroup.setLinkGroupId(linkGroupId);
                savedLinkGroup.setSavedLinkId(savedLinkId);
                savedLinkGroup.setLinkName(linkName != null ? linkName : savedLink.getTitle());
                savedLinkGroup.setClickCounter(0);

                savedLinkGroupRepository.save(savedLinkGroup);

                return convertToDto(savedLinkGroupRepository.findByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)
                                .orElseThrow(() -> new RuntimeException("Erreur lors de la création")));
        }

        // CHANGÉ: Long linkGroupId → String linkGroupId
        public SavedLinkGroupDto updateLinkInGroup(String email, String linkGroupId, Long savedLinkId,
                        String newLinkName) {
                User user = userService.findByEmail(email);

                linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                SavedLinkGroup savedLinkGroup = savedLinkGroupRepository
                                .findByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)
                                .orElseThrow(() -> new RuntimeException("Lien non trouvé dans ce groupe"));

                savedLinkGroup.setLinkName(newLinkName);
                SavedLinkGroup updated = savedLinkGroupRepository.save(savedLinkGroup);

                return convertToDto(updated);
        }

        // CHANGÉ: Long linkGroupId → String linkGroupId
        public SavedLinkGroupDto incrementClickCounter(String email, String linkGroupId, Long savedLinkId) {
                User user = userService.findByEmail(email);

                linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                SavedLinkGroup savedLinkGroup = savedLinkGroupRepository
                                .findByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)
                                .orElseThrow(() -> new RuntimeException("Lien non trouvé dans ce groupe"));

                savedLinkGroup.setClickCounter(savedLinkGroup.getClickCounter() + 1);
                SavedLinkGroup updated = savedLinkGroupRepository.save(savedLinkGroup);

                return convertToDto(updated);
        }

        // CHANGÉ: Long linkGroupId → String linkGroupId
        public void removeLinkFromGroup(String email, String linkGroupId, Long savedLinkId) {
                User user = userService.findByEmail(email);

                linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                SavedLinkGroupId id = new SavedLinkGroupId(linkGroupId, savedLinkId);
                savedLinkGroupRepository.deleteById(id);
        }

        // CHANGÉ: Long linkGroupId → String linkGroupId
        public List<SavedLinkGroupDto> getTopClickedLinks(String email, String linkGroupId) {
                User user = userService.findByEmail(email);

                linkGroupRepository.findByIdAndUserId(linkGroupId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                return savedLinkGroupRepository.findTopClickedByGroupId(linkGroupId)
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        public List<SavedLinkGroupDto> getGlobalTopClickedLinks(String email) {
                User user = userService.findByEmail(email);

                return savedLinkGroupRepository.findTopClickedByUserId(user.getId())
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        // SUPPRIMÉ: Plus besoin de cette méthode de conversion car on utilise
        // directement String

        private SavedLinkGroupDto convertToDto(SavedLinkGroup savedLinkGroup) {
                SavedLinkGroupDto dto = new SavedLinkGroupDto();
                dto.setSavedLinkId(savedLinkGroup.getSavedLinkId());
                dto.setLinkGroupId(savedLinkGroup.getLinkGroupId());
                dto.setLinkName(savedLinkGroup.getLinkName());
                dto.setClickCounter(savedLinkGroup.getClickCounter());

                if (savedLinkGroup.getSavedLink() != null) {
                        SavedLink savedLink = savedLinkGroup.getSavedLink();
                        dto.setUrl(savedLink.getUrl());

                        SavedLinkDetailsDto details = new SavedLinkDetailsDto();
                        details.setId(savedLink.getId());
                        details.setUrl(savedLink.getUrl());
                        details.setTitle(savedLink.getTitle());
                        details.setDescription(savedLink.getDescription());
                        details.setCreatedAt(savedLink.getCreatedAt());
                        dto.setSavedLinkDetails(details);
                }

                return dto;
        }
}