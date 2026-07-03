package com.example.demo.dto;

import java.util.Map;

public class PortfolioAnalyticsDto {

    private Long totalInitiatives;
    private Long totalTasks;
    private Long activeTasksCount;
    private Long totalHoursLogged;
    private Map<String, Long> domainDistribution;

    public PortfolioAnalyticsDto() {}

    public Long getTotalInitiatives() { return totalInitiatives; }
    public void setTotalInitiatives(Long totalInitiatives) { this.totalInitiatives = totalInitiatives; }

    public Long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Long totalTasks) { this.totalTasks = totalTasks; }

    public Long getActiveTasksCount() { return activeTasksCount; }
    public void setActiveTasksCount(Long activeTasksCount) { this.activeTasksCount = activeTasksCount; }

    public Long getTotalHoursLogged() { return totalHoursLogged; }
    public void setTotalHoursLogged(Long totalHoursLogged) { this.totalHoursLogged = totalHoursLogged; }

    public Map<String, Long> getDomainDistribution() { return domainDistribution; }
    public void setDomainDistribution(Map<String, Long> domainDistribution) { this.domainDistribution = domainDistribution; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long totalInitiatives;
        private Long totalTasks;
        private Long activeTasksCount;
        private Long totalHoursLogged;
        private Map<String, Long> domainDistribution;

        public Builder totalInitiatives(Long totalInitiatives) { this.totalInitiatives = totalInitiatives; return this; }
        public Builder totalTasks(Long totalTasks) { this.totalTasks = totalTasks; return this; }
        public Builder activeTasksCount(Long activeTasksCount) { this.activeTasksCount = activeTasksCount; return this; }
        public Builder totalHoursLogged(Long totalHoursLogged) { this.totalHoursLogged = totalHoursLogged; return this; }
        public Builder domainDistribution(Map<String, Long> domainDistribution) { this.domainDistribution = domainDistribution; return this; }

        public PortfolioAnalyticsDto build() {
            PortfolioAnalyticsDto d = new PortfolioAnalyticsDto();
            d.totalInitiatives = this.totalInitiatives; d.totalTasks = this.totalTasks;
            d.activeTasksCount = this.activeTasksCount; d.totalHoursLogged = this.totalHoursLogged;
            d.domainDistribution = this.domainDistribution;
            return d;
        }
    }
}
