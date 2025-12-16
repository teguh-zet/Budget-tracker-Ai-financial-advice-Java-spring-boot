package com.budgettracker.controller;

import com.budgettracker.dto.request.CreateBudgetRequest;
import com.budgettracker.dto.request.UpdateBudgetRequest;
import com.budgettracker.dto.response.ApiResponse;
import com.budgettracker.dto.response.BudgetResponse;
import com.budgettracker.service.BudgetService;
import com.budgettracker.util.SecurityUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budget")
@RequiredArgsConstructor
@Tag(name = "Budget", description = "API untuk mengelola budget per kategori")
@SecurityRequirement(name = "Bearer Authentication")
public class BudgetController {
    
    private final BudgetService budgetService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAll() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<BudgetResponse> budgets = budgetService.getAll(userId);
        return ResponseEntity.ok(ApiResponse.success("daftar budget berhasil diambil", budgets));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getActiveBudgets() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<BudgetResponse> budgets = budgetService.getActiveBudgets(userId);
        return ResponseEntity.ok(ApiResponse.success("daftar budget aktif berhasil diambil", budgets));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getById(@PathVariable Integer id) {
        BudgetResponse budget = budgetService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("data budget berhasil diambil", budget));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> create(
            @Valid @RequestBody CreateBudgetRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        BudgetResponse budget = budgetService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.success("budget berhasil dibuat", budget));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        BudgetResponse budget = budgetService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("budget berhasil diupdate", budget));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        budgetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("budget berhasil dihapus", null));
    }
}


