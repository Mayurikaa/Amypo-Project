package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectInitiativeDto {

    private Long id;
    private String projectCode;
    private String title;
    private String description;
    private BigDecimal budgetAllocated;
    private BigDecimal budgetConsumed;
    private LocalDate startDate;
    private LocalDate targetEndDate;
    private Long directorId;
    private String status;
}
