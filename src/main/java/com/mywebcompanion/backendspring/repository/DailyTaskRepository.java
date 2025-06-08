package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyPlan;
import com.mywebcompanion.backendspring.model.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {

    // Nouvelles méthodes basées sur userId (Spring Security + JWT)
    List<DailyTask> findByUserIdAndScheduledDateOrderByOrderIndexAsc(Long userId, LocalDate date);

    Optional<DailyPlan> findByUserIdAndDate(Long userId, LocalDate date);

    Long countByUserIdAndScheduledDate(Long userId, LocalDate date);

    Optional<DailyTask> findByIdAndUserId(Long id, Long userId);

    List<DailyTask> findByUserIdAndScheduledDateBeforeAndCompletedFalse(Long userId, LocalDate date);

    @Query("SELECT dt FROM DailyTask dt WHERE dt.user.id = :userId AND dt.completed = true AND DATE(dt.completedAt) = :date")
    List<DailyTask> findCompletedTasksOnDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    void deleteByIdAndUserId(Long id, Long userId);

    Optional<DailyPlan> findByUserIdAndDate(Long userId, LocalDate date);

}