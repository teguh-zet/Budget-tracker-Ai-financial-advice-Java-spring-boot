package com.budgettracker.dto.request;

import lombok.Data;

@Data
public class UpdateMonthlySummaryRequest {
    private String month;
    private String year;
    private String totalIncome;
    private String totalExpense;
    private String balance;
    private String aiSummary;
    private String aiRecomendation;
}

