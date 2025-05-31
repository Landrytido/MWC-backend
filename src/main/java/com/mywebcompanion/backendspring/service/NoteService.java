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
@Transactional(readOnly = true) // 🔧 Par défaut en lecture seule
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;
    private final NotebookRepository notebookRepository;
    private final CommentRepository commentRepository;
    private final NoteTaskRepository noteTaskRepository;

    public List<NoteDto> getNotesByClerkId(String clerkId) {
        // 🔧 OPTIMISATION: Une seule requête pour les notes
        List<Note> notes = noteRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId);

        if (notes.isEmpty()) {
            return List.of();
        }

        // 🔧 OPTIMISATION: Récupérer tous les comptes en une seule requête
        List<Long> noteIds = notes.stream().map(Note::getId).collect(Collectors.toList());

        Map<Long, Long> commentCounts = commentRepository.findCommentCountsByNoteIds(noteIds);
        Map<Long, Long> taskCounts = noteTaskRepository.findTaskCountsByNoteIds(noteIds);
        Map<Long, Long> completedTaskCounts = noteTaskRepository.findCompletedTaskCountsByNoteIds(noteIds);

        return notes.stream()
                .map(note -> convertToDto(note, commentCounts, taskCounts, completedTaskCounts))
                .collect(Collectors.toList());
    }

    @Transactional // 🔧 Écriture nécessaire
    public NoteDto createNote(String clerkId, NoteDto noteDto) {
        User user = userService.findByClerkId(clerkId);

        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(user);

        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
    }

    @Transactional // 🔧 Écriture nécessaire
    public NoteDto updateNote(String clerkId, Long noteId, NoteDto noteDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Vérifier que la note appartient à l'utilisateur
        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Not authorized to update this note");
        }

        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());

        Note updatedNote = noteRepository.save(note);
        return convertToDto(updatedNote);
    }

    @Transactional // 🔧 Écriture nécessaire
    public void deleteNote(String clerkId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Vérifier que la note appartient à l'utilisateur
        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Not authorized to delete this note");
        }

        noteRepository.delete(note);
    }

    @Transactional // 🔧 Écriture nécessaire
    public NoteDto createNoteInNotebook(String clerkId, NoteDto noteDto, Long notebookId) {
        User user = userService.findByClerkId(clerkId);

        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(user);

        // Assigner au carnet si spécifié
        if (notebookId != null) {
            Notebook notebook = notebookRepository.findByIdAndUserClerkId(notebookId, clerkId)
                    .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));
            note.setNotebook(notebook);
        }

        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
    }

    @Transactional // 🔧 Écriture nécessaire
    public NoteDto moveNoteToNotebook(String clerkId, Long noteId, Long notebookId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Not authorized to update this note");
        }

        // Si notebookId est null, on retire la note du carnet
        if (notebookId == null) {
            note.setNotebook(null);
        } else {
            Notebook notebook = notebookRepository.findByIdAndUserClerkId(notebookId, clerkId)
                    .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));
            note.setNotebook(notebook);
        }

        Note updatedNote = noteRepository.save(note);
        return convertToDto(updatedNote);
    }

    public List<NoteDto> getNotesByNotebookId(String clerkId, Long notebookId) {
        // Vérifier que le carnet appartient à l'utilisateur
        notebookRepository.findByIdAndUserClerkId(notebookId, clerkId)
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));

        List<Note> notes = noteRepository.findByNotebookIdOrderByCreatedAtDesc(notebookId);

        if (notes.isEmpty()) {
            return List.of();
        }

        // 🔧 OPTIMISATION: Récupérer tous les comptes en une seule requête
        List<Long> noteIds = notes.stream().map(Note::getId).collect(Collectors.toList());

        Map<Long, Long> commentCounts = commentRepository.findCommentCountsByNoteIds(noteIds);
        Map<Long, Long> taskCounts = noteTaskRepository.findTaskCountsByNoteIds(noteIds);
        Map<Long, Long> completedTaskCounts = noteTaskRepository.findCompletedTaskCountsByNoteIds(noteIds);

        return notes.stream()
                .map(note -> convertToDto(note, commentCounts, taskCounts, completedTaskCounts))
                .collect(Collectors.toList());
    }

    // 🔧 OPTIMISATION: Méthode de conversion avec compteurs pré-calculés
    private NoteDto convertToDto(Note note, Map<Long, Long> commentCounts,
            Map<Long, Long> taskCounts, Map<Long, Long> completedTaskCounts) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());

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

    // 🔧 Méthode de conversion simple pour les cas où les compteurs ne sont pas
    // nécessaires
    private NoteDto convertToDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());

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