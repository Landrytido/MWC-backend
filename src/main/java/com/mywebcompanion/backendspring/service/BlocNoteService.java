package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.BlocNoteDto;
import com.mywebcompanion.backendspring.model.BlocNote;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.BlocNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlocNoteService {

    private final BlocNoteRepository blocNoteRepository;
    private final UserService userService;

    public BlocNoteDto getBlocNoteByClerkId(String clerkId) {
        // OPTIMISATION: Une seule requête avec JOIN FETCH
        BlocNote blocNote = blocNoteRepository.findByUserClerkIdWithUser(clerkId)
                .orElse(null);

        if (blocNote == null) {
            // Si aucun bloc-note n'existe, créer un bloc-note vide
            return createEmptyBlocNote(clerkId);
        }

        return convertToDto(blocNote);
    }

    public BlocNoteDto upsertBlocNote(String clerkId, String content) {
        // OPTIMISATION: Utiliser la requête optimisée
        BlocNote blocNote = blocNoteRepository.findByUserClerkIdWithUser(clerkId)
                .orElse(null);

        if (blocNote == null) {
            // Créer un nouveau bloc-note
            User user = userService.findByClerkId(clerkId);

            blocNote = new BlocNote();
            blocNote.setUser(user);
            blocNote.setContent(content);
        } else {
            // Mettre à jour le bloc-note existant
            blocNote.setContent(content);
        }

        BlocNote savedBlocNote = blocNoteRepository.save(blocNote);
        return convertToDto(savedBlocNote);
    }

    public void deleteBlocNote(String clerkId) {
        // OPTIMISATION: Vérifier d'abord l'existence puis supprimer
        if (!blocNoteRepository.existsByUserClerkId(clerkId)) {
            throw new RuntimeException("Bloc-note non trouvé");
        }

        blocNoteRepository.deleteByUserClerkId(clerkId);
    }

    // OPTIMISATION: Cache le user lors de la création
    private BlocNoteDto createEmptyBlocNote(String clerkId) {
        User user = userService.findByClerkId(clerkId);

        BlocNote blocNote = new BlocNote();
        blocNote.setUser(user);
        blocNote.setContent(""); // Contenu vide par défaut

        BlocNote savedBlocNote = blocNoteRepository.save(blocNote);
        return convertToDto(savedBlocNote);
    }

    private BlocNoteDto convertToDto(BlocNote blocNote) {
        BlocNoteDto dto = new BlocNoteDto();
        dto.setId(blocNote.getId());
        dto.setContent(blocNote.getContent());
        dto.setCreatedAt(blocNote.getCreatedAt());
        dto.setUpdatedAt(blocNote.getUpdatedAt());
        return dto;
    }
}