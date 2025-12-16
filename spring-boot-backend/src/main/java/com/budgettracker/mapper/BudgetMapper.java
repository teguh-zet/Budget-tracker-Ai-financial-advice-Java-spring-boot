package com.budgettracker.mapper;

import com.budgettracker.dto.response.BudgetResponse;
import com.budgettracker.dto.response.CategoryResponse;
import com.budgettracker.entity.Budget;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BudgetMapper {
    
    public BudgetResponse toResponse(Budget budget, BigDecimal spentAmount) {
        if (budget == null) return null;
        
        BigDecimal remaining = budget.getAmount().subtract(spentAmount);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }
        
        double usagePercentage = 0.0;
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            usagePercentage = spentAmount
                    .divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        
        return BudgetResponse.builder()
                .id(budget.getId())
                .userId(budget.getUser() != null ? budget.getUser().getId() : null)
                .categoryId(budget.getCategory() != null ? budget.getCategory().getId() : null)
                .category(budget.getCategory() != null ? 
                        CategoryResponse.builder()
                                .id(budget.getCategory().getId())
                                .name(budget.getCategory().getName())
                                .description(budget.getCategory().getDescription())
                                .type(budget.getCategory().getType() != null ? 
                                        budget.getCategory().getType().name() : null)
                                .build() : null)
                .amount(budget.getAmount())
                .period(budget.getPeriod())
                .periodStart(budget.getPeriodStart())
                .periodEnd(budget.getPeriodEnd())
                .description(budget.getDescription())
                .isActive(budget.getIsActive())
                .spentAmount(spentAmount)
                .remainingAmount(remaining)
                .usagePercentage(usagePercentage)
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}

