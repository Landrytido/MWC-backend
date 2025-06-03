package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.BlocNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlocNoteRepository extends JpaRepository<BlocNote, Long> {

    @Query("SELECT bn FROM BlocNote bn WHERE bn.user.clerkId = :clerkId")
    Optional<BlocNote> findByUserClerkId(@Param("clerkId") String clerkId);

    @Query("SELECT COUNT(bn) > 0 FROM BlocNote bn WHERE bn.user.clerkId = :clerkId")
    boolean existsByUserClerkId(@Param("clerkId") String clerkId);

    @Modifying
    @Query("DELETE FROM BlocNote bn WHERE bn.user.clerkId = :clerkId")
    void deleteByUserClerkId(@Param("clerkId") String clerkId);
}