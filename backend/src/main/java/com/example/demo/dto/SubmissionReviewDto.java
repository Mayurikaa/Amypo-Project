package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionReviewDto {

    private String reviewerFeedback;
    private String completionStatus;
}
