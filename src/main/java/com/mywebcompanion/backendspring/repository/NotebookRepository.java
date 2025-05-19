package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {

    // Trouver tous les carnets d'un utilisateur, triés par titre
    List<Notebook> findByUserClerkIdOrderByTitleAsc(String clerkId);

    // Trouver un carnet par ID et utilisateur (sécurité)
    Optional<Notebook> findByIdAndUserClerkId(Long id, String clerkId);

    // Vérifier si un carnet appartient à un utilisateur
    boolean existsByIdAndUserClerkId(Long id, String clerkId);

    // Compter le nombre de notes dans un carnet
    @Query("SELECT COUNT(n) FROM Note n WHERE n.notebook.id = :notebookId")
    Long countNotesByNotebookId(@Param("notebookId") Long notebookId);

    // Supprimer un carnet par ID et utilisateur (sécurité)
    void deleteByIdAndUserClerkId(Long id, String clerkId);
}