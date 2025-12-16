package com.budgettracker.controller;

import com.budgettracker.dto.response.ApiResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {
    
    private final EntityManager entityManager;
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = Map.of(
                "ok", true,
                "env", System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "development"),
                "uptime", System.currentTimeMillis()
        );
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }
    
    @GetMapping("/db-ping")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dbPing() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return ResponseEntity.ok(ApiResponse.success("OK", Map.of("ok", true)));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Database connection failed: " + e.getMessage()));
        }
    }
}

