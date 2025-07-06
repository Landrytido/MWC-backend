package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.*;
import java.util.*;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

        List<Event> findByUserIdOrderByStartDateAsc(Long userId);

        Optional<Event> findByIdAndUserId(Long id, Long userId);

        // Événements dans une période
        @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
                        "AND e.startDate >= :start AND e.startDate <= :end " +
                        "ORDER BY e.startDate ASC")
        List<Event> findByUserIdAndDateRange(@Param("userId") Long userId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        // Événements d'un mois donné
        @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
                        "AND YEAR(e.startDate) = :year AND MONTH(e.startDate) = :month " +
                        "ORDER BY e.startDate ASC")
        List<Event> findByUserIdAndYearAndMonth(@Param("userId") Long userId,
                        @Param("year") int year,
                        @Param("month") int month);

        // Événements d'une journée
        @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
                        "AND DATE(e.startDate) = :date " +
                        "ORDER BY e.startDate ASC")
        List<Event> findByUserIdAndDate(@Param("userId") Long userId,
                        @Param("date") LocalDate date);

        // Événements avec rappels à envoyer
        @Query("SELECT DISTINCT e FROM Event e " +
                        "JOIN e.reminders r " +
                        "WHERE r.sent = false " +
                        "AND r.scheduledFor <= :now")
        List<Event> findEventsWithPendingReminders(@Param("now") LocalDateTime now);

}
