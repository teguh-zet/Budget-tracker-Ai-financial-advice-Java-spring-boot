package com.budgettracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FinancialGoal {
    
    public enum GoalType {
        SAVINGS,      // Tabungan
        INVESTMENT,   // Investasi
        PURCHASE,     // Pembelian
        DEBT_PAYOFF,  // Pelunasan hutang
        OTHER         // Lainnya
    }
    
    public enum GoalStatus {
        ACTIVE,       // Aktif
        COMPLETED,    // Tercapai
        PAUSED,       // Dijeda
        CANCELLED     // Dibatalkan
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;
    
    @Column(name = "current_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private GoalType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVE;
    
    @Column(name = "icon", length = 50)
    private String icon; // Emoji atau icon name untuk UI
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}


