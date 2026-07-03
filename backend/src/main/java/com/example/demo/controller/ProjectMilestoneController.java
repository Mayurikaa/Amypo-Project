package com.example.demo.controller;

import com.example.demo.dto.ProjectMilestoneDto;
import com.example.demo.service.ProjectMilestoneService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/milestones")
public class ProjectMilestoneController {

    private final ProjectMilestoneService projectMilestoneService;

    public ProjectMilestoneController(ProjectMilestoneService projectMilestoneService) {
        this.projectMilestoneService = projectMilestoneService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectMilestoneDto> createMilestone(@Valid @RequestBody ProjectMilestoneDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMilestoneService.createMilestone(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectMilestoneDto>> listMilestones(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long initiativeId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(projectMilestoneService.listMilestones(query, initiativeId, status, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectMilestoneDto> getMilestoneById(@PathVariable Long id) {
        return ResponseEntity.ok(projectMilestoneService.getMilestoneById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectMilestoneDto> updateMilestone(
            @PathVariable Long id,
            @Valid @RequestBody ProjectMilestoneDto dto) {
        return ResponseEntity.ok(projectMilestoneService.updateMilestone(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_DIRECTOR', 'PROJECT_MANAGER')")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
        projectMilestoneService.deleteMilestone(id);
        return ResponseEntity.noContent().build();
    }
}
