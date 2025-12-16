package com.budgettracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private UUID uuid;
    private String name;
    private String email;
    private String number;
    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

