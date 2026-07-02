package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_submission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }
}
