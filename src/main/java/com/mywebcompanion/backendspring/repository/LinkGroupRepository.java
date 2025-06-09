package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.LinkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkGroupRepository extends JpaRepository<LinkGroup, String> {

    List<LinkGroup> findByUserIdOrderByTitleAsc(Long userId);

    Optional<LinkGroup> findByIdAndUserId(String id, Long userId);

    List<LinkGroup> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    Long countByUserId(Long userId);

    // CORRECTION: groupId doit être String si LinkGroup.id est String
    @Query("SELECT COUNT(slg) FROM SavedLinkGroup slg WHERE slg.linkGroup.id = :groupId")
    Long countLinksByGroupId(@Param("groupId") String groupId);
}