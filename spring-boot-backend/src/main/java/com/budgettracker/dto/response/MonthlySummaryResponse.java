package com.budgettracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryResponse {
    private Integer id;
    
    @JsonProperty("user_id")
    private Integer userId;
    
    private String month;
    private String year;
    
    @JsonProperty("total_income")
    private String totalIncome;
    
    @JsonProperty("total_expense")
    private String totalExpense;
    
    private String balance;
    
    @JsonProperty("ai_summary")
    private String aiSummary;
    
    @JsonProperty("ai_recomendation")
    private String aiRecomendation;
    
    @JsonProperty("ai_trend_analysis")
    private String aiTrendAnalysis;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}

