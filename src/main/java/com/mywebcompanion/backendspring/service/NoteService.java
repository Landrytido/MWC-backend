package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.NoteDto;
import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.Notebook;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.CommentRepository;
import com.mywebcompanion.backendspring.repository.NoteRepository;
import com.mywebcompanion.backendspring.repository.NoteTaskRepository;
import com.mywebcompanion.backendspring.repository.NotebookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;
    private final NotebookRepository notebookRepository;
    private final CommentRepository commentRepository;
    private final NoteTaskRepository noteTaskRepository;

    public List<NoteDto> getNotesByUserEmail(String email) {
        User user = userService.findByEmail(email);
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        if (notes.isEmpty()) {
            return List.of();
        }

        // Optimisation: Récupérer tous les comptes en une seule requête
        List<Long> noteIds = notes.stream().map(Note::getId).collect(Collectors.toList());

        Map<Long, Long> commentCounts = commentRepository.findCommentCountsByNoteIds(noteIds);
        Map<Long, Long> taskCounts = noteTaskRepository.findTaskCountsByNoteIds(noteIds);
        Map<Long, Long> completedTaskCounts = noteTaskRepository.findCompletedTaskCountsByNoteIds(noteIds);

        return notes.stream()
                .map(note -> convertToDto(note, commentCounts, taskCounts, completedTaskCounts))
                .collect(Collectors.toList());
    }

    @Transactional
    public NoteDto createNote(String email, NoteDto noteDto) {
        User user = userService.findByEmail(email);

        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(user);

        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
    }

    @Transactional
    public NoteDto updateNote(String email, Long noteId, NoteDto noteDto) {
        User user = userService.findByEmail(email);
        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée ou accès non autorisé"));

        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());

        Note updatedNote = noteRepository.save(note);
        return convertToDto(updatedNote);
    }

    @Transactional
    public void deleteNote(String email, Long noteId) {
        User user = userService.findByEmail(email);
        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée ou accès non autorisé"));

        noteRepository.delete(note);
    }

    @Transactional
    public NoteDto createNoteInNotebook(String email, NoteDto noteDto, Long notebookId) {
        User user = userService.findByEmail(email);

        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(user);

        // Assigner au carnet si spécifié
        if (notebookId != null) {
            Notebook notebook = notebookRepository.findByIdAndUserId(notebookId, user.getId())
                    .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));
            note.setNotebook(notebook);
        }

        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
    }

    @Transactional
    public NoteDto moveNoteToNotebook(String email, Long noteId, Long notebookId) {
        User user = userService.findByEmail(email);
        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée ou accès non autorisé"));

        // Si notebookId est null, on retire la note du carnet
        if (notebookId == null) {
            note.setNotebook(null);
        } else {
            Notebook notebook = notebookRepository.findByIdAndUserId(notebookId, user.getId())
                    .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));
            note.setNotebook(notebook);
        }

        Note updatedNote = noteRepository.save(note);
        return convertToDto(updatedNote);
    }

    public List<NoteDto> getNotesByNotebookId(String email, Long notebookId) {
        User user = userService.findByEmail(email);

        // Vérifier que le carnet appartient à l'utilisateur
        notebookRepository.findByIdAndUserId(notebookId, user.getId())
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));

        List<Note> notes = noteRepository.findByUserIdAndNotebookId(user.getId(), notebookId);

        if (notes.isEmpty()) {
            return List.of();
        }

        // Optimisation: Récupérer tous les comptes en une seule requête
        List<Long> noteIds = notes.stream().map(Note::getId).collect(Collectors.toList());

        Map<Long, Long> commentCounts = commentRepository.findCommentCountsByNoteIds(noteIds);
        Map<Long, Long> taskCounts = noteTaskRepository.findTaskCountsByNoteIds(noteIds);
        Map<Long, Long> completedTaskCounts = noteTaskRepository.findCompletedTaskCountsByNoteIds(noteIds);

        return notes.stream()
                .map(note -> convertToDto(note, commentCounts, taskCounts, completedTaskCounts))
                .collect(Collectors.toList());
    }

    public NoteDto getNoteById(String email, Long noteId) {
        User user = userService.findByEmail(email);
        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée ou accès non autorisé"));

        return convertToDto(note);
    }

    public List<NoteDto> searchNotes(String email, String keyword) {
        User user = userService.findByEmail(email);
        List<Note> notes = noteRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(), keyword);

        return notes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Optimisation: Méthode de conversion avec compteurs pré-calculés
    private NoteDto convertToDto(Note note, Map<Long, Long> commentCounts,
            Map<Long, Long> taskCounts, Map<Long, Long> completedTaskCounts) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());

        if (note.getNotebook() != null) {
            dto.setNotebookId(note.getNotebook().getId());
            dto.setNotebookTitle(note.getNotebook().getTitle());
        }

        // Utiliser les compteurs pré-calculés
        dto.setCommentCount(commentCounts.getOrDefault(note.getId(), 0L));
        dto.setTaskCount(taskCounts.getOrDefault(note.getId(), 0L));
        dto.setCompletedTaskCount(completedTaskCounts.getOrDefault(note.getId(), 0L));

        return dto;
    }

    // Méthode de conversion simple pour les cas où les compteurs ne sont pas
    // nécessaires
    private NoteDto convertToDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());

        if (note.getNotebook() != null) {
            dto.setNotebookId(note.getNotebook().getId());
            dto.setNotebookTitle(note.getNotebook().getTitle());
        }

        // Récupérer les compteurs individuellement (moins optimal mais parfois
        // nécessaire)
        dto.setCommentCount(commentRepository.countByNoteId(note.getId()));
        dto.setTaskCount(noteTaskRepository.countByNoteId(note.getId()));
        dto.setCompletedTaskCount(noteTaskRepository.countByNoteIdAndCompletedTrue(note.getId()));

        return dto;
    }
}