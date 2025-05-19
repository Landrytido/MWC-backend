package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    Optional<DailyPlan> findByUserClerkIdAndDate(String clerkId, LocalDate date);

    boolean existsByUserClerkIdAndDate(String clerkId, LocalDate date);
}