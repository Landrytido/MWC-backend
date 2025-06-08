package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.NotebookDto;
import com.mywebcompanion.backendspring.model.Notebook;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.NotebookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final UserService userService;

    public List<NotebookDto> getAllNotebooksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        return notebookRepository.findByUserIdOrderByTitleAsc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NotebookDto createNotebook(String email, NotebookDto notebookDto) {
        User user = userService.findByEmail(email);

        Notebook notebook = new Notebook();
        notebook.setTitle(notebookDto.getTitle());
        notebook.setUser(user);

        Notebook savedNotebook = notebookRepository.save(notebook);
        return convertToDto(savedNotebook);
    }

    public NotebookDto updateNotebook(String email, Long notebookId, NotebookDto notebookDto) {
        User user = userService.findByEmail(email);
        Notebook notebook = notebookRepository.findByIdAndUserId(notebookId, user.getId())
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé ou accès non autorisé"));

        notebook.setTitle(notebookDto.getTitle());

        Notebook updatedNotebook = notebookRepository.save(notebook);
        return convertToDto(updatedNotebook);
    }

    public void deleteNotebook(String email, Long notebookId) {
        User user = userService.findByEmail(email);
        Notebook notebook = notebookRepository.findByIdAndUserId(notebookId, user.getId())
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé ou accès non autorisé"));

        // Vérifier s'il y a des notes dans ce carnet
        Long noteCount = notebookRepository.countNotesByNotebookIdAndUserId(notebookId, user.getId());
        if (noteCount > 0) {
            throw new RuntimeException("Impossible de supprimer un carnet contenant des notes. " +
                    "Déplacez d'abord les " + noteCount + " note(s) vers un autre carnet.");
        }

        notebookRepository.delete(notebook);
    }

    public NotebookDto getNotebookById(String email, Long notebookId) {
        User user = userService.findByEmail(email);
        Notebook notebook = notebookRepository.findByIdAndUserId(notebookId, user.getId())
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé ou accès non autorisé"));

        return convertToDto(notebook);
    }

    private NotebookDto convertToDto(Notebook notebook) {
        NotebookDto dto = new NotebookDto();
        dto.setId(notebook.getId());
        dto.setTitle(notebook.getTitle());
        dto.setCreatedAt(notebook.getCreatedAt());
        dto.setUpdatedAt(notebook.getUpdatedAt());

        // Compter les notes dans ce carnet pour cet utilisateur
        dto.setNoteCount(
                notebookRepository.countNotesByNotebookIdAndUserId(notebook.getId(), notebook.getUser().getId()));

        return dto;
    }
}