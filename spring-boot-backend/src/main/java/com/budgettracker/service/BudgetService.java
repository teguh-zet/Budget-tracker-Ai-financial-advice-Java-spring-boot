package com.budgettracker.service;

import com.budgettracker.dto.request.CreateBudgetRequest;
import com.budgettracker.dto.request.UpdateBudgetRequest;
import com.budgettracker.dto.response.BudgetResponse;

import java.util.List;

public interface BudgetService {
    List<BudgetResponse> getAll(Integer userId);
    BudgetResponse getById(Integer id);
    BudgetResponse create(Integer userId, CreateBudgetRequest request);
    BudgetResponse update(Integer userId, Integer id, UpdateBudgetRequest request);
    void delete(Integer id);
    List<BudgetResponse> getActiveBudgets(Integer userId);
}


