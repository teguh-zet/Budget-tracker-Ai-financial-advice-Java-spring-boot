package com.budgettracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Email tidak valid")
    private String email;
    
    @NotBlank(message = "Password wajib diisi")
    private String password;
}

