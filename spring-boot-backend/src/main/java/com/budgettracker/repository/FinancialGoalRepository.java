package com.budgettracker.repository;

import com.budgettracker.entity.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Integer> {
    
    List<FinancialGoal> findByUserId(Integer userId);
    
    List<FinancialGoal> findByUserIdAndStatus(Integer userId, FinancialGoal.GoalStatus status);
    
    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.user.id = :userId " +
           "AND fg.status = :status " +
           "AND fg.deadline >= :today " +
           "ORDER BY fg.deadline ASC")
    List<FinancialGoal> findActiveGoalsByUserId(
        @Param("userId") Integer userId,
        @Param("status") FinancialGoal.GoalStatus status,
        @Param("today") LocalDate today
    );
    
    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.user.id = :userId " +
           "AND fg.status = 'COMPLETED' " +
           "ORDER BY fg.updatedAt DESC")
    List<FinancialGoal> findCompletedGoalsByUserId(@Param("userId") Integer userId);
}


