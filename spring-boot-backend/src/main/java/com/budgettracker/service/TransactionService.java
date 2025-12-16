package com.budgettracker.service;

import com.budgettracker.dto.request.CreateTransactionRequest;
import com.budgettracker.dto.request.UpdateTransactionRequest;
import com.budgettracker.dto.response.*;

import java.util.List;

public interface TransactionService {
    PagedResponse<TransactionResponse> getAllByUser(Integer userId, Integer page, Integer limit, String search);
    TransactionResponse getById(Integer id);
    TransactionResponse create(Integer userId, CreateTransactionRequest request);
    TransactionResponse update(Integer userId, Integer id, UpdateTransactionRequest request);
    void delete(Integer id);
    MonthlyStatsResponse getMonthlySummary(Integer userId);
    List<ChartDataResponse> getMonthlyChart(Integer userId);
    List<TransactionResponse> getTodayTransactions(Integer userId);
    TodayExpenseStatsResponse getTodayExpenseStats(Integer userId);
}

