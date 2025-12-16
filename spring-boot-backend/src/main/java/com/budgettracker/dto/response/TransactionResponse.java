package com.budgettracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Integer id;
    private String type; // "income" or "expense" untuk kompatibilitas dengan frontend
    private String amount;
    private LocalDate date;
    private String note;
    private Integer userId;
    private Integer categoryId;
    private CategoryResponse category;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

