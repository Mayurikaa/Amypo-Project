package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTaskDtoTest {

    @Test
    void getId_and_setId_work() {
        ProjectTaskDto dto = new ProjectTaskDto();
        dto.setId(42L);
        assertEquals(42L, dto.getId());
    }

    @Test
    void getTitle_and_setTitle_work() {
        ProjectTaskDto dto = new ProjectTaskDto();
        dto.setTitle("Actionable task scope title is required.");
        assertEquals("Actionable task scope title is required.", dto.getTitle());
    }

    @Test
    void builder_createsDto_withAllFields() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .id(1L)
                .taskCode("TASK-001")
                .initiativeId(10L)
                .milestoneId(5L)
                .title("Sample Task")
                .description("Description")
                .priority("HIGH")
                .estimatedHours(8)
                .loggedHours(2)
                .dueDate(LocalDate.now().plusDays(7))
                .assigneeId(3L)
                .status("PENDING")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("TASK-001", dto.getTaskCode());
        assertEquals(10L, dto.getInitiativeId());
        assertEquals(5L, dto.getMilestoneId());
        assertEquals("Sample Task", dto.getTitle());
        assertEquals("HIGH", dto.getPriority());
        assertEquals(8, dto.getEstimatedHours());
        assertEquals(2, dto.getLoggedHours());
        assertEquals(3L, dto.getAssigneeId());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void noArgsConstructor_createsEmptyDto() {
        ProjectTaskDto dto = new ProjectTaskDto();
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getTaskCode());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        LocalDate due = LocalDate.now().plusDays(5);
        ProjectTaskDto dto = new ProjectTaskDto(
                1L, "TASK-001", 10L, 5L,
                "Title", "Desc", "HIGH",
                8, 0, due, 3L, "PENDING"
        );
        assertEquals(1L, dto.getId());
        assertEquals("TASK-001", dto.getTaskCode());
        assertEquals("Title", dto.getTitle());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        ProjectTaskDto dto = new ProjectTaskDto();
        dto.setTaskCode("TASK-XYZ");
        dto.setInitiativeId(100L);
        dto.setPriority("CRITICAL");
        dto.setEstimatedHours(20);
        dto.setStatus("IN_PROGRESS");

        assertEquals("TASK-XYZ", dto.getTaskCode());
        assertEquals(100L, dto.getInitiativeId());
        assertEquals("CRITICAL", dto.getPriority());
        assertEquals(20, dto.getEstimatedHours());
        assertEquals("IN_PROGRESS", dto.getStatus());
    }
}
