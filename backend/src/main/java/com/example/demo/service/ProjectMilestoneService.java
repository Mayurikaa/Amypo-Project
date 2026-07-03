package com.example.demo.service;

import com.example.demo.dto.ProjectMilestoneDto;
import com.example.demo.entity.ProjectInitiative;
import com.example.demo.entity.ProjectMilestone;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProjectInitiativeRepository;
import com.example.demo.repository.ProjectMilestoneRepository;
import com.example.demo.repository.ProjectTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectMilestoneService {

    private final ProjectMilestoneRepository projectMilestoneRepository;
    private final ProjectInitiativeRepository projectInitiativeRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public ProjectMilestoneService(ProjectMilestoneRepository projectMilestoneRepository,
                                    ProjectInitiativeRepository projectInitiativeRepository,
                                    ProjectTaskRepository projectTaskRepository) {
        this.projectMilestoneRepository = projectMilestoneRepository;
        this.projectInitiativeRepository = projectInitiativeRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    @Transactional
    public ProjectMilestoneDto createMilestone(ProjectMilestoneDto dto) {
        ProjectInitiative initiative = projectInitiativeRepository.findById(dto.getInitiativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Initiative not found: " + dto.getInitiativeId()));
        ProjectMilestone milestone = ProjectMilestone.builder()
                .initiative(initiative)
                .title(dto.getTitle())
                .targetDate(dto.getTargetDate())
                .allocatedHours(dto.getAllocatedHours())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();
        return mapToDto(projectMilestoneRepository.save(milestone));
    }

    public Page<ProjectMilestoneDto> listMilestones(String query, Long initiativeId, String status, Pageable pageable) {
        String q = (query == null) ? "" : query;
        String s = (status == null) ? "ALL" : status;
        return projectMilestoneRepository.searchMilestones(q, initiativeId, s, pageable).map(this::mapToDto);
    }

    public ProjectMilestoneDto getMilestoneById(Long id) {
        ProjectMilestone milestone = projectMilestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + id));
        return mapToDto(milestone);
    }

    @Transactional
    public ProjectMilestoneDto updateMilestone(Long id, ProjectMilestoneDto dto) {
        ProjectMilestone milestone = projectMilestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + id));
        milestone.setTitle(dto.getTitle());
        milestone.setTargetDate(dto.getTargetDate());
        milestone.setAllocatedHours(dto.getAllocatedHours());
        milestone.setStatus(dto.getStatus());
        return mapToDto(projectMilestoneRepository.save(milestone));
    }

    @Transactional
    public void deleteMilestone(Long id) {
        if (!projectMilestoneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Milestone not found: " + id);
        }
        projectTaskRepository.deleteByMilestoneId(id);
        projectMilestoneRepository.deleteById(id);
    }

    private ProjectMilestoneDto mapToDto(ProjectMilestone milestone) {
        return ProjectMilestoneDto.builder()
                .id(milestone.getId())
                .initiativeId(milestone.getInitiative().getId())
                .title(milestone.getTitle())
                .targetDate(milestone.getTargetDate())
                .allocatedHours(milestone.getAllocatedHours())
                .status(milestone.getStatus())
                .build();
    }
}
