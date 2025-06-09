package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {

    List<DailyTask> findByUserIdAndScheduledDateOrderByOrderIndexAsc(Long userId, LocalDate scheduledDate);

    Optional<DailyTask> findByIdAndUserId(Long id, Long userId);

    Long countByUserIdAndScheduledDate(Long userId, LocalDate scheduledDate);

    List<DailyTask> findByUserIdAndCompletedFalseOrderByScheduledDateAsc(Long userId);

    List<DailyTask> findByUserIdAndCompletedTrueOrderByScheduledDateDesc(Long userId);

    List<DailyTask> findByUserIdAndScheduledDateBetweenOrderByScheduledDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dt FROM DailyTask dt WHERE dt.user.id = :userId AND dt.carriedOver = true ORDER BY dt.scheduledDate ASC")
    List<DailyTask> findCarriedOverTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(dt) FROM DailyTask dt WHERE dt.user.id = :userId AND dt.completed = false AND dt.scheduledDate < :currentDate")
    Long countOverdueTasksByUserId(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(dt) FROM DailyTask dt WHERE dt.user.id = :userId AND dt.scheduledDate = :date AND dt.completed = true")
    Long countCompletedTasksByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}