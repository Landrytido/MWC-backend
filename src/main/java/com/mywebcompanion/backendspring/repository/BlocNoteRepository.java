package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.BlocNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlocNoteRepository extends JpaRepository<BlocNote, Long> {

    // Trouver le bloc-note d'un utilisateur par son clerkId
    Optional<BlocNote> findByUserClerkId(String clerkId);

    // Vérifier si un utilisateur a déjà un bloc-note
    boolean existsByUserClerkId(String clerkId);

    // Supprimer le bloc-note d'un utilisateur
    void deleteByUserClerkId(String clerkId);
}