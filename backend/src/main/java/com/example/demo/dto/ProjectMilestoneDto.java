package com.example.demo.dto;

import java.time.LocalDate;

public class ProjectMilestoneDto {

    private Long id;
    private Long initiativeId;
    private String title;
    private LocalDate targetDate;
    private Integer allocatedHours;
    private String status;

    public ProjectMilestoneDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInitiativeId() { return initiativeId; }
    public void setInitiativeId(Long initiativeId) { this.initiativeId = initiativeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public Integer getAllocatedHours() { return allocatedHours; }
    public void setAllocatedHours(Integer allocatedHours) { this.allocatedHours = allocatedHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long initiativeId;
        private String title;
        private LocalDate targetDate;
        private Integer allocatedHours;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder initiativeId(Long initiativeId) { this.initiativeId = initiativeId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder targetDate(LocalDate targetDate) { this.targetDate = targetDate; return this; }
        public Builder allocatedHours(Integer allocatedHours) { this.allocatedHours = allocatedHours; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectMilestoneDto build() {
            ProjectMilestoneDto d = new ProjectMilestoneDto();
            d.id = this.id; d.initiativeId = this.initiativeId; d.title = this.title;
            d.targetDate = this.targetDate; d.allocatedHours = this.allocatedHours; d.status = this.status;
            return d;
        }
    }
}
