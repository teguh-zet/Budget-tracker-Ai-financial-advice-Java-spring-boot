package com.budgettracker.controller;

import com.budgettracker.dto.request.CreateCategoryRequest;
import com.budgettracker.dto.request.UpdateCategoryRequest;
import com.budgettracker.dto.response.ApiResponse;
import com.budgettracker.dto.response.CategoryResponse;
import com.budgettracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "API untuk mengelola kategori transaksi (income/expense)")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Operation(
            summary = "Get semua kategori",
            description = "Mendapatkan daftar kategori. Bisa filter berdasarkan type (income/expense). Memerlukan JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Berhasil mendapatkan daftar kategori"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(
            @Parameter(description = "Filter berdasarkan type: 'income' atau 'expense' (opsional)")
            @RequestParam(required = false) String type) {
        List<CategoryResponse> categories;
        if (type != null && !type.isEmpty()) {
            categories = categoryService.getAllByType(type);
        } else {
            categories = categoryService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.success("kategori berhasil di dapat", categories));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Integer id) {
        CategoryResponse category = categoryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("kategori berhasil di dapat", category));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse category = categoryService.create(request);
        return ResponseEntity.ok(ApiResponse.success("kategori berhasil di buat", category));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse category = categoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("kategori berhasil di update", category));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("kategori berhasil di hapus", null));
    }
}

