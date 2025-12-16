package com.budgettracker.service;

import com.budgettracker.dto.request.CreateCategoryRequest;
import com.budgettracker.dto.request.UpdateCategoryRequest;
import com.budgettracker.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();
    List<CategoryResponse> getAllByType(String type); // "income" or "expense"
    CategoryResponse getById(Integer id);
    CategoryResponse create(CreateCategoryRequest request);
    CategoryResponse update(Integer id, UpdateCategoryRequest request);
    void delete(Integer id);
}

