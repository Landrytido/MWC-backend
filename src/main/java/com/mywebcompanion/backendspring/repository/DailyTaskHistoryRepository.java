package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyTaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyTaskHistoryRepository extends JpaRepository<DailyTaskHistory, Long> {

    // Trouver l'historique d'un utilisateur pour une date donnée
    List<DailyTaskHistory> findByUserClerkIdAndScheduledDateOrderByOrderIndexAsc(String clerkId, LocalDate date);

    // Rapport mensuel - compter total et complétées
    @Query("SELECT COUNT(dth), SUM(CASE WHEN dth.completed = true THEN 1 ELSE 0 END) " +
            "FROM DailyTaskHistory dth " +
            "WHERE dth.user.clerkId = :clerkId " +
            "AND YEAR(dth.scheduledDate) = :year " +
            "AND MONTH(dth.scheduledDate) = :month")
    Object[] getMonthlyReport(@Param("clerkId") String clerkId, @Param("year") int year, @Param("month") int month);

    // Trouver les tâches créées à une date donnée (basé sur createdAt)
    @Query("SELECT dth FROM DailyTaskHistory dth WHERE dth.user.clerkId = :clerkId AND DATE(dth.createdAt) = :date")
    List<DailyTaskHistory> findTasksCreatedOnDate(@Param("clerkId") String clerkId, @Param("date") LocalDate date);

    // Trouver les tâches complétées à une date donnée
    @Query("SELECT dth FROM DailyTaskHistory dth WHERE dth.user.clerkId = :clerkId AND dth.completed = true AND DATE(dth.completedAt) = :date")
    List<DailyTaskHistory> findTasksCompletedOnDate(@Param("clerkId") String clerkId, @Param("date") LocalDate date);
}