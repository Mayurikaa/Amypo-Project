package com.example.demo.dto;

public class SubmissionReviewDto {

    private String reviewerFeedback;
    private String completionStatus;

    public SubmissionReviewDto() {}

    public String getReviewerFeedback() { return reviewerFeedback; }
    public void setReviewerFeedback(String reviewerFeedback) { this.reviewerFeedback = reviewerFeedback; }

    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
}
