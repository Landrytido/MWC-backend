package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.LabelDto;
import com.mywebcompanion.backendspring.dto.NoteDto;
import com.mywebcompanion.backendspring.exception.ResourceNotFoundException;
import com.mywebcompanion.backendspring.model.Label;
import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.Notebook;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.CommentRepository;
import com.mywebcompanion.backendspring.repository.LabelRepository;
import com.mywebcompanion.backendspring.repository.NoteRepository;
import com.mywebcompanion.backendspring.repository.NotebookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

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
    private final LabelRepository labelRepository;

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
        if (noteDto.getLabelIds() != null && !noteDto.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (String labelId : noteDto.getLabelIds()) {
                Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));
                labels.add(label);
            }
            note.setLabels(labels);
        }
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
        if (noteDto.getLabelIds() != null && !noteDto.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (String labelId : noteDto.getLabelIds()) {
                Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));
                labels.add(label);
            }
            note.setLabels(labels);
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
        List<Note> notes = noteRepository.findByUserIdAndContentContaining(user.getId(), keyword);

        return notes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoteDto addLabelToNote(String email, Long noteId, String labelId) {
        User user = userService.findByEmail(email);

        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new RuntimeException("Label non trouvé"));

        note.getLabels().add(label);
        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
    }

    @Transactional
    public NoteDto removeLabelFromNote(String email, Long noteId, String labelId) {
        User user = userService.findByEmail(email);

        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        note.getLabels().removeIf(label -> label.getId().equals(labelId));
        Note savedNote = noteRepository.save(note);
        return convertToDto(savedNote);
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

        dto.setLabels(note.getLabels().stream()
                .map(this::convertLabelToDto)
                .collect(Collectors.toList()));

        dto.setCommentCount(commentCounts.getOrDefault(note.getId(), 0L));

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

        dto.setLabels(note.getLabels().stream()
                .map(this::convertLabelToDto)
                .collect(Collectors.toList()));

        dto.setCommentCount(commentRepository.countByNoteId(note.getId()));

        return dto;
    }

    private LabelDto convertLabelToDto(Label label) {
        LabelDto dto = new LabelDto();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        dto.setUpdatedAt(label.getUpdatedAt());
        return dto;
    }
}