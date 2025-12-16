package com.budgettracker.mapper;

import com.budgettracker.dto.response.CategoryResponse;
import com.budgettracker.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    
    public CategoryResponse toResponse(Category category) {
        if (category == null) return null;
        
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .type(category.getType() != null ? category.getType().name() : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

