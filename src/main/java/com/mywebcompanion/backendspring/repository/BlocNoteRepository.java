package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.BlocNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlocNoteRepository extends JpaRepository<BlocNote, Long> {

    @Query("SELECT bn FROM BlocNote bn JOIN FETCH bn.user u WHERE u.clerkId = :clerkId")
    Optional<BlocNote> findByUserClerkIdWithUser(@Param("clerkId") String clerkId);

    boolean existsByUserClerkId(String clerkId);

    void deleteByUserClerkId(String clerkId);
}