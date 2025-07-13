package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.SavedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedLinkRepository extends JpaRepository<SavedLink, Long> {

    List<SavedLink> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SavedLink> findByIdAndUserId(Long id, Long userId);

    Long countByUserId(Long userId);

    List<SavedLink> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    List<SavedLink> findByUserIdAndUrlContainingIgnoreCase(Long userId, String url);

    List<SavedLink> findByUserId(Long userId);

}