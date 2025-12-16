package com.budgettracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsResponse {
    private Integer income;
    private Integer expense;
    private Integer balance;
    private Integer saving;
}

