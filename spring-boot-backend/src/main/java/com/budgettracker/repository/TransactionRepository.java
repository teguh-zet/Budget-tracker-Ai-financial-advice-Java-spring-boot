package com.budgettracker.repository;

import com.budgettracker.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.user " +
           "WHERE t.user.id = :userId " +
           "AND (LOWER(t.note) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.category.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Transaction> findByUserIdWithSearch(
        @Param("userId") Integer userId, 
        @Param("search") String search, 
        Pageable pageable
    );
    
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.user " +
           "WHERE t.user.id = :userId")
    Page<Transaction> findByUserId(Integer userId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.category " +
           "WHERE t.user.id = :userId " +
           "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateBetween(
        @Param("userId") Integer userId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndTypeAndDateBetween(
        @Param("userId") Integer userId, 
        @Param("type") Transaction.TransactionType type, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.type = :type " +
           "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndCategoryIdAndTypeAndDateBetween(
        @Param("userId") Integer userId,
        @Param("categoryId") Integer categoryId,
        @Param("type") Transaction.TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.user " +
           "WHERE t.user.id = :userId " +
           "AND t.date BETWEEN :startDate AND :endDate " +
           "ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(
        @Param("userId") Integer userId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.user " +
           "WHERE t.id = :id")
    java.util.Optional<Transaction> findByIdWithRelations(@Param("id") Integer id);
}

