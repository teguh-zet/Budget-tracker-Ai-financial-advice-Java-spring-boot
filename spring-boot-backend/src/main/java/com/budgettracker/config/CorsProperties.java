package com.budgettracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cors")
@Data
public class CorsProperties {
    private String allowedOrigins = "http://localhost:3000";
    private String allowedMethods = "GET,POST,PUT,DELETE,PATCH,OPTIONS";
    private String allowedHeaders = "Content-Type,Authorization";
    private boolean allowCredentials = true;
}

