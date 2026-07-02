package com.example.demo.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMilestoneDto {

    private Long id;
    private Long initiativeId;
    private String title;
    private LocalDate targetDate;
    private Integer allocatedHours;
    private String status;
}
