package com.mywebcompanion.backendspring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private Long noteId;
    private LocalDateTime createdAt;

    // Informations sur l'auteur du commentaire
    private UserDto author;
}