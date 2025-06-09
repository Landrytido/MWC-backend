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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteTaskService {

    private final NoteTaskRepository noteTaskRepository;
    private final NoteRepository noteRepository;
    private final UserService userService;

    public List<NoteTaskDto> getNoteTasksByNoteId(String email, Long noteId) {
        List<NoteTask> tasks = noteTaskRepository.findByNoteIdAndParentIsNullOrderByCreatedAtAsc(noteId);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NoteTaskDto> getAllNoteTasksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        List<NoteTask> tasks = noteTaskRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NoteTaskDto> getPendingNoteTasksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        List<NoteTask> tasks = noteTaskRepository.findByUserIdAndCompletedFalseOrderByCreatedAtDesc(user.getId());
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoteTaskDto createNoteTask(String email, Long noteId, String title, Long parentId) {
        User user = userService.findByEmail(email);

        // Vérifier que la note appartient à l'utilisateur
        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée ou accès non autorisé"));

        NoteTask task = new NoteTask();
        task.setTitle(title);
        task.setNote(note);
        task.setUser(user);
        task.setCompleted(false);

        // Si c'est une sous-tâche
        if (parentId != null) {
            NoteTask parent = noteTaskRepository.findByIdAndUserId(parentId, user.getId())
                    .orElseThrow(() -> new RuntimeException("Tâche parent non trouvée"));
            task.setParent(parent);
        }

        NoteTask savedTask = noteTaskRepository.save(task);
        return convertToDto(savedTask);
    }

    @Transactional
    public NoteTaskDto updateNoteTask(String email, Long taskId, NoteTaskDto request) {
        User user = userService.findByEmail(email);
        NoteTask task = noteTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée ou accès non autorisé"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
            if (request.getCompleted()) {
                task.setCompletedAt(LocalDateTime.now());
            } else {
                task.setCompletedAt(null);
            }
        }

        NoteTask updatedTask = noteTaskRepository.save(task);
        return convertToDto(updatedTask);
    }

    @Transactional
    public NoteTaskDto toggleNoteTaskCompletion(String email, Long taskId) {
        User user = userService.findByEmail(email);
        NoteTask task = noteTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée ou accès non autorisé"));

        task.setCompleted(!task.getCompleted());
        if (task.getCompleted()) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        NoteTask updatedTask = noteTaskRepository.save(task);
        return convertToDto(updatedTask);
    }

    @Transactional
    public void deleteNoteTask(String email, Long taskId) {
        User user = userService.findByEmail(email);
        NoteTask task = noteTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée ou accès non autorisé"));

        noteTaskRepository.delete(task);
    }

    public NoteTaskDto getNoteTaskById(String email, Long taskId) {
        User user = userService.findByEmail(email);
        NoteTask task = noteTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée ou accès non autorisé"));

        return convertToDto(task);
    }

    private NoteTaskDto convertToDto(NoteTask task) {
        NoteTaskDto dto = new NoteTaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setCompleted(task.getCompleted());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getNote() != null) {
            dto.setNoteId(task.getNote().getId());
        }

        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }

        dto.setTotalSubtasks(noteTaskRepository.countSubtasksByParentId(task.getId()));
        dto.setCompletedSubtasks(noteTaskRepository.countCompletedSubtasksByParentId(task.getId()));

        return dto;
    }
}