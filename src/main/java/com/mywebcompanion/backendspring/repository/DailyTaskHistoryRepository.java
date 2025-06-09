package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyTaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyTaskHistoryRepository extends JpaRepository<DailyTaskHistory, Long> {

    // Trouver l'historique des tâches par utilisateur et date planifiée
    List<DailyTaskHistory> findByUserIdAndScheduledDateOrderByOrderIndexAsc(Long userId, LocalDate scheduledDate);

    // Trouver l'historique des tâches par utilisateur pour une période
    List<DailyTaskHistory> findByUserIdAndScheduledDateBetweenOrderByScheduledDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    // Rapport mensuel - retourne [total_tasks, completed_tasks]
    @Query(value = """
            SELECT
                COUNT(*) as total_tasks,
                SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed_tasks
            FROM daily_task_history
            WHERE user_id = :userId
            AND YEAR(scheduled_date) = :year
            AND MONTH(scheduled_date) = :month
            """, nativeQuery = true)
    Object[] getMonthlyReportByUserId(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // Statistiques par semaine
    @Query(value = """
            SELECT
                YEARWEEK(scheduled_date) as week,
                COUNT(*) as total_tasks,
                SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed_tasks
            FROM daily_task_history
            WHERE user_id = :userId
            AND scheduled_date BETWEEN :startDate AND :endDate
            GROUP BY YEARWEEK(scheduled_date)
            ORDER BY week
            """, nativeQuery = true)
    List<Object[]> getWeeklyReportByUserId(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Compter les tâches complétées pour une période
    @Query("SELECT COUNT(h) FROM DailyTaskHistory h WHERE h.user.id = :userId AND h.completed = true AND h.scheduledDate BETWEEN :startDate AND :endDate")
    Long countCompletedTasksByUserIdAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Compter le total des tâches pour une période
    @Query("SELECT COUNT(h) FROM DailyTaskHistory h WHERE h.user.id = :userId AND h.scheduledDate BETWEEN :startDate AND :endDate")
    Long countTotalTasksByUserIdAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Trouver les tâches les plus reportées
    @Query("SELECT h FROM DailyTaskHistory h WHERE h.user.id = :userId AND h.carriedOver = true ORDER BY h.scheduledDate DESC")
    List<DailyTaskHistory> findCarriedOverTasksByUserId(@Param("userId") Long userId);

    // Statistiques de productivité par mois
    @Query(value = """
            SELECT
                YEAR(scheduled_date) as year,
                MONTH(scheduled_date) as month,
                COUNT(*) as total_tasks,
                SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed_tasks,
                ROUND((SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as completion_rate
            FROM daily_task_history
            WHERE user_id = :userId
            GROUP BY YEAR(scheduled_date), MONTH(scheduled_date)
            ORDER BY year DESC, month DESC
            LIMIT 12
            """, nativeQuery = true)
    List<Object[]> getProductivityStatsByUserId(@Param("userId") Long userId);
}