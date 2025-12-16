package com.budgettracker.dto.request;

import com.budgettracker.entity.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTransactionRequest {
    
    @NotBlank(message = "Tipe transaksi wajib diisi")
    private String type; // "income" or "expense"
    
    @NotNull(message = "Jumlah harus diisi")
    private String amount;
    
    @NotNull(message = "Tanggal wajib diisi")
    private LocalDate date;
    
    private String note;
    
    @NotNull(message = "Category ID wajib diisi")
    private Integer categoryId;
    
    public Transaction.TransactionType getTransactionType() {
        return "income".equalsIgnoreCase(type) 
            ? Transaction.TransactionType.INCOME 
            : Transaction.TransactionType.EXPENSE;
    }
}

