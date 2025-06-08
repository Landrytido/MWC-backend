package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Nouvelles méthodes basées sur userId (Spring Security + JWT)
    List<Task> findByUserIdOrderByDueDateAscCreatedAtDesc(Long userId);

    List<Task> findByUserIdAndCompletedTrueOrderByUpdatedAtDesc(Long userId);

    List<Task> findByUserIdAndCompletedFalseOrderByDueDateAscCreatedAtDesc(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    Long countByUserIdAndCompletedFalse(Long userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.completed = false AND t.dueDate < :now")
    List<Task> findOverdueTasks(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.completed = false AND t.dueDate BETWEEN :start AND :end")
    List<Task> findTasksDueInPeriod(@Param("userId") Long userId, @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}