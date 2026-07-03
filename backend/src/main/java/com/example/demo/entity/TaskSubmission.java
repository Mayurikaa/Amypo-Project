package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_submission")
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private ProjectTask task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    private SystemAccount contributor;

    @Column(name = "hours_spent", nullable = false)
    private Integer hoursSpent;

    @Column(name = "submission_notes", nullable = false, columnDefinition = "TEXT")
    private String submissionNotes;

    @Column(name = "reviewer_feedback", columnDefinition = "TEXT")
    private String reviewerFeedback;

    @Column(name = "completion_status", nullable = false, length = 50)
    private String completionStatus;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    public TaskSubmission() {}

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProjectTask getTask() { return task; }
    public void setTask(ProjectTask task) { this.task = task; }

    public SystemAccount getContributor() { return contributor; }
    public void setContributor(SystemAccount contributor) { this.contributor = contributor; }

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
        private ProjectTask task;
        private SystemAccount contributor;
        private Integer hoursSpent;
        private String submissionNotes;
        private String reviewerFeedback;
        private String completionStatus;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder task(ProjectTask task) { this.task = task; return this; }
        public Builder contributor(SystemAccount contributor) { this.contributor = contributor; return this; }
        public Builder hoursSpent(Integer hoursSpent) { this.hoursSpent = hoursSpent; return this; }
        public Builder submissionNotes(String submissionNotes) { this.submissionNotes = submissionNotes; return this; }
        public Builder reviewerFeedback(String reviewerFeedback) { this.reviewerFeedback = reviewerFeedback; return this; }
        public Builder completionStatus(String completionStatus) { this.completionStatus = completionStatus; return this; }

        public TaskSubmission build() {
            TaskSubmission s = new TaskSubmission();
            s.id = this.id;
            s.task = this.task;
            s.contributor = this.contributor;
            s.hoursSpent = this.hoursSpent;
            s.submissionNotes = this.submissionNotes;
            s.reviewerFeedback = this.reviewerFeedback;
            s.completionStatus = this.completionStatus;
            return s;
        }
    }
}
