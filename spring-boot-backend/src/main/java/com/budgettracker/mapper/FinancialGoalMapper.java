package com.budgettracker.mapper;

import com.budgettracker.dto.response.FinancialGoalResponse;
import com.budgettracker.entity.FinancialGoal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class FinancialGoalMapper {
    
    public FinancialGoalResponse toResponse(FinancialGoal goal) {
        if (goal == null) return null;
        
        // Calculate progress percentage
        double progressPercentage = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        
        // Calculate remaining amount
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(goal.getCurrentAmount());
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }
        
        // Calculate days remaining
        LocalDate today = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(today, goal.getDeadline());
        
        // Check if completed
        boolean isCompleted = goal.getStatus() == FinancialGoal.GoalStatus.COMPLETED ||
                goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0;
        
        // Check if overdue
        boolean isOverdue = daysRemaining < 0 && !isCompleted;
        
        return FinancialGoalResponse.builder()
                .id(goal.getId())
                .userId(goal.getUser() != null ? goal.getUser().getId() : null)
                .name(goal.getName())
                .description(goal.getDescription())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .deadline(goal.getDeadline())
                .type(goal.getType() != null ? goal.getType().name() : null)
                .status(goal.getStatus() != null ? goal.getStatus().name() : null)
                .icon(goal.getIcon())
                .progressPercentage(progressPercentage)
                .remainingAmount(remainingAmount)
                .daysRemaining(daysRemaining)
                .isCompleted(isCompleted)
                .isOverdue(isOverdue)
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}


