package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Méthodes existantes mises à jour pour le tri par priorité
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findByUserIdOrderByPriorityAndCreatedAt(@Param("userId") Long userId);

    List<Task> findByUserIdAndCompletedTrueOrderByUpdatedAtDesc(Long userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.completed = false ORDER BY t.priority DESC, t.scheduledDate ASC, t.dueDate ASC, t.createdAt DESC")
    List<Task> findByUserIdAndCompletedFalseOrderByPriorityAndDates(@Param("userId") Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    Long countByUserIdAndCompletedFalse(Long userId);

    // Nouvelles méthodes pour la planification quotidienne
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = :date ORDER BY t.priority DESC, t.orderIndex ASC, t.createdAt ASC")
    List<Task> findByUserIdAndScheduledDateOrderByPriorityAndOrder(@Param("userId") Long userId,
            @Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = :date AND t.completed = false ORDER BY t.priority DESC, t.orderIndex ASC")
    List<Task> findPendingTasksByUserIdAndScheduledDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = :date AND t.completed = true ORDER BY t.priority DESC, t.completedAt DESC")
    List<Task> findCompletedTasksByUserIdAndScheduledDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Tâches d'aujourd'hui
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = CURRENT_DATE ORDER BY t.priority DESC, t.orderIndex ASC")
    List<Task> findTodayTasksByUserId(@Param("userId") Long userId);

    // Tâches de demain
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = CURRENT_DATE + 1 ORDER BY t.priority DESC, t.orderIndex ASC")
    List<Task> findTomorrowTasksByUserId(@Param("userId") Long userId);

    // Tâches en retard (avec échéance dépassée)
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.completed = false AND t.dueDate < :now ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findOverdueTasksByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Tâches avec échéance dans une période donnée
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.completed = false AND t.dueDate BETWEEN :start AND :end ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findTasksDueInPeriod(@Param("userId") Long userId, @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Tâches reportées
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.carriedOver = true ORDER BY t.priority DESC, t.scheduledDate ASC")
    List<Task> findCarriedOverTasksByUserId(@Param("userId") Long userId);

    // Statistiques mensuelles
    @Query(value = """
            SELECT
                COUNT(*) as total_tasks,
                SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed_tasks,
                t.priority
            FROM tasks t
            WHERE t.user_id = :userId
            AND YEAR(t.created_at) = :year
            AND MONTH(t.created_at) = :month
            GROUP BY t.priority
            """, nativeQuery = true)
    List<Object[]> findMonthlyStatsByPriority(@Param("userId") Long userId, @Param("year") int year,
            @Param("month") int month);

    @Query(value = """
            SELECT
                DATE(t.created_at) as task_date,
                COUNT(*) as total_tasks,
                SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed_tasks
            FROM tasks t
            WHERE t.user_id = :userId
            AND YEAR(t.created_at) = :year
            AND MONTH(t.created_at) = :month
            GROUP BY DATE(t.created_at)
            ORDER BY task_date
            """, nativeQuery = true)
    List<Object[]> findDailyStatsForMonth(@Param("userId") Long userId, @Param("year") int year,
            @Param("month") int month);

    // Compter les tâches par statut
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = CURRENT_DATE AND t.completed = false")
    Long countTodayPendingTasks(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = CURRENT_DATE + 1 AND t.completed = false")
    Long countTomorrowPendingTasks(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.completed = false AND t.dueDate < CURRENT_TIMESTAMP")
    Long countOverdueTasks(@Param("userId") Long userId);

    // Trouver la prochaine position d'ordre pour une date donnée
    @Query("SELECT COALESCE(MAX(t.orderIndex), -1) + 1 FROM Task t WHERE t.user.id = :userId AND t.scheduledDate = :date")
    Integer findNextOrderIndex(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Statistiques globales pour un utilisateur
    @Query(value = """
            SELECT
                COUNT(*) as total_tasks,
                SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed_tasks,
                SUM(CASE WHEN completed = false AND due_date < NOW() THEN 1 ELSE 0 END) as overdue_tasks,
                SUM(CASE WHEN scheduled_date = CURDATE() THEN 1 ELSE 0 END) as today_tasks,
                SUM(CASE WHEN scheduled_date = CURDATE() + INTERVAL 1 DAY THEN 1 ELSE 0 END) as tomorrow_tasks
            FROM tasks
            WHERE user_id = :userId
            """, nativeQuery = true)
    Object[] findUserTaskSummary(@Param("userId") Long userId);

    // Recherche par titre ou description
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    // Tâches par priorité
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.priority = :priority ORDER BY t.scheduledDate ASC, t.dueDate ASC, t.createdAt DESC")
    List<Task> findByUserIdAndPriority(@Param("userId") Long userId, @Param("priority") Integer priority);
}