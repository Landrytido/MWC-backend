package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    Optional<DailyPlan> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailyPlan> findByUserIdOrderByDateDesc(Long userId);

    List<DailyPlan> findByUserIdAndConfirmedTrueOrderByDateDesc(Long userId);

    List<DailyPlan> findByUserIdAndConfirmedFalseOrderByDateAsc(Long userId);

    List<DailyPlan> findByUserIdAndDateBetweenOrderByDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);

    Long countByUserIdAndConfirmedTrue(Long userId);

    Long countByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);

    @Query(value = """
            SELECT
                YEAR(date) as year,
                MONTH(date) as month,
                COUNT(*) as total_plans,
                SUM(CASE WHEN confirmed = true THEN 1 ELSE 0 END) as confirmed_plans
            FROM daily_plan
            WHERE user_id = :userId
            AND YEAR(date) = :year
            AND MONTH(date) = :month
            GROUP BY YEAR(date), MONTH(date)
            """, nativeQuery = true)
    Object[] getMonthlyPlanStats(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT dp FROM DailyPlan dp WHERE dp.user.id = :userId AND dp.confirmed = true ORDER BY dp.date DESC")
    List<DailyPlan> findLastConfirmedPlans(@Param("userId") Long userId);
}