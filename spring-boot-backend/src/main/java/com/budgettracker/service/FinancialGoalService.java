package com.budgettracker.service;

import com.budgettracker.dto.request.AddAmountToGoalRequest;
import com.budgettracker.dto.request.CreateFinancialGoalRequest;
import com.budgettracker.dto.request.UpdateFinancialGoalRequest;
import com.budgettracker.dto.response.FinancialGoalResponse;

import java.math.BigDecimal;
import java.util.List;

public interface FinancialGoalService {
    List<FinancialGoalResponse> getAll(Integer userId);
    List<FinancialGoalResponse> getActiveGoals(Integer userId);
    List<FinancialGoalResponse> getCompletedGoals(Integer userId);
    FinancialGoalResponse getById(Integer id);
    FinancialGoalResponse create(Integer userId, CreateFinancialGoalRequest request);
    FinancialGoalResponse update(Integer userId, Integer id, UpdateFinancialGoalRequest request);
    void delete(Integer id);
    FinancialGoalResponse addAmount(Integer userId, Integer id, AddAmountToGoalRequest request);
    FinancialGoalResponse completeGoal(Integer userId, Integer id);
    void autoUpdateFromIncome(Integer userId, BigDecimal incomeAmount);
}

