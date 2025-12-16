package com.budgettracker.repository;

import com.budgettracker.entity.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Integer> {
    
    @Query("SELECT m FROM MonthlySummary m WHERE m.user.id = :userId " +
           "AND m.createdAt >= :startOfDay AND m.createdAt <= :endOfDay")
    Optional<MonthlySummary> findByUserIdAndCreatedAtBetween(
        @Param("userId") Integer userId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
    
    @Query("SELECT m FROM MonthlySummary m WHERE m.user.id = :userId " +
           "AND m.createdAt >= :startOfDay AND m.createdAt <= :endOfDay " +
           "ORDER BY m.createdAt DESC")
    List<MonthlySummary> findAllByUserIdAndCreatedAtBetween(
        @Param("userId") Integer userId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
    
    @Query("SELECT COUNT(m) FROM MonthlySummary m WHERE m.user.id = :userId " +
           "AND m.createdAt >= :startOfDay AND m.createdAt <= :endOfDay")
    long countByUserIdAndCreatedAtBetween(
        @Param("userId") Integer userId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
}

