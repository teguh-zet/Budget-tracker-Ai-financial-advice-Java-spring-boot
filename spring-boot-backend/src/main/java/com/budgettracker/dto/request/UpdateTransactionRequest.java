package com.budgettracker.dto.request;

import com.budgettracker.entity.Transaction;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTransactionRequest {
    private String type; // "income" or "expense"
    private String amount;
    private LocalDate date;
    private String note;
    private Integer categoryId;
    
    public Transaction.TransactionType getTransactionType() {
        if (type == null) return null;
        return "income".equalsIgnoreCase(type) 
            ? Transaction.TransactionType.INCOME 
            : Transaction.TransactionType.EXPENSE;
    }
}

