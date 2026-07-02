package com.example.demo.dto;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioAnalyticsDto {

    private Long totalInitiatives;
    private Long totalTasks;
    private Long activeTasksCount;
    private Long totalHoursLogged;
    private Map<String, Long> domainDistribution;
}
