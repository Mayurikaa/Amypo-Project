package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void systemAccount_builder_setsAllFields() {
        SystemAccount account = SystemAccount.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashed")
                .fullName("Test User")
                .domainRole("PROJECT_MANAGER")
                .isActive(true)
                .build();

        assertEquals(1L, account.getId());
        assertEquals("test@example.com", account.getEmail());
        assertEquals("hashed", account.getPasswordHash());
        assertEquals("Test User", account.getFullName());
        assertEquals("PROJECT_MANAGER", account.getDomainRole());
        assertTrue(account.getIsActive());
    }

    @Test
    void systemAccount_isActive_defaultsToTrue() {
        SystemAccount account = SystemAccount.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .fullName("User")
                .domainRole("TEAM_CONTRIBUTOR")
                .build();

        assertTrue(account.getIsActive());
    }

    @Test
    void systemAccount_onPrePersist_setsCreatedAt() {
        SystemAccount account = SystemAccount.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .fullName("User")
                .domainRole("TEAM_CONTRIBUTOR")
                .build();
        account.onCreate();

        assertNotNull(account.getCreatedAt());
    }

    @Test
    void projectTask_builder_setsAllFields() {
        SystemAccount director = SystemAccount.builder()
                .id(1L).email("d@example.com").fullName("Director")
                .domainRole("PROJECT_DIRECTOR").isActive(true).build();

        ProjectInitiative initiative = ProjectInitiative.builder()
                .id(1L).projectCode("PRJ-001").title("Initiative")
                .budgetAllocated(BigDecimal.valueOf(10000))
                .budgetConsumed(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(3))
                .director(director).status("ACTIVE").build();

        ProjectTask task = ProjectTask.builder()
                .id(100L)
                .taskCode("TASK-001")
                .initiative(initiative)
                .title("Task One")
                .priority("HIGH")
                .estimatedHours(8)
                .loggedHours(0)
                .dueDate(LocalDate.now().plusDays(7))
                .status("PENDING")
                .build();

        assertEquals(100L, task.getId());
        assertEquals("TASK-001", task.getTaskCode());
        assertEquals("Task One", task.getTitle());
        assertEquals("HIGH", task.getPriority());
        assertEquals(8, task.getEstimatedHours());
        assertEquals(0, task.getLoggedHours());
        assertEquals("PENDING", task.getStatus());
    }

    @Test
    void projectTask_loggedHours_defaultsToZero() {
        ProjectTask task = ProjectTask.builder()
                .taskCode("T-001")
                .title("Task")
                .priority("LOW")
                .estimatedHours(5)
                .dueDate(LocalDate.now().plusDays(3))
                .status("PENDING")
                .build();

        assertEquals(0, task.getLoggedHours());
    }

    @Test
    void projectTask_onPrePersist_setsCreatedAt() {
        ProjectTask task = ProjectTask.builder()
                .taskCode("T-001").title("Task").priority("LOW")
                .estimatedHours(5).dueDate(LocalDate.now().plusDays(3))
                .status("PENDING").build();
        task.onCreate();

        assertNotNull(task.getCreatedAt());
    }

    @Test
    void projectInitiative_onPrePersist_setsBudgetConsumedToZero() {
        SystemAccount director = SystemAccount.builder()
                .id(1L).email("d@example.com").fullName("Director")
                .domainRole("PROJECT_DIRECTOR").isActive(true).build();

        ProjectInitiative initiative = ProjectInitiative.builder()
                .projectCode("PRJ-001").title("Initiative")
                .budgetAllocated(BigDecimal.valueOf(5000))
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(2))
                .director(director).status("ACTIVE").build();
        initiative.onCreate();

        assertNotNull(initiative.getCreatedAt());
        assertEquals(BigDecimal.ZERO, initiative.getBudgetConsumed());
    }

    @Test
    void taskSubmission_builder_setsAllFields() {
        SystemAccount contributor = SystemAccount.builder()
                .id(5L).email("c@example.com").fullName("Contributor")
                .domainRole("TEAM_CONTRIBUTOR").isActive(true).build();

        TaskSubmission submission = TaskSubmission.builder()
                .id(1L)
                .contributor(contributor)
                .hoursSpent(4)
                .submissionNotes("Notes here")
                .completionStatus("PENDING_REVIEW")
                .build();

        assertEquals(1L, submission.getId());
        assertEquals(4, submission.getHoursSpent());
        assertEquals("Notes here", submission.getSubmissionNotes());
        assertEquals("PENDING_REVIEW", submission.getCompletionStatus());
    }
}
