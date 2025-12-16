package com.budgettracker.service;

import com.budgettracker.dto.request.LoginRequest;
import com.budgettracker.dto.request.RegisterRequest;
import com.budgettracker.dto.response.AuthResponse;
import com.budgettracker.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getProfile(Integer userId);
}

