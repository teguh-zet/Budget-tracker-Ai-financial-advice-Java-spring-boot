package com.budgettracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    
    @NotBlank(message = "Nama kategori wajib diisi")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Tipe kategori wajib diisi (INCOME atau EXPENSE)")
    private String type; // "INCOME" or "EXPENSE"
}

