package com.budgettracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.budgettracker.config.CorsProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(CorsProperties.class)
public class BudgetTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BudgetTrackerApplication.class, args);
    }
}

