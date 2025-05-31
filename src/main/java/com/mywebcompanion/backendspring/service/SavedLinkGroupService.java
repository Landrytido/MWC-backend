package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.SavedLinkDetailsDto;
import com.mywebcompanion.backendspring.dto.SavedLinkGroupDto;
import com.mywebcompanion.backendspring.model.SavedLink;
import com.mywebcompanion.backendspring.model.SavedLinkGroup;
import com.mywebcompanion.backendspring.model.SavedLinkGroupId;
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

        public List<SavedLinkGroupDto> getLinksByGroupId(String linkGroupId) {
                return savedLinkGroupRepository.findByLinkGroupIdOrderByLinkNameAsc(linkGroupId)
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        public SavedLinkGroupDto addLinkToGroup(String clerkId, String linkGroupId, Long savedLinkId, String linkName) {
                // Vérifier que le groupe appartient à l'utilisateur
                linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                // Vérifier que le lien appartient à l'utilisateur
                SavedLink savedLink = savedLinkRepository.findByIdAndUserClerkId(savedLinkId, clerkId)
                                .orElseThrow(() -> new RuntimeException("Lien sauvegardé non trouvé"));

                // Vérifier si le lien n'est pas déjà dans le groupe
                if (savedLinkGroupRepository.existsByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)) {
                        throw new RuntimeException("Ce lien est déjà dans ce groupe");
                }

                // Créer la relation
                SavedLinkGroup savedLinkGroup = new SavedLinkGroup();
                savedLinkGroup.setLinkGroupId(linkGroupId);
                savedLinkGroup.setSavedLinkId(savedLinkId);
                savedLinkGroup.setLinkName(linkName != null ? linkName : savedLink.getTitle());
                savedLinkGroup.setClickCounter(0);

                // 🔧 CORRECTION : SAUVEGARDER AVANT DE RÉCUPÉRER
                savedLinkGroupRepository.save(savedLinkGroup);

                // Recharger avec les relations pour s'assurer d'avoir toutes les données
                return convertToDto(savedLinkGroupRepository.findByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)
                                .orElseThrow(() -> new RuntimeException("Erreur lors de la création")));
        }

        public SavedLinkGroupDto updateLinkInGroup(String clerkId, String linkGroupId, Long savedLinkId,
                        String newLinkName) {
                // Vérifier que le groupe appartient à l'utilisateur
                linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                // Trouver la relation
                SavedLinkGroup savedLinkGroup = savedLinkGroupRepository
                                .findByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)
                                .orElseThrow(() -> new RuntimeException("Lien non trouvé dans ce groupe"));

                // Mettre à jour le nom
                savedLinkGroup.setLinkName(newLinkName);
                SavedLinkGroup updated = savedLinkGroupRepository.save(savedLinkGroup);

                return convertToDto(updated);
        }

        public SavedLinkGroupDto incrementClickCounter(String clerkId, String linkGroupId, Long savedLinkId) {
                // Vérifier que le groupe appartient à l'utilisateur
                linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                // Trouver la relation
                SavedLinkGroup savedLinkGroup = savedLinkGroupRepository
                                .findByLinkGroupIdAndSavedLinkId(linkGroupId, savedLinkId)
                                .orElseThrow(() -> new RuntimeException("Lien non trouvé dans ce groupe"));

                // Incrémenter le compteur
                savedLinkGroup.setClickCounter(savedLinkGroup.getClickCounter() + 1);
                SavedLinkGroup updated = savedLinkGroupRepository.save(savedLinkGroup);

                return convertToDto(updated);
        }

        public void removeLinkFromGroup(String clerkId, String linkGroupId, Long savedLinkId) {
                // Vérifier que le groupe appartient à l'utilisateur
                linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                // Supprimer la relation
                SavedLinkGroupId id = new SavedLinkGroupId(linkGroupId, savedLinkId);
                savedLinkGroupRepository.deleteById(id);
        }

        public List<SavedLinkGroupDto> getTopClickedLinks(String clerkId, String linkGroupId) {
                // Vérifier que le groupe appartient à l'utilisateur
                linkGroupRepository.findByIdAndUserClerkId(linkGroupId, clerkId)
                                .orElseThrow(() -> new RuntimeException("Groupe de liens non trouvé"));

                return savedLinkGroupRepository.findTopClickedByGroupId(linkGroupId)
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        public List<SavedLinkGroupDto> getGlobalTopClickedLinks(String clerkId) {
                return savedLinkGroupRepository.findTopClickedByUserClerkId(clerkId)
                                .stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
        }

        private SavedLinkGroupDto convertToDto(SavedLinkGroup savedLinkGroup) {
                SavedLinkGroupDto dto = new SavedLinkGroupDto();
                dto.setSavedLinkId(savedLinkGroup.getSavedLinkId());
                dto.setLinkGroupId(savedLinkGroup.getLinkGroupId());
                dto.setLinkName(savedLinkGroup.getLinkName());
                dto.setClickCounter(savedLinkGroup.getClickCounter());

                // Ajouter les détails du lien sauvegardé si disponible
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