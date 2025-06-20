package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.NoteDto;
import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.Notebook;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.CommentRepository;
import com.mywebcompanion.backendspring.repository.NoteRepository;
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

    public List<NoteDto> getNotesByUserEmail(String email) {
        User user = userService.findByEmail(email);
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        if (notes.isEmpty()) {
            return List.of();
        }

        List<Long> noteIds = notes.stream().map(Note::getId).collect(Collectors.toList());
        Map<Long, Long> commentCounts = commentRepository.findCommentCountsByNoteIds(noteIds);

        return notes.stream()
                .map(note -> convertToDto(note, commentCounts))
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

        notebookRepository.findByIdAndUserId(notebookId, user.getId())
                .orElseThrow(() -> new RuntimeException("Carnet non trouvé"));

        List<Note> notes = noteRepository.findByUserIdAndNotebookId(user.getId(), notebookId);

        if (notes.isEmpty()) {
            return List.of();
        }

        List<Long> noteIds = notes.stream().map(Note::getId).collect(Collectors.toList());
        Map<Long, Long> commentCounts = commentRepository.findCommentCountsByNoteIds(noteIds);

        return notes.stream()
                .map(note -> convertToDto(note, commentCounts))
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

    private NoteDto convertToDto(Note note, Map<Long, Long> commentCounts) {
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

        dto.setCommentCount(commentCounts.getOrDefault(note.getId(), 0L));

        dto.setTaskCount(0L);
        dto.setCompletedTaskCount(0L);

        return dto;
    }

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

        dto.setCommentCount(commentRepository.countByNoteId(note.getId()));

        dto.setTaskCount(0L);
        dto.setCompletedTaskCount(0L);

        return dto;
    }
}