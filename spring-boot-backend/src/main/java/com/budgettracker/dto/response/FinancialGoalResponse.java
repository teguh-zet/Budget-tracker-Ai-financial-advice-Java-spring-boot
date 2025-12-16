package com.budgettracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoalResponse {
    private Integer id;
    
    @JsonProperty("user_id")
    private Integer userId;
    
    private String name;
    private String description;
    
    @JsonProperty("target_amount")
    private BigDecimal targetAmount;
    
    @JsonProperty("current_amount")
    private BigDecimal currentAmount;
    
    private LocalDate deadline;
    private String type;
    private String status;
    private String icon;
    
    @JsonProperty("progress_percentage")
    private Double progressPercentage; // Calculated field
    
    @JsonProperty("remaining_amount")
    private BigDecimal remainingAmount; // Calculated field
    
    @JsonProperty("days_remaining")
    private Long daysRemaining; // Calculated field
    
    @JsonProperty("is_completed")
    private Boolean isCompleted; // Calculated field
    
    @JsonProperty("is_overdue")
    private Boolean isOverdue; // Calculated field
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}


