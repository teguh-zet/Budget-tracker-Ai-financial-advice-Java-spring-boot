package com.budgettracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_summaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MonthlySummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "month", length = 25, nullable = false)
    private String month;
    
    @Column(name = "year", length = 4, nullable = false)
    private String year;
    
    @Column(name = "total_income", nullable = false)
    private String totalIncome;
    
    @Column(name = "total_expense", nullable = false)
    private String totalExpense;
    
    @Column(name = "balance", nullable = false)
    private String balance;
    
    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;
    
    @Column(name = "ai_recomendation", columnDefinition = "TEXT")
    private String aiRecomendation;
    
    @Column(name = "ai_trend_analysis", columnDefinition = "TEXT")
    private String aiTrendAnalysis;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

