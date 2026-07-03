package com.example.demo.controller;

import com.example.demo.dto.SubmissionReviewDto;
import com.example.demo.dto.TaskSubmissionDto;
import com.example.demo.service.TaskSubmissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submissions")
public class TaskSubmissionController {

    private final TaskSubmissionService taskSubmissionService;

    public TaskSubmissionController(TaskSubmissionService taskSubmissionService) {
        this.taskSubmissionService = taskSubmissionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEAM_CONTRIBUTOR')")
    public ResponseEntity<TaskSubmissionDto> submitWork(@Valid @RequestBody TaskSubmissionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskSubmissionService.submitWork(dto));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<TaskSubmissionDto> reviewSubmission(
            @PathVariable Long id,
            @RequestBody SubmissionReviewDto reviewDto) {
        return ResponseEntity.ok(taskSubmissionService.reviewSubmission(id, reviewDto));
    }

    @GetMapping
    public ResponseEntity<Page<TaskSubmissionDto>> listSubmissions(
            @RequestParam(required = false) Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskSubmissionService.listSubmissionsByTask(taskId, PageRequest.of(page, size)));
    }
}
