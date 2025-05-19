package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    List<Note> findByNotebookIdOrderByCreatedAtDesc(Long notebookId);
}