package com.budgettracker.controller;

import com.budgettracker.dto.request.CreateTransactionRequest;
import com.budgettracker.dto.request.UpdateTransactionRequest;
import com.budgettracker.dto.response.*;
import com.budgettracker.exception.ForbiddenException;
import com.budgettracker.service.TransactionService;
import com.budgettracker.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "API untuk mengelola transaksi (income/expense)")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Operation(
            summary = "Get semua transaksi",
            description = "Mendapatkan daftar transaksi user dengan pagination dan search. Memerlukan JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Berhasil mendapatkan daftar transaksi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @Parameter(description = "Nomor halaman (default: 1)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Jumlah data per halaman (default: 10)") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "Keyword untuk search (opsional)") @RequestParam(required = false) String search) {
        Integer userId = SecurityUtil.getCurrentUserId();
        PagedResponse<TransactionResponse> response = transactionService.getAllByUser(userId, page, limit, search);
        
        // Match Express.js response structure: {success, message, data, pagination}
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Daftar transaksi kamu");
        result.put("data", response.getData());
        result.put("pagination", response.getPagination());
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(@PathVariable Integer id) {
        Integer userId = SecurityUtil.getCurrentUserId();
        TransactionResponse transaction = transactionService.getById(id);
        
        if (!transaction.getUserId().equals(userId)) {
            throw new ForbiddenException("Kamu tidak bisa Akses transaksi ini");
        }
        
        return ResponseEntity.ok(ApiResponse.success("transaksi ditemukan", transaction));
    }
    
    @Operation(
            summary = "Buat transaksi baru",
            description = "Membuat transaksi baru (income atau expense). Memerlukan JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transaksi berhasil dibuat"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request tidak valid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Valid @RequestBody CreateTransactionRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        TransactionResponse transaction = transactionService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("transaksi sudah terbuat", transaction));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        transactionService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("transaksi sudah terupdate", null));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("transaksi sudah di hapus", null));
    }
    
    @GetMapping("/monthly-summary")
    public ResponseEntity<ApiResponse<MonthlyStatsResponse>> getMonthlySummary() {
        Integer userId = SecurityUtil.getCurrentUserId();
        MonthlyStatsResponse response = transactionService.getMonthlySummary(userId);
        return ResponseEntity.ok(ApiResponse.success("summary data berhasil di ambil", response));
    }
    
    @GetMapping("/monthly-chart")
    public ResponseEntity<ApiResponse<List<ChartDataResponse>>> getMonthlyChart() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<ChartDataResponse> response = transactionService.getMonthlyChart(userId);
        return ResponseEntity.ok(ApiResponse.success("chart data berhasil di ambil", response));
    }
    
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTodayTransactions() {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<TransactionResponse> response = transactionService.getTodayTransactions(userId);
        return ResponseEntity.ok(ApiResponse.success("data transaksi hari ini berhasil di ambil", response));
    }
    
    @GetMapping("/today-expense-stats")
    public ResponseEntity<ApiResponse<TodayExpenseStatsResponse>> getTodayExpenseStats() {
        Integer userId = SecurityUtil.getCurrentUserId();
        TodayExpenseStatsResponse response = transactionService.getTodayExpenseStats(userId);
        return ResponseEntity.ok(ApiResponse.success("data pengeluaran hari ini berhasil di ambil", response));
    }
}

