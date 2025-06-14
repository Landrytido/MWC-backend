package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.BlocNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

@Repository
public interface BlocNoteRepository extends JpaRepository<BlocNote, Long> {

    Optional<BlocNote> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

}