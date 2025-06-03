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

    public List<NotebookDto> getAllNotebooksByClerkId(String clerkId) {
        return notebookRepository.findByUserClerkIdOrderByTitleAsc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NotebookDto createNotebook(String clerkId, NotebookDto notebookDto) {
        User user = userService.findByClerkIdMinimal(clerkId);

        Notebook notebook = new Notebook();
        notebook.setTitle(notebookDto.getTitle());
        notebook.setUser(user);

        Notebook savedNotebook = notebookRepository.save(notebook);
        return convertToDto(savedNotebook);
    }

    public NotebookDto updateNotebook(String clerkId, Long notebookId, NotebookDto notebookDto) {
        Notebook notebook = notebookRepository.findByIdAndUserClerkId(notebookId, clerkId)
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));

        notebook.setTitle(notebookDto.getTitle());

        Notebook updatedNotebook = notebookRepository.save(notebook);
        return convertToDto(updatedNotebook);
    }

    public void deleteNotebook(String clerkId, Long notebookId) {
        Notebook notebook = notebookRepository.findByIdAndUserClerkId(notebookId, clerkId)
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));

        // Vérifier s'il y a des notes dans ce carnet
        Long noteCount = notebookRepository.countNotesByNotebookId(notebookId);
        if (noteCount > 0) {
            throw new RuntimeException("Impossible de supprimer un carnet contenant des notes. " +
                    "Déplacez d'abord les " + noteCount + " note(s) vers un autre carnet.");
        }

        notebookRepository.delete(notebook);
    }

    public NotebookDto getNotebookById(String clerkId, Long notebookId) {
        Notebook notebook = notebookRepository.findByIdAndUserClerkId(notebookId, clerkId)
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));

        return convertToDto(notebook);
    }

    private NotebookDto convertToDto(Notebook notebook) {
        NotebookDto dto = new NotebookDto();
        dto.setId(notebook.getId());
        dto.setTitle(notebook.getTitle());
        dto.setCreatedAt(notebook.getCreatedAt());
        dto.setUpdatedAt(notebook.getUpdatedAt());

        // Compter les notes dans ce carnet
        dto.setNoteCount(notebookRepository.countNotesByNotebookId(notebook.getId()));

        return dto;
    }
}