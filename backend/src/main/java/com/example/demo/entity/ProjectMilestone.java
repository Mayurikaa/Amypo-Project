package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_milestone")
public class ProjectMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiative_id", nullable = false)
    private ProjectInitiative initiative;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "allocated_hours", nullable = false)
    private Integer allocatedHours;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ProjectMilestone() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProjectInitiative getInitiative() { return initiative; }
    public void setInitiative(ProjectInitiative initiative) { this.initiative = initiative; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public Integer getAllocatedHours() { return allocatedHours; }
    public void setAllocatedHours(Integer allocatedHours) { this.allocatedHours = allocatedHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private ProjectInitiative initiative;
        private String title;
        private LocalDate targetDate;
        private Integer allocatedHours;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder initiative(ProjectInitiative initiative) { this.initiative = initiative; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder targetDate(LocalDate targetDate) { this.targetDate = targetDate; return this; }
        public Builder allocatedHours(Integer allocatedHours) { this.allocatedHours = allocatedHours; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectMilestone build() {
            ProjectMilestone m = new ProjectMilestone();
            m.id = this.id;
            m.initiative = this.initiative;
            m.title = this.title;
            m.targetDate = this.targetDate;
            m.allocatedHours = this.allocatedHours;
            m.status = this.status;
            return m;
        }
    }
}
