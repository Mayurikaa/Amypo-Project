package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectInitiativeDto {

    private Long id;
    private String projectCode;
    private String title;
    private String description;
    private BigDecimal budgetAllocated;
    private BigDecimal budgetConsumed;
    private LocalDate startDate;
    private LocalDate targetEndDate;
    private Long directorId;
    private String status;

    public ProjectInitiativeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBudgetAllocated() { return budgetAllocated; }
    public void setBudgetAllocated(BigDecimal budgetAllocated) { this.budgetAllocated = budgetAllocated; }

    public BigDecimal getBudgetConsumed() { return budgetConsumed; }
    public void setBudgetConsumed(BigDecimal budgetConsumed) { this.budgetConsumed = budgetConsumed; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getTargetEndDate() { return targetEndDate; }
    public void setTargetEndDate(LocalDate targetEndDate) { this.targetEndDate = targetEndDate; }

    public Long getDirectorId() { return directorId; }
    public void setDirectorId(Long directorId) { this.directorId = directorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String projectCode;
        private String title;
        private String description;
        private BigDecimal budgetAllocated;
        private BigDecimal budgetConsumed;
        private LocalDate startDate;
        private LocalDate targetEndDate;
        private Long directorId;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder projectCode(String projectCode) { this.projectCode = projectCode; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder budgetAllocated(BigDecimal budgetAllocated) { this.budgetAllocated = budgetAllocated; return this; }
        public Builder budgetConsumed(BigDecimal budgetConsumed) { this.budgetConsumed = budgetConsumed; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder targetEndDate(LocalDate targetEndDate) { this.targetEndDate = targetEndDate; return this; }
        public Builder directorId(Long directorId) { this.directorId = directorId; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectInitiativeDto build() {
            ProjectInitiativeDto d = new ProjectInitiativeDto();
            d.id = this.id; d.projectCode = this.projectCode; d.title = this.title;
            d.description = this.description; d.budgetAllocated = this.budgetAllocated;
            d.budgetConsumed = this.budgetConsumed; d.startDate = this.startDate;
            d.targetEndDate = this.targetEndDate; d.directorId = this.directorId;
            d.status = this.status;
            return d;
        }
    }
}
