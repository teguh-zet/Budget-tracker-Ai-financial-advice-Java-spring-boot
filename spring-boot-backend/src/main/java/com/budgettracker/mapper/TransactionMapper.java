package com.budgettracker.mapper;

import com.budgettracker.dto.response.CategoryResponse;
import com.budgettracker.dto.response.TransactionResponse;
import com.budgettracker.dto.response.UserResponse;
import com.budgettracker.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) return null;
        
        CategoryResponse categoryResponse = transaction.getCategory() != null 
            ? categoryMapper.toResponse(transaction.getCategory()) 
            : null;
        
        UserResponse userResponse = transaction.getUser() != null 
            ? userMapper.toResponse(transaction.getUser()) 
            : null;
        
        // Convert enum to string for frontend compatibility
        String typeString = transaction.getType() != null 
            ? transaction.getType().name().toLowerCase() 
            : null;
        
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(typeString)
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .note(transaction.getNote())
                .userId(transaction.getUser() != null ? transaction.getUser().getId() : null)
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .category(categoryResponse)
                .user(userResponse)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}

