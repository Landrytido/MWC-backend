package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.SavedLinkGroup;
import com.mywebcompanion.backendspring.model.SavedLinkGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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

    // Nouvelles méthodes basées sur userId (Spring Security + JWT)
    @Query("SELECT slg FROM SavedLinkGroup slg " +
            "JOIN slg.linkGroup lg " +
            "WHERE lg.user.id = :userId " +
            "ORDER BY slg.clickCounter DESC")
    List<SavedLinkGroup> findTopClickedByUserId(@Param("userId") Long userId);

    @Query("SELECT slg FROM SavedLinkGroup slg " +
            "WHERE slg.linkGroupId = :linkGroupId " +
            "ORDER BY slg.clickCounter DESC " +
            "LIMIT 10")
    List<SavedLinkGroup> findTopClickedByGroupId(@Param("linkGroupId") String linkGroupId);

    // Trouver les liens d'un utilisateur dans tous ses groupes
    @Query("SELECT slg FROM SavedLinkGroup slg " +
            "JOIN slg.linkGroup lg " +
            "WHERE lg.user.id = :userId " +
            "ORDER BY slg.linkName ASC")
    List<SavedLinkGroup> findAllByUserId(@Param("userId") Long userId);

    // Compter les liens dans les groupes d'un utilisateur
    @Query("SELECT COUNT(slg) FROM SavedLinkGroup slg " +
            "JOIN slg.linkGroup lg " +
            "WHERE lg.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}