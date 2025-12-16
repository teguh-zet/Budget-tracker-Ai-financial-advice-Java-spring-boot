package com.budgettracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateBudgetRequest {
    
    @NotNull(message = "Category ID wajib diisi")
    private Integer categoryId;
    
    @NotNull(message = "Amount wajib diisi")
    @DecimalMin(value = "0.01", message = "Amount harus lebih besar dari 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Period wajib diisi (MONTHLY, WEEKLY, YEARLY)")
    private String period; // "MONTHLY", "WEEKLY", "YEARLY"
    
    @NotNull(message = "Period start wajib diisi")
    private LocalDate periodStart;
    
    private LocalDate periodEnd;
    
    private String description;
}


