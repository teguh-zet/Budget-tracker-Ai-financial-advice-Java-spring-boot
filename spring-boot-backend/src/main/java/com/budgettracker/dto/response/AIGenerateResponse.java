package com.budgettracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGenerateResponse {
    private String summary;
    private List<String> recommendations;
    
    @JsonProperty("trend_analysis")
    private String trendAnalysis;
}

