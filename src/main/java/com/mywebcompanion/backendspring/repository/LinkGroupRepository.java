package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.LinkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LinkGroupRepository extends JpaRepository<LinkGroup, String> {

    // Trouver tous les groupes d'un utilisateur
    List<LinkGroup> findByUserClerkIdOrderByTitleAsc(String clerkId);

    // Trouver un groupe par ID et utilisateur (sécurité)
    Optional<LinkGroup> findByIdAndUserClerkId(String id, String clerkId);

    // Vérifier si un groupe appartient à un utilisateur
    boolean existsByIdAndUserClerkId(String id, String clerkId);

    // Compter le nombre de liens dans un groupe
    @Query("SELECT COUNT(slg) FROM SavedLinkGroup slg WHERE slg.linkGroupId = :groupId")
    Long countLinksByGroupId(@Param("groupId") String groupId);

    // Supprimer un groupe par ID et utilisateur (sécurité)
    void deleteByIdAndUserClerkId(String id, String clerkId);
}