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

    // Remplacer clerkId par email de l'utilisateur authentifié
    public BlocNoteDto getBlocNoteByUserEmail(String email) {
        System.out.println("🔍 getBlocNoteByUserEmail - email: " + email);

        // Récupérer l'utilisateur par email
        User user = userService.findByEmail(email);

        // Chercher le bloc-note de cet utilisateur
        Optional<BlocNote> blocNoteOpt = blocNoteRepository.findByUserId(user.getId());

        if (blocNoteOpt.isEmpty()) {
            return createEmptyBlocNote(user);
        }

        return convertToDto(blocNoteOpt.get());
    }

    public BlocNoteDto upsertBlocNote(String email, String content) {
        User user = userService.findByEmail(email);
        Optional<BlocNote> existingBlocNote = blocNoteRepository.findByUserId(user.getId());

        BlocNote blocNote;
        if (existingBlocNote.isPresent()) {
            blocNote = existingBlocNote.get();
            blocNote.setContent(content);
        } else {
            blocNote = new BlocNote();
            blocNote.setUser(user);
            blocNote.setContent(content);
        }

        BlocNote savedBlocNote = blocNoteRepository.save(blocNote);
        return convertToDto(savedBlocNote);
    }

    public void deleteBlocNote(String email) {
        User user = userService.findByEmail(email);

        if (!blocNoteRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("Bloc-note non trouvé");
        }

        blocNoteRepository.deleteByUserId(user.getId());
    }

    private BlocNoteDto createEmptyBlocNote(User user) {
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