package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, String> {

    List<Label> findByUserIdOrderByNameAsc(Long userId);

    Optional<Label> findByIdAndUserId(String id, Long userId);

    Optional<Label> findByNameAndUserId(String name, Long userId);

    List<Label> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);

    Long countByUserId(Long userId);

    @Query("SELECT COUNT(n) FROM Note n JOIN n.labels l WHERE l.id = :labelId AND n.user.id = :userId")
    Long countNotesByLabelIdAndUserId(@Param("labelId") String labelId, @Param("userId") Long userId);
}