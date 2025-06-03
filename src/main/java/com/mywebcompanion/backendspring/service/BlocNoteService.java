package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.BlocNoteDto;
import com.mywebcompanion.backendspring.model.BlocNote;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.BlocNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlocNoteService {

    private final BlocNoteRepository blocNoteRepository;
    private final UserService userService;

    public BlocNoteDto getBlocNoteByClerkId(String clerkId) {
        System.out.println("🔍 getBlocNoteByClerkId - clerkId: " + clerkId);

        // ✅ UNE SEULE requête optimisée
        Optional<BlocNote> blocNoteOpt = blocNoteRepository.findByUserClerkId(clerkId);

        if (blocNoteOpt.isEmpty()) {
            return createEmptyBlocNote(clerkId);
        }

        return convertToDto(blocNoteOpt.get());
    }

    public BlocNoteDto upsertBlocNote(String clerkId, String content) {
        // ✅ Vérifier d'abord l'existence
        Optional<BlocNote> existingBlocNote = blocNoteRepository.findByUserClerkId(clerkId);

        BlocNote blocNote;
        if (existingBlocNote.isPresent()) {
            // Mettre à jour le bloc-note existant
            blocNote = existingBlocNote.get();
            blocNote.setContent(content);
        } else {
            // Créer un nouveau bloc-note
            User user = userService.findByClerkIdMinimal(clerkId); // ✅ Méthode minimale
            blocNote = new BlocNote();
            blocNote.setUser(user);
            blocNote.setContent(content);
        }

        BlocNote savedBlocNote = blocNoteRepository.save(blocNote);
        return convertToDto(savedBlocNote);
    }

    public void deleteBlocNote(String clerkId) {
        if (!blocNoteRepository.existsByUserClerkId(clerkId)) {
            throw new RuntimeException("Bloc-note non trouvé");
        }
        blocNoteRepository.deleteByUserClerkId(clerkId);
    }

    private BlocNoteDto createEmptyBlocNote(String clerkId) {
        User user = userService.findByClerkIdMinimal(clerkId); // ✅ Méthode minimale

        BlocNote blocNote = new BlocNote();
        blocNote.setUser(user);
        blocNote.setContent("");

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