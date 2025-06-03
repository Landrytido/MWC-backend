package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.CommentDto;
import com.mywebcompanion.backendspring.dto.UserDto;
import com.mywebcompanion.backendspring.model.Comment;
import com.mywebcompanion.backendspring.model.Note;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.CommentRepository;
import com.mywebcompanion.backendspring.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final UserService userService;

    public List<CommentDto> getCommentsByNoteId(String clerkId, Long noteId) {
        // Vérifier que l'utilisateur peut accéder à cette note
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        // Pour l'instant, seul le propriétaire de la note peut voir ses commentaires
        // Plus tard, on peut ajouter la gestion des notes partagées
        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Vous n'avez pas accès aux commentaires de cette note");
        }

        return commentRepository.findByNoteIdOrderByCreatedAtAsc(noteId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(String clerkId, Long noteId, String content) {
        User user = userService.findByClerkIdMinimal(clerkId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        // Vérifier que l'utilisateur peut commenter cette note
        // Pour l'instant, seul le propriétaire peut commenter ses propres notes
        if (!note.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Vous ne pouvez pas commenter cette note");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setNote(note);

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    public CommentDto updateComment(String clerkId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        // Vérifier que l'utilisateur est l'auteur du commentaire
        if (!comment.getUser().getClerkId().equals(clerkId)) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres commentaires");
        }

        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    public void deleteComment(String clerkId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        // Vérifier que l'utilisateur est l'auteur du commentaire OU le propriétaire de
        // la note
        boolean isAuthor = comment.getUser().getClerkId().equals(clerkId);
        boolean isNoteOwner = comment.getNote().getUser().getClerkId().equals(clerkId);

        if (!isAuthor && !isNoteOwner) {
            throw new RuntimeException(
                    "Vous ne pouvez supprimer que vos propres commentaires ou les commentaires sur vos notes");
        }

        commentRepository.delete(comment);
    }

    public List<CommentDto> getMyComments(String clerkId) {
        return commentRepository.findByUserClerkIdOrderByCreatedAtDesc(clerkId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setNoteId(comment.getNote().getId());
        dto.setCreatedAt(comment.getCreatedAt());

        // Convertir l'auteur en UserDto
        UserDto authorDto = new UserDto();
        authorDto.setClerkId(comment.getUser().getClerkId());
        authorDto.setEmail(comment.getUser().getEmail());
        authorDto.setFirstName(comment.getUser().getFirstName());
        authorDto.setLastName(comment.getUser().getLastName());
        dto.setAuthor(authorDto);

        return dto;
    }
}