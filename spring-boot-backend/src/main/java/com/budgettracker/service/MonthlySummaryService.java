package com.budgettracker.service;

import com.budgettracker.dto.request.CreateMonthlySummaryRequest;
import com.budgettracker.dto.request.UpdateMonthlySummaryRequest;
import com.budgettracker.dto.response.AIGenerateResponse;
import com.budgettracker.dto.response.MonthlySummaryResponse;

import java.util.List;

public interface MonthlySummaryService {
    List<MonthlySummaryResponse> getAll();
    MonthlySummaryResponse getById(Integer id);
    com.budgettracker.entity.MonthlySummary getEntityById(Integer id); // For PDF export
    MonthlySummaryResponse create(Integer userId, CreateMonthlySummaryRequest request);
    MonthlySummaryResponse update(Integer userId, Integer id, UpdateMonthlySummaryRequest request);
    void delete(Integer id);
    AIGenerateResponse generate(Integer userId);
}

