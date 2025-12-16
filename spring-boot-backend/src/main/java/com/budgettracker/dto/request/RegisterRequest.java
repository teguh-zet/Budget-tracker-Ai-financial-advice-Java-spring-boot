package com.budgettracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Nama wajib diisi")
    @Size(max = 50, message = "Nama maksimal 50 karakter")
    private String name;
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Email tidak valid")
    @Size(max = 50, message = "Email maksimal 50 karakter")
    private String email;
    
    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;
    
    private String number;
}

