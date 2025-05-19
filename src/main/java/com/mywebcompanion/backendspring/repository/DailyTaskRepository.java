package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {

    // Trouver les tâches d'un utilisateur pour une date donnée
    List<DailyTask> findByUserClerkIdAndScheduledDateOrderByOrderIndexAsc(String clerkId, LocalDate date);

    // Compter les tâches d'un utilisateur pour une date donnée
    Long countByUserClerkIdAndScheduledDate(String clerkId, LocalDate date);

    // Trouver une tâche par ID et utilisateur
    Optional<DailyTask> findByIdAndUserClerkId(Long id, String clerkId);

    // Trouver les tâches non complétées avant une certaine date (pour report)
    List<DailyTask> findByUserClerkIdAndScheduledDateBeforeAndCompletedFalse(String clerkId, LocalDate date);

    // Trouver les tâches complétées à une date donnée
    @Query("SELECT dt FROM DailyTask dt WHERE dt.user.clerkId = :clerkId AND dt.completed = true AND DATE(dt.completedAt) = :date")
    List<DailyTask> findCompletedTasksOnDate(@Param("clerkId") String clerkId, @Param("date") LocalDate date);

    // Supprimer une tâche par ID et utilisateur
    void deleteByIdAndUserClerkId(Long id, String clerkId);
}