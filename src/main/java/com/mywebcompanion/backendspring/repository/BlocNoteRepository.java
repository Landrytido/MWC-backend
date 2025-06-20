package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.BlocNote;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface BlocNoteRepository extends JpaRepository<BlocNote, Long> {

    Optional<BlocNote> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BlocNote b WHERE b.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}