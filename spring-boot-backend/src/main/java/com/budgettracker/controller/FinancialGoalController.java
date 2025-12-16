package com.budgettracker.controller;

import com.budgettracker.dto.request.AddAmountToGoalRequest;
import com.budgettracker.dto.request.CreateFinancialGoalRequest;
import com.budgettracker.dto.request.UpdateFinancialGoalRequest;
import com.budgettracker.dto.response.ApiResponse;
import com.budgettracker.dto.response.FinancialGoalResponse;
import com.budgettracker.service.FinancialGoalService;
import com.budgettracker.util.SecurityUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financial-goals")
@RequiredArgsConstructor
@Tag(name = "Financial Goals", description = "API untuk mengelola financial goals (tabungan, investasi, pembelian, dll)")
@SecurityRequirement(name = "Bearer Authentication")
public class FinancialGoalController {
    
    private final FinancialGoalService financialGoalService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialGoalResponse>>> getAll() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<FinancialGoalResponse> goals = financialGoalService.getAll(userId);
        return ResponseEntity.ok(ApiResponse.success("daftar financial goals berhasil diambil", goals));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FinancialGoalResponse>>> getActiveGoals() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<FinancialGoalResponse> goals = financialGoalService.getActiveGoals(userId);
        return ResponseEntity.ok(ApiResponse.success("daftar financial goals aktif berhasil diambil", goals));
    }
    
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<FinancialGoalResponse>>> getCompletedGoals() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<FinancialGoalResponse> goals = financialGoalService.getCompletedGoals(userId);
        return ResponseEntity.ok(ApiResponse.success("daftar financial goals selesai berhasil diambil", goals));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialGoalResponse>> getById(@PathVariable Integer id) {
        FinancialGoalResponse goal = financialGoalService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("data financial goal berhasil diambil", goal));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<FinancialGoalResponse>> create(
            @Valid @RequestBody CreateFinancialGoalRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        FinancialGoalResponse goal = financialGoalService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("financial goal berhasil dibuat", goal));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialGoalResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateFinancialGoalRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        FinancialGoalResponse goal = financialGoalService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("financial goal berhasil diupdate", goal));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        financialGoalService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("financial goal berhasil dihapus", null));
    }
    
    @PostMapping("/{id}/add-amount")
    public ResponseEntity<ApiResponse<FinancialGoalResponse>> addAmount(
            @PathVariable Integer id,
            @Valid @RequestBody AddAmountToGoalRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        FinancialGoalResponse goal = financialGoalService.addAmount(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("amount berhasil ditambahkan ke goal", goal));
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<FinancialGoalResponse>> completeGoal(@PathVariable Integer id) {
        Integer userId = SecurityUtil.getCurrentUserId();
        FinancialGoalResponse goal = financialGoalService.completeGoal(userId, id);
        return ResponseEntity.ok(ApiResponse.success("financial goal berhasil diselesaikan", goal));
    }
}


