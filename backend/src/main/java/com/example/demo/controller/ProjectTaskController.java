package com.example.demo.controller;

import com.example.demo.dto.ProjectTaskDto;
import com.example.demo.service.ProjectTaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    public ProjectTaskController(ProjectTaskService projectTaskService) {
        this.projectTaskService = projectTaskService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectTaskDto> spawnPipelineTask(@Valid @RequestBody ProjectTaskDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectTaskService.createTask(dto));
    }

    @GetMapping("/")
    public ResponseEntity<Page<ProjectTaskDto>> listPipelineTasks(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(projectTaskService.listTasks(query, status, assigneeId, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectTaskDto> updatePipelineTask(
            @PathVariable Long id,
            @Valid @RequestBody ProjectTaskDto dto) {
        return ResponseEntity.ok(projectTaskService.updateTask(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<Void> purgeTaskReference(@PathVariable Long id) {
        projectTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectTaskDto> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return ResponseEntity.ok(projectTaskService.updateTaskStatus(id, newStatus));
    }

    @PutMapping("/{id}/assign/{assigneeId}")
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectTaskDto> assignTask(
            @PathVariable Long id,
            @PathVariable Long assigneeId) {
        return ResponseEntity.ok(projectTaskService.assignTask(id, assigneeId));
    }
}
