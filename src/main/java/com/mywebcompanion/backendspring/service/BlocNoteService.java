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

        // ✅ REQUÊTE DIRECTE - évite le chargement des collections User
        Optional<BlocNote> blocNoteOpt = blocNoteRepository.findByUserClerkId(clerkId);

        if (blocNoteOpt.isEmpty()) {
            return createEmptyBlocNote(clerkId);
        }

        return convertToDto(blocNoteOpt.get());
    }

    public BlocNoteDto upsertBlocNote(String clerkId, String content) {
        Optional<BlocNote> existingBlocNote = blocNoteRepository.findByUserClerkId(clerkId);

        BlocNote blocNote;
        if (existingBlocNote.isPresent()) {
            blocNote = existingBlocNote.get();
            blocNote.setContent(content);
        } else {
            // ✅ Utiliser la méthode simple pour récupérer juste l'User
            User user = userService.findByClerkIdSimple(clerkId);
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
        // ✅ Utiliser la méthode simple
        User user = userService.findByClerkIdSimple(clerkId);

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