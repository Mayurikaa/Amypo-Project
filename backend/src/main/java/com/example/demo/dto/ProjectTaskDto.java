package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskDto {

    private Long id;

    @NotBlank(message = "Sequential task tag string must be specified.")
    private String taskCode;

    @NotNull(message = "Parent project initiative mapping is required.")
    private Long initiativeId;

    private Long milestoneId;

    @NotBlank(message = "Actionable task scope title is required.")
    private String title;

    private String description;

    @NotBlank
    private String priority;

    @NotNull
    @Min(value = 1, message = "Estimated workload capacity must be a positive count.")
    private Integer estimatedHours;

    private Integer loggedHours;

    @NotNull
    private LocalDate dueDate;

    private Long assigneeId;

    private String status;
}
