package com.budgettracker.controller;

import com.budgettracker.dto.request.LoginRequest;
import com.budgettracker.dto.request.RegisterRequest;
import com.budgettracker.dto.response.ApiResponse;
import com.budgettracker.dto.response.AuthResponse;
import com.budgettracker.dto.response.UserResponse;
import com.budgettracker.service.AuthService;
import com.budgettracker.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API untuk autentikasi: register, login, dan profile")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(
            summary = "Register user baru",
            description = "Mendaftarkan user baru ke sistem. Setelah register berhasil, akan mendapatkan JWT token untuk autentikasi."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Register berhasil"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request tidak valid atau email sudah terdaftar")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Register berhasil", response));
    }
    
    @Operation(
            summary = "Login user",
            description = "Login dengan email dan password. Jika berhasil, akan mendapatkan JWT token yang digunakan untuk autentikasi di endpoint lainnya."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login berhasil"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Email atau password salah")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
    }
    
    @Operation(
            summary = "Get profile user",
            description = "Mendapatkan informasi profile user yang sedang login. Memerlukan JWT token."
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile berhasil diambil"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token tidak valid")
    })
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        Integer userId = SecurityUtil.getCurrentUserId();
        UserResponse response = authService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile berhasil di ambil", response));
    }
}

