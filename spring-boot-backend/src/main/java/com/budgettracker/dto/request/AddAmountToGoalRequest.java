package com.budgettracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddAmountToGoalRequest {
    
    @NotNull(message = "Amount wajib diisi")
    @DecimalMin(value = "0.01", message = "Amount harus lebih besar dari 0")
    private BigDecimal amount;
    
    private String note; // Optional note untuk tracking
}


