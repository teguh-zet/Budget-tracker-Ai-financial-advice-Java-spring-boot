package com.budgettracker.mapper;

import com.budgettracker.dto.response.MonthlySummaryResponse;
import com.budgettracker.entity.MonthlySummary;
import org.springframework.stereotype.Component;

@Component
public class MonthlySummaryMapper {
    
    public MonthlySummaryResponse toResponse(MonthlySummary summary) {
        if (summary == null) return null;
        
        return MonthlySummaryResponse.builder()
                .id(summary.getId())
                .userId(summary.getUser() != null ? summary.getUser().getId() : null)
                .month(summary.getMonth())
                .year(summary.getYear())
                .totalIncome(summary.getTotalIncome())
                .totalExpense(summary.getTotalExpense())
                .balance(summary.getBalance())
                .aiSummary(summary.getAiSummary())
                .aiRecomendation(summary.getAiRecomendation())
                .aiTrendAnalysis(summary.getAiTrendAnalysis())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .build();
    }
}

