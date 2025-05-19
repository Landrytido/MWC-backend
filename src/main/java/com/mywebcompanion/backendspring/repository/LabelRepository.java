package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, String> {

    // Trouver tous les labels d'un utilisateur
    List<Label> findByUserIdOrderByNameAsc(Integer userId);

    // Trouver un label par nom et utilisateur
    Optional<Label> findByNameAndUserId(String name, Integer userId);

    // Vérifier si un label appartient à un utilisateur
    boolean existsByIdAndUserId(String id, Integer userId);

    // Compter le nombre de notes associées à un label
    @Query("SELECT COUNT(n) FROM Note n JOIN n.labels l WHERE l.id = :labelId")
    Long countNotesByLabelId(@Param("labelId") String labelId);

    // Supprimer un label par ID et utilisateur (sécurité)
    void deleteByIdAndUserId(String id, Integer userId);
}