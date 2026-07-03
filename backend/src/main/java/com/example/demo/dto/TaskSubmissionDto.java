package com.example.demo.dto;

import java.time.LocalDateTime;

public class TaskSubmissionDto {

    private Long id;
    private Long taskId;
    private Long contributorId;
    private Integer hoursSpent;
    private String submissionNotes;
    private String reviewerFeedback;
    private String completionStatus;
    private LocalDateTime submittedAt;

    public TaskSubmissionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getContributorId() { return contributorId; }
    public void setContributorId(Long contributorId) { this.contributorId = contributorId; }

    public Integer getHoursSpent() { return hoursSpent; }
    public void setHoursSpent(Integer hoursSpent) { this.hoursSpent = hoursSpent; }

    public String getSubmissionNotes() { return submissionNotes; }
    public void setSubmissionNotes(String submissionNotes) { this.submissionNotes = submissionNotes; }

    public String getReviewerFeedback() { return reviewerFeedback; }
    public void setReviewerFeedback(String reviewerFeedback) { this.reviewerFeedback = reviewerFeedback; }

    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long taskId;
        private Long contributorId;
        private Integer hoursSpent;
        private String submissionNotes;
        private String reviewerFeedback;
        private String completionStatus;
        private LocalDateTime submittedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder taskId(Long taskId) { this.taskId = taskId; return this; }
        public Builder contributorId(Long contributorId) { this.contributorId = contributorId; return this; }
        public Builder hoursSpent(Integer hoursSpent) { this.hoursSpent = hoursSpent; return this; }
        public Builder submissionNotes(String submissionNotes) { this.submissionNotes = submissionNotes; return this; }
        public Builder reviewerFeedback(String reviewerFeedback) { this.reviewerFeedback = reviewerFeedback; return this; }
        public Builder completionStatus(String completionStatus) { this.completionStatus = completionStatus; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }

        public TaskSubmissionDto build() {
            TaskSubmissionDto d = new TaskSubmissionDto();
            d.id = this.id; d.taskId = this.taskId; d.contributorId = this.contributorId;
            d.hoursSpent = this.hoursSpent; d.submissionNotes = this.submissionNotes;
            d.reviewerFeedback = this.reviewerFeedback; d.completionStatus = this.completionStatus;
            d.submittedAt = this.submittedAt;
            return d;
        }
    }
}
