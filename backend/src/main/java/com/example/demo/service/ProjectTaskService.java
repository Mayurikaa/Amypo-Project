package com.example.demo.service;

import com.example.demo.dto.ProjectTaskDto;
import com.example.demo.entity.ProjectInitiative;
import com.example.demo.entity.ProjectMilestone;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectInitiativeRepository projectInitiativeRepository;
    private final ProjectMilestoneRepository projectMilestoneRepository;
    private final SystemAccountRepository systemAccountRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;

    private static final List<String> VALID_STATUSES = List.of(
            "PENDING", "IN_PROGRESS", "IN_REVIEW", "COMPLETED"
    );

    @Transactional
    public ProjectTaskDto createTask(ProjectTaskDto dto) {
        if (projectTaskRepository.findByTaskCode(dto.getTaskCode()).isPresent()) {
            throw new CapacityExceededException(
                    "Task Code parameter matching collision: Code reference already maps to active pipeline entry.");
        }
        ProjectInitiative initiative = projectInitiativeRepository.findById(dto.getInitiativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Initiative not found: " + dto.getInitiativeId()));

        ProjectMilestone milestone = null;
        if (dto.getMilestoneId() != null) {
            milestone = projectMilestoneRepository.findById(dto.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + dto.getMilestoneId()));
        }

        SystemAccount assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = systemAccountRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found: " + dto.getAssigneeId()));
            validateAssigneeWorkloadCapacity(dto.getAssigneeId(), dto.getEstimatedHours());
        }

        ProjectTask task = ProjectTask.builder()
                .taskCode(dto.getTaskCode())
                .initiative(initiative)
                .milestone(milestone)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority().toUpperCase())
                .estimatedHours(dto.getEstimatedHours())
                .dueDate(dto.getDueDate())
                .assignee(assignee)
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .build();
        return mapToDto(projectTaskRepository.save(task));
    }

    public void validateAssigneeWorkloadCapacity(Long assigneeId, Integer upcomingHours) {
        Long existingLoad = projectTaskRepository.calculateRemainingHoursForAssignee(assigneeId);
        if (existingLoad == null) existingLoad = 0L;
        if (existingLoad + upcomingHours > 40) {
            throw new CapacityExceededException(
                    "Resource workload constraint parameters blocked: Requested assignment exceeds " +
                    "candidate's active maximum continuous operational limit of 40 weekly hours.");
        }
    }

    @Transactional
    public ProjectTaskDto updateTaskStatus(Long taskId, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new UnauthorizedActionException("Invalid status transition: " + newStatus);
        }
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        task.setStatus(newStatus);
        return mapToDto(projectTaskRepository.save(task));
    }

    @Transactional
    public ProjectTaskDto assignTask(Long taskId, Long assigneeId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        SystemAccount assignee = systemAccountRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found: " + assigneeId));
        int remainingHours = task.getEstimatedHours() - task.getLoggedHours();
        validateAssigneeWorkloadCapacity(assigneeId, remainingHours);
        task.setAssignee(assignee);
        if ("PENDING".equals(task.getStatus())) {
            task.setStatus("IN_PROGRESS");
        }
        return mapToDto(projectTaskRepository.save(task));
    }

    public Page<ProjectTaskDto> listTasks(String query, String status, Long assigneeId, Pageable pageable) {
        String q = (query == null) ? "" : query;
        String s = (status == null) ? "ALL" : status;
        return projectTaskRepository.searchTasks(q, s, assigneeId, pageable).map(this::mapToDto);
    }

    @Transactional
    public ProjectTaskDto updateTask(Long id, ProjectTaskDto dto) {
        ProjectTask task = projectTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority().toUpperCase());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setDueDate(dto.getDueDate());
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        return mapToDto(projectTaskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!projectTaskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found: " + id);
        }
        taskSubmissionRepository.deleteByTaskId(id);
        projectTaskRepository.deleteById(id);
    }

    public ProjectTaskDto getTaskById(Long id) {
        ProjectTask task = projectTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        return mapToDto(task);
    }

    private ProjectTaskDto mapToDto(ProjectTask task) {
        return ProjectTaskDto.builder()
                .id(task.getId())
                .taskCode(task.getTaskCode())
                .initiativeId(task.getInitiative().getId())
                .milestoneId(task.getMilestone() != null ? task.getMilestone().getId() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .estimatedHours(task.getEstimatedHours())
                .loggedHours(task.getLoggedHours())
                .dueDate(task.getDueDate())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .status(task.getStatus())
                .build();
    }
}
