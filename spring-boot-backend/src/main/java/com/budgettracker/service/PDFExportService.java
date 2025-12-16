package com.budgettracker.service;

import com.budgettracker.entity.MonthlySummary;

public interface PDFExportService {
    byte[] generateSummaryPDF(MonthlySummary summary);
}


