package com.budgettracker.repository;

import com.budgettracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    
    List<Budget> findByUserIdAndIsActiveTrue(Integer userId);
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.isActive = true " +
           "AND b.periodStart <= :date " +
           "AND (b.periodEnd IS NULL OR b.periodEnd >= :date)")
    List<Budget> findActiveBudgetsByUserIdAndDate(
        @Param("userId") Integer userId,
        @Param("date") LocalDate date
    );
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId " +
           "AND b.isActive = true " +
           "AND b.periodStart <= :date " +
           "AND (b.periodEnd IS NULL OR b.periodEnd >= :date)")
    Optional<Budget> findActiveBudgetByUserIdAndCategoryAndDate(
        @Param("userId") Integer userId,
        @Param("categoryId") Integer categoryId,
        @Param("date") LocalDate date
    );
    
    List<Budget> findByUserId(Integer userId);
}


