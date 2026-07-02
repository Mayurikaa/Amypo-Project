package com.example.demo.service;

import com.example.demo.dto.SubmissionReviewDto;
import com.example.demo.dto.TaskSubmissionDto;
import com.example.demo.entity.ProjectInitiative;
import com.example.demo.entity.ProjectTask;
import com.example.demo.entity.SystemAccount;
import com.example.demo.entity.TaskSubmission;
import com.example.demo.exception.ResourceNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSubmissionServiceTest {

    @Mock private TaskSubmissionRepository taskSubmissionRepository;
    @Mock private ProjectTaskRepository projectTaskRepository;
    @Mock private SystemAccountRepository systemAccountRepository;

    @InjectMocks
    private TaskSubmissionService taskSubmissionService;

    private ProjectTask sampleTask;
    private SystemAccount sampleContributor;
    private TaskSubmission sampleSubmission;

    @BeforeEach
    void setUp() {
        SystemAccount director = SystemAccount.builder()
                .id(1L).email("director@example.com")
                .fullName("Director").domainRole("PROJECT_DIRECTOR").isActive(true).build();

        ProjectInitiative initiative = ProjectInitiative.builder()
                .id(1L).projectCode("PRJ-001").title("Initiative")
                .budgetAllocated(BigDecimal.valueOf(10000))
                .budgetConsumed(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(3))
                .director(director).status("ACTIVE").build();

        sampleTask = ProjectTask.builder()
                .id(10L).taskCode("TASK-001").initiative(initiative)
                .title("Task One").priority("HIGH").estimatedHours(8)
                .loggedHours(0).dueDate(LocalDate.now().plusDays(7))
                .status("IN_PROGRESS").build();

        sampleContributor = SystemAccount.builder()
                .id(5L).email("contrib@example.com")
                .fullName("Contributor").domainRole("TEAM_CONTRIBUTOR").isActive(true).build();

        sampleSubmission = TaskSubmission.builder()
                .id(100L).task(sampleTask).contributor(sampleContributor)
                .hoursSpent(4).submissionNotes("Done half")
                .completionStatus("PENDING_REVIEW")
                .submittedAt(LocalDateTime.now()).build();
    }

    @Test
    void submitWork_success() {
        TaskSubmissionDto dto = TaskSubmissionDto.builder()
                .taskId(10L).contributorId(5L).hoursSpent(4)
                .submissionNotes("Work done").completionStatus("PENDING_REVIEW").build();

        when(projectTaskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
        when(systemAccountRepository.findById(5L)).thenReturn(Optional.of(sampleContributor));
        when(taskSubmissionRepository.save(any())).thenReturn(sampleSubmission);

        TaskSubmissionDto result = taskSubmissionService.submitWork(dto);

        assertNotNull(result);
        verify(taskSubmissionRepository).save(any());
    }

    @Test
    void submitWork_taskNotFound_throwsResourceNotFoundException() {
        TaskSubmissionDto dto = TaskSubmissionDto.builder()
                .taskId(999L).contributorId(5L).hoursSpent(4)
                .submissionNotes("Work").completionStatus("PENDING_REVIEW").build();

        when(projectTaskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskSubmissionService.submitWork(dto));
    }

    @Test
    void submitWork_contributorNotFound_throwsResourceNotFoundException() {
        TaskSubmissionDto dto = TaskSubmissionDto.builder()
                .taskId(10L).contributorId(999L).hoursSpent(4)
                .submissionNotes("Work").completionStatus("PENDING_REVIEW").build();

        when(projectTaskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
        when(systemAccountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskSubmissionService.submitWork(dto));
    }

    @Test
    void reviewSubmission_success() {
        SubmissionReviewDto reviewDto = new SubmissionReviewDto("Good work", "APPROVED");

        when(taskSubmissionRepository.findById(100L)).thenReturn(Optional.of(sampleSubmission));
        when(taskSubmissionRepository.save(any())).thenReturn(sampleSubmission);

        TaskSubmissionDto result = taskSubmissionService.reviewSubmission(100L, reviewDto);

        assertNotNull(result);
        verify(taskSubmissionRepository).save(any());
    }

    @Test
    void reviewSubmission_notFound_throwsResourceNotFoundException() {
        SubmissionReviewDto reviewDto = new SubmissionReviewDto("Feedback", "APPROVED");

        when(taskSubmissionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskSubmissionService.reviewSubmission(999L, reviewDto));
    }

    @Test
    void listSubmissionsByTask_withTaskId_returnsFilteredPage() {
        Page<TaskSubmission> page = new PageImpl<>(List.of(sampleSubmission));
        when(taskSubmissionRepository.findByTaskId(eq(10L), any())).thenReturn(page);

        Page<TaskSubmissionDto> result = taskSubmissionService.listSubmissionsByTask(10L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listSubmissionsByTask_withoutTaskId_returnsAllPage() {
        Page<TaskSubmission> page = new PageImpl<>(List.of(sampleSubmission));
        when(taskSubmissionRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<TaskSubmissionDto> result = taskSubmissionService.listSubmissionsByTask(null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteSubmission_success() {
        when(taskSubmissionRepository.existsById(100L)).thenReturn(true);

        taskSubmissionService.deleteSubmission(100L);

        verify(taskSubmissionRepository).deleteById(100L);
    }

    @Test
    void deleteSubmission_notFound_throwsResourceNotFoundException() {
        when(taskSubmissionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> taskSubmissionService.deleteSubmission(999L));
    }
}
