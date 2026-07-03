package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_initiative")
public class ProjectInitiative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_code", nullable = false, unique = true, length = 50)
    private String projectCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "budget_allocated", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetAllocated;

    @Column(name = "budget_consumed", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetConsumed = BigDecimal.ZERO;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "target_end_date", nullable = false)
    private LocalDate targetEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false)
    private SystemAccount director;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProjectInitiative() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.budgetConsumed == null) this.budgetConsumed = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public SystemAccount getDirector() { return director; }
    public void setDirector(SystemAccount director) { this.director = director; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String projectCode;
        private String title;
        private String description;
        private BigDecimal budgetAllocated;
        private BigDecimal budgetConsumed = BigDecimal.ZERO;
        private LocalDate startDate;
        private LocalDate targetEndDate;
        private SystemAccount director;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder projectCode(String projectCode) { this.projectCode = projectCode; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder budgetAllocated(BigDecimal budgetAllocated) { this.budgetAllocated = budgetAllocated; return this; }
        public Builder budgetConsumed(BigDecimal budgetConsumed) { this.budgetConsumed = budgetConsumed; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder targetEndDate(LocalDate targetEndDate) { this.targetEndDate = targetEndDate; return this; }
        public Builder director(SystemAccount director) { this.director = director; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectInitiative build() {
            ProjectInitiative p = new ProjectInitiative();
            p.id = this.id;
            p.projectCode = this.projectCode;
            p.title = this.title;
            p.description = this.description;
            p.budgetAllocated = this.budgetAllocated;
            p.budgetConsumed = this.budgetConsumed;
            p.startDate = this.startDate;
            p.targetEndDate = this.targetEndDate;
            p.director = this.director;
            p.status = this.status;
            return p;
        }
    }
}
