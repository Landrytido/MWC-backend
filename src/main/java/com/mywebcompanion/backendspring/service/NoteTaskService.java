package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.NoteTaskDto;
import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.NoteTask;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.NoteRepository;
import com.mywebcompanion.backendspring.repository.NoteTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteTaskService {

    private final NoteTaskRepository noteTaskRepository;
    private final NoteRepository noteRepository;
    private final UserService userService;

    public List<NoteTaskDto> getNoteTasksByNoteId(String clerkId, Long noteId) {
        // Vérifier que la note appartient à l'utilisateur
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Vous n'avez pas accès aux tâches de cette note");
        }

        return noteTaskRepository.findByNoteIdAndParentIsNullOrderByCreatedAtAsc(noteId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NoteTaskDto> getAllNoteTasksByUserId(String clerkId) {
        return noteTaskRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NoteTaskDto> getPendingNoteTasksByUserId(String clerkId) {
        return noteTaskRepository.findByUserClerkIdAndCompletedFalseOrderByCreatedAtDesc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NoteTaskDto createNoteTask(String clerkId, Long noteId, String title, Long parentId) {
        User user = userService.findByClerkIdMinimal(clerkId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        // Vérifier que la note appartient à l'utilisateur
        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Vous ne pouvez pas ajouter de tâches à cette note");
        }

        NoteTask noteTask = new NoteTask();
        noteTask.setTitle(title);
        noteTask.setNote(note);
        noteTask.setUser(user);

        // Si c'est une sous-tâche
        if (parentId != null) {
            NoteTask parent = noteTaskRepository.findByIdAndUserClerkId(parentId, clerkId)
                    .orElseThrow(() -> new RuntimeException("Tâche parent non trouvée"));
            noteTask.setParent(parent);
        }

        NoteTask savedTask = noteTaskRepository.save(noteTask);
        return convertToDto(savedTask);
    }

    public NoteTaskDto updateNoteTask(String clerkId, Long taskId, NoteTaskDto dto) {
        NoteTask noteTask = noteTaskRepository.findByIdAndUserClerkId(taskId, clerkId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        noteTask.setTitle(dto.getTitle());

        // Gestion de l'état de completion
        if (dto.getCompleted() != null && !noteTask.getCompleted() && dto.getCompleted()) {
            // Marquer comme complétée
            noteTask.setCompleted(true);

            // Si c'est une tâche parent, vérifier les sous-tâches
            if (noteTask.getParent() == null && !noteTask.getSubtasks().isEmpty()) {
                // Marquer toutes les sous-tâches comme complétées
                for (NoteTask subtask : noteTask.getSubtasks()) {
                    subtask.setCompleted(true);
                    noteTaskRepository.save(subtask);
                }
            }
        } else if (dto.getCompleted() != null) {
            noteTask.setCompleted(dto.getCompleted());

            // Si c'est une sous-tâche et qu'on la marque comme non complétée,
            // s'assurer que la tâche parent n'est pas complétée
            if (noteTask.getParent() != null && !dto.getCompleted()) {
                NoteTask parent = noteTask.getParent();
                if (parent.getCompleted()) {
                    parent.setCompleted(false);
                    noteTaskRepository.save(parent);
                }
            }
        }

        NoteTask updatedTask = noteTaskRepository.save(noteTask);
        return convertToDto(updatedTask);
    }

    public NoteTaskDto toggleNoteTaskCompletion(String clerkId, Long taskId) {
        NoteTask noteTask = noteTaskRepository.findByIdAndUserClerkId(taskId, clerkId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        boolean newCompletionStatus = !noteTask.getCompleted();
        noteTask.setCompleted(newCompletionStatus);

        // Logique de cascade pour les sous-tâches/tâches parent
        if (newCompletionStatus && noteTask.getParent() == null && !noteTask.getSubtasks().isEmpty()) {
            // Marquer toutes les sous-tâches comme complétées
            for (NoteTask subtask : noteTask.getSubtasks()) {
                subtask.setCompleted(true);
                noteTaskRepository.save(subtask);
            }
        } else if (!newCompletionStatus && noteTask.getParent() != null) {
            // Si c'est une sous-tâche qu'on marque comme non complétée,
            // s'assurer que la tâche parent n'est pas complétée
            NoteTask parent = noteTask.getParent();
            if (parent.getCompleted()) {
                parent.setCompleted(false);
                noteTaskRepository.save(parent);
            }
        }

        NoteTask updatedTask = noteTaskRepository.save(noteTask);
        return convertToDto(updatedTask);
    }

    public void deleteNoteTask(String clerkId, Long taskId) {
        NoteTask noteTask = noteTaskRepository.findByIdAndUserClerkId(taskId, clerkId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        // La suppression en cascade des sous-tâches est gérée par JPA
        noteTaskRepository.delete(noteTask);
    }

    public NoteTaskDto getNoteTaskById(String clerkId, Long taskId) {
        NoteTask noteTask = noteTaskRepository.findByIdAndUserClerkId(taskId, clerkId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        return convertToDto(noteTask);
    }

    private NoteTaskDto convertToDto(NoteTask noteTask) {
        NoteTaskDto dto = new NoteTaskDto();
        dto.setId(noteTask.getId());
        dto.setTitle(noteTask.getTitle());
        dto.setCompleted(noteTask.getCompleted());
        dto.setNoteId(noteTask.getNote().getId());
        dto.setParentId(noteTask.getParent() != null ? noteTask.getParent().getId() : null);
        dto.setCreatedAt(noteTask.getCreatedAt());
        dto.setUpdatedAt(noteTask.getUpdatedAt());

        // Convertir les sous-tâches (récursif)
        if (!noteTask.getSubtasks().isEmpty()) {
            dto.setSubtasks(noteTask.getSubtasks().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        }

        // Statistiques pour les tâches parent
        if (noteTask.getParent() == null) {
            Long totalSubtasks = noteTaskRepository.countSubtasksByParentId(noteTask.getId());
            Long completedSubtasks = noteTaskRepository.countCompletedSubtasksByParentId(noteTask.getId());
            dto.setTotalSubtasks(totalSubtasks.intValue());
            dto.setCompletedSubtasks(completedSubtasks.intValue());
        }

        return dto;
    }
}