package com.budgettracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateBudgetRequest {
    
    private Integer categoryId;
    
    @DecimalMin(value = "0.01", message = "Amount harus lebih besar dari 0")
    private BigDecimal amount;
    
    private String period; // "MONTHLY", "WEEKLY", "YEARLY"
    
    private LocalDate periodStart;
    
    private LocalDate periodEnd;
    
    private String description;
    
    private Boolean isActive;
}


