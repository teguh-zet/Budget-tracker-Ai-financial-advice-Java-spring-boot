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
public class BudgetResponse {
    private Integer id;
    
    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("category_id")
    private Integer categoryId;
    
    private CategoryResponse category;
    
    private BigDecimal amount;
    private String period;
    
    @JsonProperty("period_start")
    private LocalDate periodStart;
    
    @JsonProperty("period_end")
    private LocalDate periodEnd;
    
    private String description;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    @JsonProperty("spent_amount")
    private BigDecimal spentAmount; // Calculated field
    
    @JsonProperty("remaining_amount")
    private BigDecimal remainingAmount; // Calculated field
    
    @JsonProperty("usage_percentage")
    private Double usagePercentage; // Calculated field
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}


