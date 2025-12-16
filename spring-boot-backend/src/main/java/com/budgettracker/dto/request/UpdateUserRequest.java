package com.budgettracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    
    @Size(max = 50, message = "Nama maksimal 50 karakter")
    private String name;
    
    @Email(message = "Format email tidak valid")
    @Size(max = 50, message = "Email maksimal 50 karakter")
    private String email;
    
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;
    
    private String number;
}

