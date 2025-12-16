package com.budgettracker.service.impl;

import com.budgettracker.dto.request.CreateUserRequest;
import com.budgettracker.dto.request.UpdateUserRequest;
import com.budgettracker.dto.response.UserResponse;
import com.budgettracker.entity.User;
import com.budgettracker.exception.BadRequestException;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.mapper.UserMapper;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.service.FileStorageService;
import com.budgettracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
    
    @Override
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new NotFoundException("Data Users Belum ada boy!");
        }
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserResponse getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Data User Belum ada boy!"));
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email Sudah Terdaftar");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .number(request.getNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse update(Integer id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Data User Tidak Ditemukan"));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email Sudah Di pake browww");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getNumber() != null) {
            user.setNumber(request.getNumber());
        }
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Data User Tidak Ditemukan"));
        userRepository.delete(user);
    }
    
    @Override
    @Transactional
    public UserResponse updateProfilePicture(Integer userId, String profilePicturePath) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Data User Tidak Ditemukan"));
        
        // Delete old profile picture if exists
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            String oldFilePath = fileStorageService.getProfilePictureDir() + "/" + user.getProfilePicture();
            fileStorageService.deleteFile(oldFilePath);
        }
        
        user.setProfilePicture(profilePicturePath);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    @Override
    public byte[] getProfilePicture(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new NotFoundException("Foto profil tidak ditemukan");
        }
        // Construct full path
        String fullPath = fileStorageService.getProfilePictureDir() + "/" + filePath;
        return fileStorageService.loadFileAsBytes(fullPath);
    }
    
    @Override
    public com.budgettracker.entity.User getEntityById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Data User Tidak Ditemukan"));
    }
}

