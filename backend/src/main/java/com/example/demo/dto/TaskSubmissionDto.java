package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSubmissionDto {

    private Long id;
    private Long taskId;
    private Long contributorId;
    private Integer hoursSpent;
    private String submissionNotes;
    private String reviewerFeedback;
    private String completionStatus;
    private LocalDateTime submittedAt;
}
