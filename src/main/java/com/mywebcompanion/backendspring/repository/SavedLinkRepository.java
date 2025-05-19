package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.SavedLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedLinkRepository extends JpaRepository<SavedLink, Long> {
    List<SavedLink> findByUserClerkIdOrderByCreatedAtDesc(String clerkId);

    Optional<SavedLink> findByIdAndUserClerkId(Long id, String clerkId);

}