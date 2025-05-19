package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.NoteDto;
import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.Notebook;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.CommentRepository;
import com.mywebcompanion.backendspring.repository.NoteRepository;
import com.mywebcompanion.backendspring.repository.NotebookRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    @Autowired
    private NotebookRepository notebookRepository;
    @Autowired
    private CommentRepository commentRepository;

    public List<NoteDto> getNotesByClerkId(String clerkId) {
        return noteRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NoteDto createNote(String clerkId, NoteDto noteDto) {
        User user = userService.findByClerkId(clerkId);

        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(user);

        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
    }

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

    public void deleteNote(String clerkId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Vérifier que la note appartient à l'utilisateur
        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Not authorized to delete this note");
        }

        noteRepository.delete(note);
    }

    private NoteDto convertToDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setCommentCount(commentRepository.countByNoteId(note.getId()));

        return dto;
    }

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

        return noteRepository.findByNotebookIdOrderByCreatedAtDesc(notebookId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}