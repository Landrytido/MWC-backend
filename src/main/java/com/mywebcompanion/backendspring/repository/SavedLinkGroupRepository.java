package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.SavedLinkGroup;
import com.mywebcompanion.backendspring.model.SavedLinkGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedLinkGroupRepository extends JpaRepository<SavedLinkGroup, SavedLinkGroupId> {

    // Trouver tous les liens d'un groupe
    List<SavedLinkGroup> findByLinkGroupIdOrderByLinkNameAsc(String linkGroupId);

    // Trouver une relation spécifique
    Optional<SavedLinkGroup> findByLinkGroupIdAndSavedLinkId(String linkGroupId, Long savedLinkId);

    // Vérifier si un lien est déjà dans un groupe
    boolean existsByLinkGroupIdAndSavedLinkId(String linkGroupId, Long savedLinkId);

    // Trouver tous les groupes qui contiennent un lien spécifique
    List<SavedLinkGroup> findBySavedLinkId(Long savedLinkId);

    // Supprimer toutes les relations d'un groupe
    void deleteByLinkGroupId(String linkGroupId);

    // Supprimer toutes les relations d'un lien
    void deleteBySavedLinkId(Long savedLinkId);

    // Trouver les liens les plus cliqués dans un groupe
    @Query("SELECT slg FROM SavedLinkGroup slg WHERE slg.linkGroupId = :groupId ORDER BY slg.clickCounter DESC")
    List<SavedLinkGroup> findTopClickedByGroupId(@Param("groupId") String groupId);

    // Trouver les liens les plus cliqués globalement pour un utilisateur
    @Query("SELECT slg FROM SavedLinkGroup slg " +
            "JOIN slg.linkGroup lg " +
            "WHERE lg.user.clerkId = :clerkId " +
            "ORDER BY slg.clickCounter DESC")
    List<SavedLinkGroup> findTopClickedByUserClerkId(@Param("clerkId") String clerkId);
}