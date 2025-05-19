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
        BlocNote blocNote = blocNoteRepository.findByUserClerkId(clerkId)
                .orElse(null);

        if (blocNote == null) {
            // Si aucun bloc-note n'existe, créer un bloc-note vide
            return createEmptyBlocNote(clerkId);
        }

        return convertToDto(blocNote);
    }

    public BlocNoteDto upsertBlocNote(String clerkId, String content) {
        BlocNote blocNote = blocNoteRepository.findByUserClerkId(clerkId)
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
        BlocNote blocNote = blocNoteRepository.findByUserClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("Bloc-note non trouvé"));

        blocNoteRepository.delete(blocNote);
    }

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