package com.budgettracker.service.impl;

import com.budgettracker.dto.request.CreateCategoryRequest;
import com.budgettracker.dto.request.UpdateCategoryRequest;
import com.budgettracker.dto.response.CategoryResponse;
import com.budgettracker.entity.Category;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.mapper.CategoryMapper;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();
        // Return empty list instead of throwing exception
        // Frontend can handle empty list gracefully
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CategoryResponse> getAllByType(String type) {
        Category.CategoryType categoryType;
        try {
            // Convert "income" or "expense" to enum
            if (type != null && type.equalsIgnoreCase("income")) {
                categoryType = Category.CategoryType.INCOME;
            } else if (type != null && type.equalsIgnoreCase("expense")) {
                categoryType = Category.CategoryType.EXPENSE;
            } else {
                // If type is invalid, return all
                return getAll();
            }
        } catch (Exception e) {
            // If conversion fails, return all
            return getAll();
        }
        
        List<Category> categories = categoryRepository.findByType(categoryType);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CategoryResponse getById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kategori Tidak di temukan!"));
        return categoryMapper.toResponse(category);
    }
    
    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        Category.CategoryType categoryType;
        try {
            if (request.getType() != null && request.getType().equalsIgnoreCase("INCOME")) {
                categoryType = Category.CategoryType.INCOME;
            } else if (request.getType() != null && request.getType().equalsIgnoreCase("EXPENSE")) {
                categoryType = Category.CategoryType.EXPENSE;
            } else {
                throw new IllegalArgumentException("Tipe kategori harus INCOME atau EXPENSE");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipe kategori tidak valid: " + request.getType());
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(categoryType)
                .build();
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Override
    @Transactional
    public CategoryResponse update(Integer id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kategori Tidak di temukan!"));
        
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kategori Tidak di temukan!"));
        categoryRepository.delete(category);
    }
}

