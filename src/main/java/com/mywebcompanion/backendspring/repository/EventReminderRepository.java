package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.EventReminder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;

@Repository
public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {

    List<EventReminder> findByEventIdAndSentFalse(Long eventId);

    @Query("SELECT r FROM EventReminder r WHERE r.sent = false " +
            "AND r.scheduledFor <= :now")
    List<EventReminder> findPendingReminders(@Param("now") LocalDateTime now);
}