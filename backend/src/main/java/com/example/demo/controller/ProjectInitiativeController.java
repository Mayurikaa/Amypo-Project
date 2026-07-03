package com.example.demo.controller;

import com.example.demo.dto.PortfolioAnalyticsDto;
import com.example.demo.dto.ProjectInitiativeDto;
import com.example.demo.service.ProjectInitiativeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/initiatives")
public class ProjectInitiativeController {

    private final ProjectInitiativeService projectInitiativeService;

    public ProjectInitiativeController(ProjectInitiativeService projectInitiativeService) {
        this.projectInitiativeService = projectInitiativeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<ProjectInitiativeDto> createInitiative(@Valid @RequestBody ProjectInitiativeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectInitiativeService.createInitiative(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectInitiativeDto> getInitiativeById(@PathVariable Long id) {
        return ResponseEntity.ok(projectInitiativeService.getInitiativeById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectInitiativeDto>> listInitiatives(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(projectInitiativeService.listInitiatives(query, status, PageRequest.of(page, size)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<PortfolioAnalyticsDto> getAnalytics() {
        return ResponseEntity.ok(projectInitiativeService.getPortfolioAnalytics());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<ProjectInitiativeDto> updateInitiative(
            @PathVariable Long id,
            @Valid @RequestBody ProjectInitiativeDto dto) {
        return ResponseEntity.ok(projectInitiativeService.updateInitiative(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<Void> deleteInitiative(@PathVariable Long id) {
        projectInitiativeService.deleteInitiative(id);
        return ResponseEntity.noContent().build();
    }
}
