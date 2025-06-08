package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.LinkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LinkGroupRepository extends JpaRepository<LinkGroup, String> {

    List<LinkGroup> findByUserIdOrderByTitleAsc(Long userId);

    Optional<LinkGroup> findByIdAndUserId(Long id, Long userId);

    List<LinkGroup> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    Long countByUserId(Long userId);

    @Query("SELECT COUNT(slg) FROM SavedLinkGroup slg WHERE slg.linkGroup.id = :groupId")
    Long countLinksByGroupId(@Param("groupId") Long groupId);
}