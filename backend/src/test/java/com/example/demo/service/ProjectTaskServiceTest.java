package com.example.demo.service;

import com.example.demo.dto.ProjectTaskDto;
import com.example.demo.entity.ProjectInitiative;
import com.example.demo.entity.ProjectTask;
import com.example.demo.entity.SystemAccount;
import com.example.demo.exception.CapacityExceededException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;
import com.example.demo.repository.ProjectInitiativeRepository;
import com.example.demo.repository.ProjectMilestoneRepository;
import com.example.demo.repository.ProjectTaskRepository;
import com.example.demo.repository.SystemAccountRepository;
import com.example.demo.repository.TaskSubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectTaskServiceTest {

    @Mock private ProjectTaskRepository projectTaskRepository;
    @Mock private ProjectInitiativeRepository projectInitiativeRepository;
    @Mock private ProjectMilestoneRepository projectMilestoneRepository;
    @Mock private SystemAccountRepository systemAccountRepository;
    @Mock private TaskSubmissionRepository taskSubmissionRepository;

    @InjectMocks
    private ProjectTaskService projectTaskService;

    private ProjectTask sampleTask;
    private ProjectInitiative sampleInitiative;
    private SystemAccount sampleAssignee;

    @BeforeEach
    void setUp() {
        sampleAssignee = SystemAccount.builder()
                .id(10L).email("assignee@example.com")
                .fullName("Assignee").domainRole("TEAM_CONTRIBUTOR").isActive(true).build();

        sampleInitiative = ProjectInitiative.builder()
                .id(1L).projectCode("PRJ-001").title("Initiative One")
                .status("ACTIVE").director(sampleAssignee).build();

        sampleTask = ProjectTask.builder()
                .id(100L).taskCode("TASK-001").initiative(sampleInitiative)
                .title("Sample Task").priority("HIGH").estimatedHours(8)
                .loggedHours(0).dueDate(LocalDate.now().plusDays(7))
                .status("PENDING").build();
    }

    @Test
    void createTask_success() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .taskCode("TASK-NEW").initiativeId(1L).title("New Task")
                .priority("HIGH").estimatedHours(5)
                .dueDate(LocalDate.now().plusDays(5)).build();

        when(projectTaskRepository.findByTaskCode("TASK-NEW")).thenReturn(Optional.empty());
        when(projectInitiativeRepository.findById(1L)).thenReturn(Optional.of(sampleInitiative));
        when(projectTaskRepository.save(any())).thenReturn(sampleTask);

        ProjectTaskDto result = projectTaskService.createTask(dto);

        assertNotNull(result);
        verify(projectTaskRepository).save(any());
    }

    @Test
    void createTask_duplicateTaskCode_throwsCapacityExceededException() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(1L).title("Task")
                .priority("HIGH").estimatedHours(5)
                .dueDate(LocalDate.now().plusDays(5)).build();

        when(projectTaskRepository.findByTaskCode("TASK-001")).thenReturn(Optional.of(sampleTask));

        CapacityExceededException ex = assertThrows(CapacityExceededException.class,
                () -> projectTaskService.createTask(dto));
        assertTrue(ex.getMessage().contains("Task Code parameter matching collision"));
    }

    @Test
    void createTask_initiativeNotFound_throwsResourceNotFoundException() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .taskCode("TASK-NEW").initiativeId(999L).title("Task")
                .priority("HIGH").estimatedHours(5)
                .dueDate(LocalDate.now().plusDays(5)).build();

        when(projectTaskRepository.findByTaskCode("TASK-NEW")).thenReturn(Optional.empty());
        when(projectInitiativeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectTaskService.createTask(dto));
    }

    @Test
    void createTask_assigneeCapacityExceeded_throwsCapacityExceededException() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .taskCode("TASK-NEW").initiativeId(1L).title("Task")
                .priority("HIGH").estimatedHours(10).assigneeId(10L)
                .dueDate(LocalDate.now().plusDays(5)).build();

        when(projectTaskRepository.findByTaskCode("TASK-NEW")).thenReturn(Optional.empty());
        when(projectInitiativeRepository.findById(1L)).thenReturn(Optional.of(sampleInitiative));
        when(systemAccountRepository.findById(10L)).thenReturn(Optional.of(sampleAssignee));
        when(projectTaskRepository.calculateRemainingHoursForAssignee(10L)).thenReturn(35L);

        assertThrows(CapacityExceededException.class, () -> projectTaskService.createTask(dto));
    }

    @Test
    void validateAssigneeWorkloadCapacity_withinLimit_noException() {
        when(projectTaskRepository.calculateRemainingHoursForAssignee(10L)).thenReturn(20L);

        assertDoesNotThrow(() -> projectTaskService.validateAssigneeWorkloadCapacity(10L, 15));
    }

    @Test
    void validateAssigneeWorkloadCapacity_exceedsLimit_throwsCapacityExceededException() {
        when(projectTaskRepository.calculateRemainingHoursForAssignee(10L)).thenReturn(35L);

        CapacityExceededException ex = assertThrows(CapacityExceededException.class,
                () -> projectTaskService.validateAssigneeWorkloadCapacity(10L, 10));
        assertTrue(ex.getMessage().contains("Resource workload constraint parameters blocked"));
    }

    @Test
    void updateTaskStatus_validStatus_success() {
        when(projectTaskRepository.findById(100L)).thenReturn(Optional.of(sampleTask));
        when(projectTaskRepository.save(any())).thenReturn(sampleTask);

        ProjectTaskDto result = projectTaskService.updateTaskStatus(100L, "IN_PROGRESS");

        assertNotNull(result);
        verify(projectTaskRepository).save(any());
    }

    @Test
    void updateTaskStatus_invalidStatus_throwsUnauthorizedActionException() {
        assertThrows(UnauthorizedActionException.class,
                () -> projectTaskService.updateTaskStatus(100L, "INVALID_STATUS"));
    }

    @Test
    void updateTaskStatus_taskNotFound_throwsResourceNotFoundException() {
        when(projectTaskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectTaskService.updateTaskStatus(999L, "IN_PROGRESS"));
    }

    @Test
    void assignTask_success_transitionsToPending() {
        when(projectTaskRepository.findById(100L)).thenReturn(Optional.of(sampleTask));
        when(systemAccountRepository.findById(10L)).thenReturn(Optional.of(sampleAssignee));
        when(projectTaskRepository.calculateRemainingHoursForAssignee(10L)).thenReturn(5L);
        when(projectTaskRepository.save(any())).thenReturn(sampleTask);

        ProjectTaskDto result = projectTaskService.assignTask(100L, 10L);

        assertNotNull(result);
        verify(projectTaskRepository).save(any());
    }

    @Test
    void assignTask_taskNotFound_throwsResourceNotFoundException() {
        when(projectTaskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectTaskService.assignTask(999L, 10L));
    }

    @Test
    void listTasks_delegatesToRepository() {
        Page<ProjectTask> page = new PageImpl<>(List.of(sampleTask));
        when(projectTaskRepository.searchTasks(any(), any(), any(), any())).thenReturn(page);

        Page<ProjectTaskDto> result = projectTaskService.listTasks("", "ALL", null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteTask_success() {
        when(projectTaskRepository.existsById(100L)).thenReturn(true);

        projectTaskService.deleteTask(100L);

        verify(taskSubmissionRepository).deleteByTaskId(100L);
        verify(projectTaskRepository).deleteById(100L);
    }

    @Test
    void deleteTask_notFound_throwsResourceNotFoundException() {
        when(projectTaskRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> projectTaskService.deleteTask(999L));
    }

    @Test
    void updateTask_success() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .title("Updated Title").description("Updated desc")
                .priority("LOW").estimatedHours(10)
                .dueDate(LocalDate.now().plusDays(10)).status("IN_REVIEW").build();

        when(projectTaskRepository.findById(100L)).thenReturn(Optional.of(sampleTask));
        when(projectTaskRepository.save(any())).thenReturn(sampleTask);

        ProjectTaskDto result = projectTaskService.updateTask(100L, dto);

        assertNotNull(result);
        verify(projectTaskRepository).save(any());
    }

    @Test
    void updateTask_notFound_throwsResourceNotFoundException() {
        ProjectTaskDto dto = ProjectTaskDto.builder()
                .title("T").priority("LOW").estimatedHours(5)
                .dueDate(LocalDate.now().plusDays(1)).build();

        when(projectTaskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectTaskService.updateTask(999L, dto));
    }
}
