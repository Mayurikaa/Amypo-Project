package com.example.demo.service;

import com.example.demo.dto.SubmissionReviewDto;
import com.example.demo.dto.TaskSubmissionDto;
import com.example.demo.entity.ProjectTask;
import com.example.demo.entity.SystemAccount;
import com.example.demo.entity.TaskSubmission;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProjectTaskRepository;
import com.example.demo.repository.SystemAccountRepository;
import com.example.demo.repository.TaskSubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskSubmissionService {

    private final TaskSubmissionRepository taskSubmissionRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final SystemAccountRepository systemAccountRepository;

    public TaskSubmissionService(TaskSubmissionRepository taskSubmissionRepository,
                                  ProjectTaskRepository projectTaskRepository,
                                  SystemAccountRepository systemAccountRepository) {
        this.taskSubmissionRepository = taskSubmissionRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.systemAccountRepository = systemAccountRepository;
    }

    @Transactional
    public TaskSubmissionDto submitWork(TaskSubmissionDto dto) {
        ProjectTask task = projectTaskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + dto.getTaskId()));
        SystemAccount contributor = systemAccountRepository.findById(dto.getContributorId())
                .orElseThrow(() -> new ResourceNotFoundException("Contributor not found: " + dto.getContributorId()));
        TaskSubmission submission = TaskSubmission.builder()
                .task(task)
                .contributor(contributor)
                .hoursSpent(dto.getHoursSpent())
                .submissionNotes(dto.getSubmissionNotes())
                .completionStatus(dto.getCompletionStatus() != null ? dto.getCompletionStatus() : "PENDING_REVIEW")
                .build();
        return mapToDto(taskSubmissionRepository.save(submission));
    }

    @Transactional
    public TaskSubmissionDto reviewSubmission(Long id, SubmissionReviewDto reviewDto) {
        TaskSubmission submission = taskSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + id));
        submission.setReviewerFeedback(reviewDto.getReviewerFeedback());
        submission.setCompletionStatus(reviewDto.getCompletionStatus());
        return mapToDto(taskSubmissionRepository.save(submission));
    }

    public Page<TaskSubmissionDto> listSubmissionsByTask(Long taskId, Pageable pageable) {
        if (taskId != null) {
            return taskSubmissionRepository.findByTaskId(taskId, pageable).map(this::mapToDto);
        }
        return taskSubmissionRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public void deleteSubmission(Long id) {
        if (!taskSubmissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Submission not found: " + id);
        }
        taskSubmissionRepository.deleteById(id);
    }

    private TaskSubmissionDto mapToDto(TaskSubmission submission) {
        return TaskSubmissionDto.builder()
                .id(submission.getId())
                .taskId(submission.getTask().getId())
                .contributorId(submission.getContributor().getId())
                .hoursSpent(submission.getHoursSpent())
                .submissionNotes(submission.getSubmissionNotes())
                .reviewerFeedback(submission.getReviewerFeedback())
                .completionStatus(submission.getCompletionStatus())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
