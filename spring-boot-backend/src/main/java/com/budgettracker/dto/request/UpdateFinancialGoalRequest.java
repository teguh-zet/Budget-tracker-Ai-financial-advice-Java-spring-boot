package com.budgettracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateFinancialGoalRequest {
    
    private String name;
    
    private String description;
    
    @DecimalMin(value = "0.01", message = "Target amount harus lebih besar dari 0")
    private BigDecimal targetAmount;
    
    private LocalDate deadline;
    
    private String type; // "SAVINGS", "INVESTMENT", "PURCHASE", "DEBT_PAYOFF", "OTHER"
    
    private String status; // "ACTIVE", "COMPLETED", "PAUSED", "CANCELLED"
    
    private String icon;
    
    private BigDecimal currentAmount; // Untuk manual update
}


