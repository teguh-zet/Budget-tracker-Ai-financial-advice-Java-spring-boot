package com.budgettracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateFinancialGoalRequest {
    
    @NotBlank(message = "Nama goal wajib diisi")
    private String name;
    
    private String description;
    
    @NotNull(message = "Target amount wajib diisi")
    @DecimalMin(value = "0.01", message = "Target amount harus lebih besar dari 0")
    private BigDecimal targetAmount;
    
    @NotNull(message = "Deadline wajib diisi")
    private LocalDate deadline;
    
    @NotBlank(message = "Tipe goal wajib diisi (SAVINGS, INVESTMENT, PURCHASE, DEBT_PAYOFF, OTHER)")
    private String type; // "SAVINGS", "INVESTMENT", "PURCHASE", "DEBT_PAYOFF", "OTHER"
    
    private String icon; // Optional emoji atau icon name
}


