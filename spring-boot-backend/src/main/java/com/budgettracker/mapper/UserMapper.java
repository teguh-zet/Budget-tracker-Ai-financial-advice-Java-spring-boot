package com.budgettracker.mapper;

import com.budgettracker.dto.response.UserResponse;
import com.budgettracker.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    @Value("${file.profile-picture-url-prefix:/api/v1/users/profile/picture}")
    private String profilePictureUrlPrefix;
    
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        
        // Construct full URL for profile picture
        String profilePictureUrl = null;
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            profilePictureUrl = profilePictureUrlPrefix + "?userId=" + user.getId();
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .name(user.getName())
                .email(user.getEmail())
                .number(user.getNumber())
                .profilePicture(profilePictureUrl)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

