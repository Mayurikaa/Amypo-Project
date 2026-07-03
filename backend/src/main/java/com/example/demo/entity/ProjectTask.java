package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_task")
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_code", nullable = false, unique = true, length = 50)
    private String taskCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiative_id", nullable = false)
    private ProjectInitiative initiative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id", nullable = true)
    private ProjectMilestone milestone;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String priority;

    @Column(name = "estimated_hours", nullable = false)
    private Integer estimatedHours;

    @Column(name = "logged_hours", nullable = false)
    private Integer loggedHours = 0;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = true)
    private SystemAccount assignee;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProjectTask() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.loggedHours == null) this.loggedHours = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskCode() { return taskCode; }
    public void setTaskCode(String taskCode) { this.taskCode = taskCode; }

    public ProjectInitiative getInitiative() { return initiative; }
    public void setInitiative(ProjectInitiative initiative) { this.initiative = initiative; }

    public ProjectMilestone getMilestone() { return milestone; }
    public void setMilestone(ProjectMilestone milestone) { this.milestone = milestone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }

    public Integer getLoggedHours() { return loggedHours; }
    public void setLoggedHours(Integer loggedHours) { this.loggedHours = loggedHours; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public SystemAccount getAssignee() { return assignee; }
    public void setAssignee(SystemAccount assignee) { this.assignee = assignee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String taskCode;
        private ProjectInitiative initiative;
        private ProjectMilestone milestone;
        private String title;
        private String description;
        private String priority;
        private Integer estimatedHours;
        private Integer loggedHours = 0;
        private LocalDate dueDate;
        private SystemAccount assignee;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder taskCode(String taskCode) { this.taskCode = taskCode; return this; }
        public Builder initiative(ProjectInitiative initiative) { this.initiative = initiative; return this; }
        public Builder milestone(ProjectMilestone milestone) { this.milestone = milestone; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder priority(String priority) { this.priority = priority; return this; }
        public Builder estimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; return this; }
        public Builder loggedHours(Integer loggedHours) { this.loggedHours = loggedHours; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder assignee(SystemAccount assignee) { this.assignee = assignee; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectTask build() {
            ProjectTask t = new ProjectTask();
            t.id = this.id;
            t.taskCode = this.taskCode;
            t.initiative = this.initiative;
            t.milestone = this.milestone;
            t.title = this.title;
            t.description = this.description;
            t.priority = this.priority;
            t.estimatedHours = this.estimatedHours;
            t.loggedHours = this.loggedHours;
            t.dueDate = this.dueDate;
            t.assignee = this.assignee;
            t.status = this.status;
            return t;
        }
    }
}
