package com.budgettracker.service.impl;

import com.budgettracker.entity.MonthlySummary;
import com.budgettracker.service.PDFExportService;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@Slf4j
public class PDFExportServiceImpl implements PDFExportService {
    
    @Override
    public byte[] generateSummaryPDF(MonthlySummary summary) {
        try {
            String htmlContent = generateHTMLContent(summary);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal membuat PDF: " + e.getMessage());
        }
    }
    
    private String generateHTMLContent(MonthlySummary summary) {
        String totalIncome = formatCurrency(summary.getTotalIncome());
        String totalExpense = formatCurrency(summary.getTotalExpense());
        String balance = formatCurrency(summary.getBalance());
        
        String recommendations = "";
        if (summary.getAiRecomendation() != null && !summary.getAiRecomendation().isEmpty()) {
            String[] recs = summary.getAiRecomendation().split("\n");
            StringBuilder recBuilder = new StringBuilder();
            for (String rec : recs) {
                if (!rec.trim().isEmpty()) {
                    recBuilder.append("<li>").append(rec.trim()).append("</li>");
                }
            }
            recommendations = recBuilder.toString();
        }
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 40px;
                        color: #333;
                        line-height: 1.6;
                    }
                    .header {
                        text-align: center;
                        border-bottom: 3px solid #4f46e5;
                        padding-bottom: 20px;
                        margin-bottom: 30px;
                    }
                    .header h1 {
                        color: #4f46e5;
                        margin: 0;
                        font-size: 28px;
                    }
                    .header p {
                        color: #666;
                        margin: 5px 0;
                    }
                    .section {
                        margin-bottom: 30px;
                        padding: 20px;
                        background-color: #f9fafb;
                        border-radius: 8px;
                        border-left: 4px solid #4f46e5;
                    }
                    .section h2 {
                        color: #4f46e5;
                        margin-top: 0;
                        font-size: 20px;
                        border-bottom: 2px solid #e5e7eb;
                        padding-bottom: 10px;
                    }
                    .stats {
                        display: flex;
                        justify-content: space-around;
                        margin: 20px 0;
                        flex-wrap: wrap;
                    }
                    .stat-item {
                        text-align: center;
                        padding: 15px;
                        background: white;
                        border-radius: 8px;
                        min-width: 150px;
                        margin: 10px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .stat-label {
                        font-size: 12px;
                        color: #666;
                        margin-bottom: 5px;
                    }
                    .stat-value {
                        font-size: 18px;
                        font-weight: bold;
                        color: #4f46e5;
                    }
                    .content {
                        color: #333;
                        line-height: 1.8;
                        text-align: justify;
                    }
                    .recommendations ul {
                        padding-left: 20px;
                    }
                    .recommendations li {
                        margin-bottom: 10px;
                        line-height: 1.6;
                    }
                    .footer {
                        margin-top: 40px;
                        text-align: center;
                        color: #999;
                        font-size: 12px;
                        border-top: 1px solid #e5e7eb;
                        padding-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>📊 AI Financial Summary</h1>
                    <p>%s %s</p>
                    <p>Dibuat pada: %s</p>
                </div>
                
                <div class="section">
                    <h2>💰 Statistik Keuangan</h2>
                    <div class="stats">
                        <div class="stat-item">
                            <div class="stat-label">Total Pemasukan</div>
                            <div class="stat-value">%s</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-label">Total Pengeluaran</div>
                            <div class="stat-value">%s</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-label">Saldo</div>
                            <div class="stat-value">%s</div>
                        </div>
                    </div>
                </div>
                
                <div class="section">
                    <h2>📄 Ringkasan</h2>
                    <div class="content">
                        %s
                    </div>
                </div>
                
                <div class="section">
                    <h2>💡 Rekomendasi</h2>
                    <div class="content recommendations">
                        <ul>
                            %s
                        </ul>
                    </div>
                </div>
                
                <div class="section">
                    <h2>📈 Analisis Tren</h2>
                    <div class="content">
                        %s
                    </div>
                </div>
                
                <div class="footer">
                    <p>Dokumen ini dibuat secara otomatis oleh Budget Tracker Application</p>
                    <p>Powered by AI Financial Advisor</p>
                </div>
            </body>
            </html>
            """.formatted(
                summary.getMonth(),
                summary.getYear(),
                summary.getCreatedAt() != null ? summary.getCreatedAt().toString() : "",
                totalIncome,
                totalExpense,
                balance,
                summary.getAiSummary() != null ? summary.getAiSummary().replace("\n", "<br>") : "Tidak tersedia",
                recommendations.isEmpty() ? "<li>Tidak ada rekomendasi</li>" : recommendations,
                summary.getAiTrendAnalysis() != null ? summary.getAiTrendAnalysis().replace("\n", "<br>") : "Tidak tersedia"
            );
    }
    
    private String formatCurrency(String amount) {
        try {
            BigDecimal value = new BigDecimal(amount);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
            return format.format(value);
        } catch (Exception e) {
            return "Rp " + amount;
        }
    }
}

