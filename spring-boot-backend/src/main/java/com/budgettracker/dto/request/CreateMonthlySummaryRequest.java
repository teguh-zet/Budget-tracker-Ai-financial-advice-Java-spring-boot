package com.budgettracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMonthlySummaryRequest {
    
    @NotBlank(message = "Bulan wajib diisi")
    private String month;
    
    @NotBlank(message = "Tahun wajib diisi")
    private String year;
    
    @NotNull(message = "Total pemasukan wajib diisi")
    private String totalIncome;
    
    @NotNull(message = "Total pengeluaran wajib diisi")
    private String totalExpense;
    
    @NotNull(message = "Balance wajib diisi")
    private String balance;
    
    private String aiSummary;
    private String aiRecomendation;
}

