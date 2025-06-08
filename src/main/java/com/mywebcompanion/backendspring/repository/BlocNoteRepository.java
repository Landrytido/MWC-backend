package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.BlocNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlocNoteRepository extends JpaRepository<BlocNote, Long> {

    // Remplacer les méthodes Clerk par des méthodes basées sur User ID
    Optional<BlocNote> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);

}