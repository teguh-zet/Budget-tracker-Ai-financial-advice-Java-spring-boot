package com.budgettracker.controller;

import com.budgettracker.dto.request.CreateMonthlySummaryRequest;
import com.budgettracker.dto.request.UpdateMonthlySummaryRequest;
import com.budgettracker.dto.response.AIGenerateResponse;
import com.budgettracker.dto.response.ApiResponse;
import com.budgettracker.dto.response.MonthlySummaryResponse;
import com.budgettracker.service.MonthlySummaryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/monthly-summary")
@RequiredArgsConstructor
@Tag(name = "Monthly Summary", description = "API untuk mengelola monthly summary dengan AI-generated analysis")
@SecurityRequirement(name = "Bearer Authentication")
public class MonthlySummaryController {
    
    private final MonthlySummaryService monthlySummaryService;
    private final com.budgettracker.service.PDFExportService pdfExportService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<MonthlySummaryResponse>>> getAll() {
        List<MonthlySummaryResponse> summaries = monthlySummaryService.getAll();
        return ResponseEntity.ok(ApiResponse.success("list daftar summary bulanan", summaries));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> getById(@PathVariable Integer id) {
        MonthlySummaryResponse summary = monthlySummaryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("data summary bulanan", summary));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> create(
            @Valid @RequestBody CreateMonthlySummaryRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        MonthlySummaryResponse summary = monthlySummaryService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("membuat summary bulanan", summary));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMonthlySummaryRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        MonthlySummaryResponse summary = monthlySummaryService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("update summary bulanan", summary));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        monthlySummaryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("delete summary bulanan", null));
    }
    
    @Operation(
            summary = "Generate monthly summary dengan AI",
            description = "Generate summary bulanan dengan analisis AI. Maksimal 2x per hari. Memerlukan JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Summary berhasil di-generate"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Sudah mencapai batas maksimal generate (2x per hari) atau error lainnya"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Rate limit tercapai")
    })
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<AIGenerateResponse>> generate() {
        Integer userId = SecurityUtil.getCurrentUserId();
        AIGenerateResponse response = monthlySummaryService.generate(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Summary Bulanan Berhasil Dibuat", response));
    }
    
    @Operation(
            summary = "Export summary ke PDF",
            description = "Download monthly summary dalam format PDF. Memerlukan JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF berhasil di-generate dan di-download"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Tidak memiliki akses ke summary ini")
    })
    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<org.springframework.core.io.Resource> exportPDF(
            @Parameter(description = "ID dari monthly summary", required = true)
            @PathVariable Integer id) {
        try {
            Integer userId = SecurityUtil.getCurrentUserId();
            com.budgettracker.entity.MonthlySummary summary = monthlySummaryService.getEntityById(id);
            
            // Check ownership
            if (!summary.getUser().getId().equals(userId)) {
                throw new com.budgettracker.exception.ForbiddenException("Anda tidak memiliki akses ke summary ini");
            }
            
            byte[] pdfBytes = pdfExportService.generateSummaryPDF(summary);
            org.springframework.core.io.ByteArrayResource resource = 
                    new org.springframework.core.io.ByteArrayResource(pdfBytes);
            
            String filename = String.format("Financial_Summary_%s_%s.pdf", 
                    summary.getMonth(), summary.getYear());
            
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + filename + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (com.budgettracker.exception.ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            throw new com.budgettracker.exception.BadRequestException("Gagal membuat PDF: " + e.getMessage());
        }
    }
}

