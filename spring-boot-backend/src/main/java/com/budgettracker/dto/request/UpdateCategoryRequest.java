package com.budgettracker.dto.request;

import lombok.Data;

@Data
public class UpdateCategoryRequest {
    private String name;
    private String description;
}

