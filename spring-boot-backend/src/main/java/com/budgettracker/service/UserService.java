package com.budgettracker.service;

import com.budgettracker.dto.request.CreateUserRequest;
import com.budgettracker.dto.request.UpdateUserRequest;
import com.budgettracker.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(Integer id);
    UserResponse create(CreateUserRequest request);
    UserResponse update(Integer id, UpdateUserRequest request);
    void delete(Integer id);
    UserResponse updateProfilePicture(Integer userId, String profilePicturePath);
    byte[] getProfilePicture(String filePath);
    com.budgettracker.entity.User getEntityById(Integer id);
}

