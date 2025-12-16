package com.budgettracker.service.impl;

import com.budgettracker.dto.request.CreateMonthlySummaryRequest;
import com.budgettracker.dto.request.UpdateMonthlySummaryRequest;
import com.budgettracker.dto.response.AIGenerateResponse;
import com.budgettracker.dto.response.MonthlySummaryResponse;
import com.budgettracker.entity.MonthlySummary;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.User;
import com.budgettracker.exception.BadRequestException;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.exception.RateLimitException;
import com.budgettracker.mapper.MonthlySummaryMapper;
import com.budgettracker.repository.MonthlySummaryRepository;
import com.budgettracker.repository.TransactionRepository;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.service.MonthlySummaryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlySummaryServiceImpl implements MonthlySummaryService {
    
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MonthlySummaryMapper monthlySummaryMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${openrouter.api-key}")
    private String openRouterApiKey;
    
    @Value("${openrouter.api-url}")
    private String openRouterApiUrl;
    
    @Value("${openrouter.model}")
    private String openRouterModel;
    
    @Override
    public List<MonthlySummaryResponse> getAll() {
        return monthlySummaryRepository.findAll().stream()
                .map(monthlySummaryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public MonthlySummaryResponse getById(Integer id) {
        MonthlySummary summary = monthlySummaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Summary Bulanan Tidak ditemukan!"));
        return monthlySummaryMapper.toResponse(summary);
    }
    
    @Override
    public MonthlySummary getEntityById(Integer id) {
        return monthlySummaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Summary Bulanan Tidak ditemukan!"));
    }
    
    @Override
    @Transactional
    public MonthlySummaryResponse create(Integer userId, CreateMonthlySummaryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));
        
        MonthlySummary summary = MonthlySummary.builder()
                .user(user)
                .month(request.getMonth())
                .year(request.getYear())
                .totalIncome(request.getTotalIncome())
                .totalExpense(request.getTotalExpense())
                .balance(request.getBalance())
                .aiSummary(request.getAiSummary())
                .aiRecomendation(request.getAiRecomendation())
                .build();
        
        summary = monthlySummaryRepository.save(summary);
        return monthlySummaryMapper.toResponse(summary);
    }
    
    @Override
    @Transactional
    public MonthlySummaryResponse update(Integer userId, Integer id, UpdateMonthlySummaryRequest request) {
        MonthlySummary summary = monthlySummaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Summary Bulanan Tidak ditemukan!"));
        
        if (!summary.getUser().getId().equals(userId)) {
            throw new NotFoundException("Summary Bulanan Tidak ditemukan!");
        }
        
        if (request.getMonth() != null) summary.setMonth(request.getMonth());
        if (request.getYear() != null) summary.setYear(request.getYear());
        if (request.getTotalIncome() != null) summary.setTotalIncome(request.getTotalIncome());
        if (request.getTotalExpense() != null) summary.setTotalExpense(request.getTotalExpense());
        if (request.getBalance() != null) summary.setBalance(request.getBalance());
        if (request.getAiSummary() != null) summary.setAiSummary(request.getAiSummary());
        if (request.getAiRecomendation() != null) summary.setAiRecomendation(request.getAiRecomendation());
        
        summary = monthlySummaryRepository.save(summary);
        return monthlySummaryMapper.toResponse(summary);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        MonthlySummary summary = monthlySummaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Summary Bulanan Tidak ditemukan!"));
        monthlySummaryRepository.delete(summary);
    }
    
    @Override
    @Transactional
    public AIGenerateResponse generate(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Pengguna Tidak Ditemukan!"));
        
        LocalDate now = LocalDate.now();
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = now.atTime(23, 59, 59);
        
        // Check how many times summary has been generated today (max 2x per day)
        long todayCount = monthlySummaryRepository.countByUserIdAndCreatedAtBetween(
                userId, startOfDay, endOfDay);
        
        if (todayCount >= 3) {
            throw new BadRequestException(
                    "Anda sudah mencapai batas maksimal generate summary hari ini (2x). " +
                    "Silakan coba lagi besok atau hapus salah satu summary yang sudah ada.");
        }
        
        if (todayCount == 1) {
            log.info("User {} generating second summary today (1/2)", userId);
        } else {
            log.info("User {} generating first summary today (0/2)", userId);
        }
        
        // Get transactions for current month
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userId, startOfMonth, endOfMonth);
        
        java.util.concurrent.atomic.AtomicInteger totalIncome = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger totalExpense = new java.util.concurrent.atomic.AtomicInteger(0);
        
        List<Object> formattedTx = transactions.stream().map(tx -> {
            int amount = Integer.parseInt(tx.getAmount());
            if (tx.getType() == Transaction.TransactionType.INCOME) {
                totalIncome.addAndGet(amount);
            } else {
                totalExpense.addAndGet(amount);
            }
            
            return java.util.Map.of(
                    "type", tx.getType() == Transaction.TransactionType.INCOME ? "pemasukan" : "pengeluaran",
                    "category", tx.getCategory() != null ? tx.getCategory().getName() : "Lainnya",
                    "amount", amount,
                    "date", tx.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
            );
        }).collect(Collectors.toList());
        
        String month = now.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("id-ID")));
        
        // Prepare payload for OpenRouter
        java.util.Map<String, Object> payload = java.util.Map.of(
                "user", user.getName(),
                "month", month,
                "transactions", formattedTx,
                "total_income", totalIncome.get(),
                "total_expense", totalExpense.get()
        );
        
        // Build messages with stronger emphasis on trend_analysis
        java.util.Map<String, Object> systemMessage = java.util.Map.of(
                "role", "system",
                "content", "Posisikan Dirimu sebagai Ahli Keuangan Profesional. " +
                        "Tugas kamu adalah menganalisis data keuangan bulanan dan membuat ringkasan yang komprehensif, detail, dan informatif. " +
                        "\n\n" +
                        "INSTRUKSI PENTING:\n" +
                        "1. SUMMARY: Buat ringkasan keuangan yang DETAIL (minimal 2-3 paragraf, 150-250 kata). " +
                        "   - Analisis kondisi keuangan secara menyeluruh\n" +
                        "   - Bandingkan pemasukan vs pengeluaran\n" +
                        "   - Identifikasi pola dan tren yang terlihat\n" +
                        "   - Berikan konteks dan insight yang bermanfaat\n" +
                        "\n" +
                        "2. RECOMMENDATIONS: Berikan 5-7 rekomendasi yang SPESIFIK dan DAPAT DILAKUKAN (actionable). " +
                        "   - Setiap rekomendasi harus jelas, spesifik, dan praktis\n" +
                        "   - Fokus pada peningkatan kondisi keuangan\n" +
                        "   - Berikan saran yang relevan dengan data yang ada\n" +
                        "\n" +
                        "3. TREND_ANALYSIS (WAJIB - JANGAN DIHILANGKAN): Buat analisis tren yang MENDALAM (minimal 1-2 paragraf, 100-150 kata). " +
                        "   - Analisis pola pengeluaran dan pemasukan secara detail\n" +
                        "   - Identifikasi kategori yang dominan dan alasan dominasinya\n" +
                        "   - Prediksi tren ke depan berdasarkan data historis\n" +
                        "   - Berikan insight tentang kebiasaan keuangan dan pola yang terlihat\n" +
                        "   - Analisis apakah ada perubahan signifikan dari bulan sebelumnya (jika ada data)\n" +
                        "   - Berikan kesimpulan tentang kesehatan keuangan jangka panjang\n" +
                        "\n" +
                        "FORMAT OUTPUT (WAJIB - HARUS DIIKUTI):\n" +
                        "Hasilkan HANYA JSON yang valid, TANPA markdown code blocks, dengan struktur EXACT:\n" +
                        "{\n" +
                        "  \"summary\": \"string panjang dan detail (minimal 150 kata)\",\n" +
                        "  \"recommendations\": [\"rekomendasi 1\", \"rekomendasi 2\", \"rekomendasi 3\", \"rekomendasi 4\", \"rekomendasi 5\"],\n" +
                        "  \"trend_analysis\": \"string panjang dan detail (minimal 100 kata) - WAJIB DIISI\"\n" +
                        "}\n" +
                        "\n" +
                        "ATURAN KETAT:\n" +
                        "- WAJIB mengembalikan SEMUA 3 field: summary, recommendations, trend_analysis\n" +
                        "- Field 'trend_analysis' TIDAK BOLEH kosong atau null\n" +
                        "- Gunakan bahasa Indonesia yang jelas dan profesional\n" +
                        "- JANGAN ubah nama key apapun (summary, recommendations, trend_analysis)\n" +
                        "- JANGAN tambahkan markdown code blocks (```json atau ```)\n" +
                        "- JANGAN tambahkan teks lain di luar JSON\n" +
                        "- Buat konten yang informatif, detail, dan bermanfaat\n" +
                        "- Fokus pada kualitas dan kedalaman analisis"
        );
        
        java.util.Map<String, Object> userMessage = java.util.Map.of(
                "role", "user",
                "content", "Berikut adalah data keuangan untuk dianalisis:\n\n" + 
                        objectMapper.valueToTree(payload).toString() +
                        "\n\n" +
                        "PENTING: Pastikan kamu mengembalikan SEMUA 3 field dalam JSON:\n" +
                        "1. summary (ringkasan keuangan)\n" +
                        "2. recommendations (array rekomendasi)\n" +
                        "3. trend_analysis (analisis tren - WAJIB DIISI dengan minimal 100 kata)\n\n" +
                        "Silakan buat ringkasan keuangan yang komprehensif, detail, dan informatif sesuai instruksi di atas."
        );
        
        java.util.Map<String, Object> body = java.util.Map.of(
                "model", openRouterModel,
                "messages", List.of(systemMessage, userMessage),
                "response_format", java.util.Map.of("type", "json_object")
        );
        
        // Call OpenRouter API with retry
        String responseContent = callOpenRouterWithRetry(body);
        
        // Parse response
        AIGenerateResponse aiResponse = parseAIResponse(responseContent, totalIncome.get(), totalExpense.get());
        
        // Save to database
        MonthlySummary summary = MonthlySummary.builder()
                .user(user)
                .month(now.format(DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("id-ID"))))
                .year(String.valueOf(now.getYear()))
                .totalIncome(String.valueOf(totalIncome.get()))
                .totalExpense(String.valueOf(totalExpense.get()))
                .balance(String.valueOf(totalIncome.get() - totalExpense.get()))
                .aiSummary(aiResponse.getSummary())
                .aiRecomendation(String.join("\n", aiResponse.getRecommendations()))
                .aiTrendAnalysis(aiResponse.getTrendAnalysis())
                .build();
        
        monthlySummaryRepository.save(summary);
        
        return aiResponse;
    }
    
    private String callOpenRouterWithRetry(java.util.Map<String, Object> body) {
        // Validate API key
        if (openRouterApiKey == null || openRouterApiKey.trim().isEmpty()) {
            throw new BadRequestException("OpenRouter API key tidak dikonfigurasi. Silakan set OPENROUTER_API_KEY environment variable.");
        }
        
        WebClient webClient = WebClient.builder()
                .baseUrl(openRouterApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openRouterApiKey.trim())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("HTTP-Referer", "https://budget-tracker-app.com") // Optional: untuk tracking
                .defaultHeader("X-Title", "Budget Tracker") // Optional: untuk tracking
                .build();
        
        int maxRetries = 2; // Kurangi retry untuk menghindari rate limit
        Exception lastException = null;
        boolean isRateLimited = false;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                log.debug("Calling OpenRouter API (attempt {}/{})", i + 1, maxRetries);
                log.debug("API URL: {}", openRouterApiUrl);
                log.debug("Model: {}", openRouterModel);
                
                String response = webClient.post()
                        .bodyValue(body)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                                clientResponse -> {
                                    HttpStatus httpStatus = HttpStatus.valueOf(clientResponse.statusCode().value());
                                    
                                    return clientResponse.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("OpenRouter API Error: Status {} - Body: {}", 
                                                        clientResponse.statusCode(), errorBody);
                                                
                                                // Handle 429 Rate Limit khusus
                                                if (httpStatus == HttpStatus.TOO_MANY_REQUESTS) {
                                                    String errorMessage = "Rate limit tercapai. Silakan tunggu beberapa saat sebelum mencoba lagi.";
                                                    try {
                                                        JsonNode errorJson = objectMapper.readTree(errorBody);
                                                        if (errorJson.has("error") && errorJson.get("error").has("message")) {
                                                            errorMessage = "Rate limit tercapai: " + 
                                                                    errorJson.get("error").get("message").asText() + 
                                                                    ". Silakan tunggu beberapa saat sebelum mencoba lagi.";
                                                        }
                                                    } catch (Exception e) {
                                                        // Use default message
                                                    }
                                                    return Mono.error(new RateLimitException(errorMessage));
                                                }
                                                
                                                // Handle error lainnya
                                                try {
                                                    JsonNode errorJson = objectMapper.readTree(errorBody);
                                                    String errorMessage = errorJson.has("error") && errorJson.get("error").has("message")
                                                            ? errorJson.get("error").get("message").asText()
                                                            : errorBody;
                                                    
                                                    // Untuk 401, 403, 400 - jangan retry
                                                    if (httpStatus == HttpStatus.UNAUTHORIZED || 
                                                        httpStatus == HttpStatus.FORBIDDEN || 
                                                        httpStatus == HttpStatus.BAD_REQUEST) {
                                                        return Mono.error(new BadRequestException(
                                                                "Gagal menghubungi AI Service: " + clientResponse.statusCode() + " " + errorMessage));
                                                    }
                                                    
                                                    return Mono.error(new BadRequestException(
                                                            "Gagal menghubungi AI Service: " + clientResponse.statusCode() + " " + errorMessage));
                                                } catch (Exception e) {
                                                    return Mono.error(new BadRequestException(
                                                            "Gagal menghubungi AI Service: " + clientResponse.statusCode() + " " + errorBody));
                                                }
                                            });
                                })
                        .bodyToMono(String.class)
                        .block();
                
                if (response == null || response.trim().isEmpty()) {
                    throw new BadRequestException("Response dari AI Service kosong");
                }
                
                JsonNode jsonResponse = objectMapper.readTree(response);
                
                if (jsonResponse.has("error")) {
                    String errorMsg = jsonResponse.get("error").has("message") 
                            ? jsonResponse.get("error").get("message").asText()
                            : jsonResponse.get("error").toString();
                    throw new BadRequestException("Gagal menghubungi AI Service: " + errorMsg);
                }
                
                if (!jsonResponse.has("choices") || jsonResponse.get("choices").size() == 0) {
                    throw new BadRequestException("Response dari AI Service tidak memiliki choices");
                }
                
                String content = jsonResponse.get("choices").get(0).get("message").get("content").asText();
                log.info("Raw AI response content length: {} characters", content.length());
                log.debug("Raw AI response content (first 500 chars): {}", 
                        content.length() > 500 ? content.substring(0, 500) + "..." : content);
                return content;
                
            } catch (RateLimitException e) {
                // Rate limit - jangan retry, langsung throw dengan pesan yang jelas
                isRateLimited = true;
                throw e;
            } catch (BadRequestException e) {
                // Re-throw BadRequestException immediately (don't retry untuk client errors)
                throw e;
            } catch (WebClientResponseException e) {
                // Handle WebClientResponseException khusus
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    isRateLimited = true;
                    throw new RateLimitException("Rate limit tercapai. Silakan tunggu beberapa saat sebelum mencoba lagi.");
                }
                lastException = e;
                log.warn("OpenRouter API call failed (attempt {}/{}): {} - {}", 
                        i + 1, maxRetries, e.getStatusCode(), e.getMessage());
                if (i < maxRetries - 1) {
                    // Exponential backoff: 5s, 10s, 20s
                    long delayMs = 5000L * (long) Math.pow(2, i);
                    try {
                        log.info("Retrying after {}ms...", delayMs);
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BadRequestException("Request interrupted");
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.warn("OpenRouter API call failed (attempt {}/{}): {}", i + 1, maxRetries, e.getMessage());
                if (i < maxRetries - 1) {
                    // Exponential backoff: 5s, 10s
                    long delayMs = 5000L * (long) Math.pow(2, i);
                    try {
                        log.info("Retrying after {}ms...", delayMs);
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BadRequestException("Request interrupted");
                    }
                }
            }
        }
        
        // Jika masih rate limited setelah retry
        if (isRateLimited) {
            throw new RateLimitException("Rate limit tercapai setelah beberapa percobaan. Silakan tunggu beberapa menit sebelum mencoba lagi.");
        }
        
        String errorMessage = "Gagal menghubungi AI Service setelah " + maxRetries + " percobaan";
        if (lastException != null) {
            if (lastException.getMessage() != null) {
                errorMessage += ": " + lastException.getMessage();
            } else {
                errorMessage += ": " + lastException.getClass().getSimpleName();
            }
        }
        throw new BadRequestException(errorMessage);
    }
    
    private AIGenerateResponse parseAIResponse(String content, int totalIncome, int totalExpense) {
        try {
            // Remove markdown code blocks if present
            String cleaned = content.replaceAll("```json\\s*|\\s*```", "").trim();
            log.debug("Parsing AI response (cleaned): {}", cleaned);
            
            JsonNode json = objectMapper.readTree(cleaned);
            
            // Check if all required fields exist
            if (!json.has("summary")) {
                log.error("Missing 'summary' field in AI response");
                throw new BadRequestException("Struktur JSON Tidak Lengkap: field 'summary' tidak ditemukan");
            }
            if (!json.has("recommendations")) {
                log.error("Missing 'recommendations' field in AI response");
                throw new BadRequestException("Struktur JSON Tidak Lengkap: field 'recommendations' tidak ditemukan");
            }
            if (!json.has("trend_analysis")) {
                log.error("Missing 'trend_analysis' field in AI response. Available fields: {}", json.fieldNames());
                throw new BadRequestException("Struktur JSON Tidak Lengkap: field 'trend_analysis' tidak ditemukan. " +
                        "Pastikan AI mengembalikan field 'trend_analysis' dalam response JSON.");
            }
            
            List<String> recommendations = objectMapper.convertValue(
                    json.get("recommendations"), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            
            String trendAnalysis = json.get("trend_analysis").asText();
            log.info("Successfully parsed AI response - summary length: {}, recommendations count: {}, trend_analysis length: {}", 
                    json.get("summary").asText().length(), 
                    recommendations.size(),
                    trendAnalysis != null ? trendAnalysis.length() : 0);
            
            // Handle empty or null trend_analysis with fallback
            if (trendAnalysis == null || trendAnalysis.trim().isEmpty()) {
                log.warn("trend_analysis is empty or null in AI response, generating fallback");
                // Generate fallback trend analysis based on available data
                trendAnalysis = "Berdasarkan data keuangan yang tersedia, " +
                        "dapat dilihat bahwa kondisi keuangan menunjukkan " +
                        (totalIncome > totalExpense ? "surplus yang positif. " : "perlu perhatian lebih. ") +
                        "Pola pengeluaran dan pemasukan perlu dianalisis lebih lanjut untuk " +
                        "mengoptimalkan pengelolaan keuangan ke depan. " +
                        "Disarankan untuk melakukan evaluasi rutin terhadap kebiasaan keuangan " +
                        "dan menyesuaikan strategi sesuai dengan tujuan keuangan jangka panjang.";
            }
            
            return AIGenerateResponse.builder()
                    .summary(json.get("summary").asText())
                    .recommendations(recommendations)
                    .trendAnalysis(trendAnalysis)
                    .build();
                    
        } catch (BadRequestException e) {
            // Re-throw BadRequestException as is
            throw e;
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage(), e);
            throw new BadRequestException("Gagal Mengurai Response JSON dari LLM: " + e.getMessage() + ". Harap Di Coba Lagi!");
        }
    }
}


