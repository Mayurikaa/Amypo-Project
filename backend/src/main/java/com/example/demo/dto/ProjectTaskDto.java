package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

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

    public ProjectTaskDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskCode() { return taskCode; }
    public void setTaskCode(String taskCode) { this.taskCode = taskCode; }

    public Long getInitiativeId() { return initiativeId; }
    public void setInitiativeId(Long initiativeId) { this.initiativeId = initiativeId; }

    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }

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

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String taskCode;
        private Long initiativeId;
        private Long milestoneId;
        private String title;
        private String description;
        private String priority;
        private Integer estimatedHours;
        private Integer loggedHours;
        private LocalDate dueDate;
        private Long assigneeId;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder taskCode(String taskCode) { this.taskCode = taskCode; return this; }
        public Builder initiativeId(Long initiativeId) { this.initiativeId = initiativeId; return this; }
        public Builder milestoneId(Long milestoneId) { this.milestoneId = milestoneId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder priority(String priority) { this.priority = priority; return this; }
        public Builder estimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; return this; }
        public Builder loggedHours(Integer loggedHours) { this.loggedHours = loggedHours; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder assigneeId(Long assigneeId) { this.assigneeId = assigneeId; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectTaskDto build() {
            ProjectTaskDto d = new ProjectTaskDto();
            d.id = this.id; d.taskCode = this.taskCode; d.initiativeId = this.initiativeId;
            d.milestoneId = this.milestoneId; d.title = this.title; d.description = this.description;
            d.priority = this.priority; d.estimatedHours = this.estimatedHours;
            d.loggedHours = this.loggedHours; d.dueDate = this.dueDate;
            d.assigneeId = this.assigneeId; d.status = this.status;
            return d;
        }
    }
}
