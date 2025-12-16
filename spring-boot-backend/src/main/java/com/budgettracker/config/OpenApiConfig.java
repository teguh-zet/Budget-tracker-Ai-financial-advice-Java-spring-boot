package com.budgettracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Budget Tracker API")
                        .version("1.0.0")
                        .description("""
                                API Documentation untuk Budget Tracker Application.
                                
                                ## Fitur Utama:
                                - Authentication (Register, Login, Profile)
                                - Transaction Management (Income/Expense)
                                - Category Management
                                - Monthly Summary dengan AI
                                - Budget Management
                                - Financial Goals
                                - PDF Export
                                - Profile Picture Upload
                                
                                ## Authentication:
                                Sebagian besar endpoint memerlukan JWT token. 
                                Dapatkan token dengan melakukan login, kemudian gunakan token tersebut 
                                di header Authorization dengan format: `Bearer <token>`
                                """)
                        .contact(new Contact()
                                .name("Budget Tracker Team")
                                .email("support@budgettracker.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:5001")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.budgettracker.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("""
                        Masukkan JWT token yang didapat dari endpoint /api/v1/auth/login
                        
                        Format: Bearer <your-token>
                        
                        Contoh: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                        """);
    }
}

