package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long> {

    // Nouvelles méthodes basées sur userId (Spring Security + JWT)
    List<Notebook> findByUserIdOrderByTitleAsc(Long userId);

    Optional<Notebook> findByIdAndUserId(Long id, Long userId);

    List<Notebook> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    Long countByUserId(Long userId);

    @Query("SELECT COUNT(n) FROM Note n WHERE n.notebook.id = :notebookId AND n.user.id = :userId")
    Long countNotesByNotebookIdAndUserId(@Param("notebookId") Long notebookId, @Param("userId") Long userId);
}