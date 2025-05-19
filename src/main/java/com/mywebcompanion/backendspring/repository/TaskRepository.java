package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Trouver toutes les tâches d'un utilisateur, triées par date d'échéance
    List<Task> findByUserClerkIdOrderByDueDateAscCreatedAtDesc(String clerkId);

    // Trouver les tâches complétées d'un utilisateur
    List<Task> findByUserClerkIdAndCompletedTrueOrderByUpdatedAtDesc(String clerkId);

    // Trouver les tâches non complétées d'un utilisateur
    List<Task> findByUserClerkIdAndCompletedFalseOrderByDueDateAscCreatedAtDesc(String clerkId);

    // Trouver les tâches en retard (non complétées et date d'échéance passée)
    @Query("SELECT t FROM Task t WHERE t.user.clerkId = :clerkId AND t.completed = false AND t.dueDate < :now")
    List<Task> findOverdueTasks(@Param("clerkId") String clerkId, @Param("now") LocalDateTime now);

    // Trouver les tâches dues dans les prochains X jours
    @Query("SELECT t FROM Task t WHERE t.user.clerkId = :clerkId AND t.completed = false AND t.dueDate BETWEEN :now AND :futureDate")
    List<Task> findTasksDueInPeriod(@Param("clerkId") String clerkId, @Param("now") LocalDateTime now,
            @Param("futureDate") LocalDateTime futureDate);

    // Trouver une tâche par ID et utilisateur (sécurité)
    Optional<Task> findByIdAndUserClerkId(Long id, String clerkId);

    // Compter les tâches non complétées d'un utilisateur
    Long countByUserClerkIdAndCompletedFalse(String clerkId);

    // Supprimer une tâche par ID et utilisateur (sécurité)
    void deleteByIdAndUserClerkId(Long id, String clerkId);
}