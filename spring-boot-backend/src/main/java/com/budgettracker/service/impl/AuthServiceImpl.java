package com.budgettracker.service.impl;

import com.budgettracker.dto.request.LoginRequest;
import com.budgettracker.dto.request.RegisterRequest;
import com.budgettracker.dto.response.AuthResponse;
import com.budgettracker.dto.response.UserResponse;
import com.budgettracker.entity.User;
import com.budgettracker.exception.BadRequestException;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.mapper.UserMapper;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.service.AuthService;
import com.budgettracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email User Sudah Terdaftar");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .number(request.getNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        UserResponse userResponse = userMapper.toResponse(user);
        
        return AuthResponse.builder()
                .user(userResponse)
                .token(token)
                .build();
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Email Tidak Ditemukan"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Password nya salah boy");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        UserResponse userResponse = userMapper.toResponse(user);
        
        return AuthResponse.builder()
                .user(userResponse)
                .token(token)
                .build();
    }
    
    @Override
    public UserResponse getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));
        return userMapper.toResponse(user);
    }
}

