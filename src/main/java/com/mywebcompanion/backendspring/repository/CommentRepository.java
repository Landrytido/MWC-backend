package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

        List<Comment> findByNoteIdOrderByCreatedAtAsc(Long noteId);

        List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

}