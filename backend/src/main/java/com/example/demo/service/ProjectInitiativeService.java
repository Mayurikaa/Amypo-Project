package com.example.demo.service;

import com.example.demo.dto.PortfolioAnalyticsDto;
import com.example.demo.dto.ProjectInitiativeDto;
import com.example.demo.entity.ProjectInitiative;
import com.example.demo.entity.SystemAccount;
import com.example.demo.exception.CapacityExceededException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProjectInitiativeRepository;
import com.example.demo.repository.ProjectMilestoneRepository;
import com.example.demo.repository.ProjectTaskRepository;
import com.example.demo.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectInitiativeService {

    private final ProjectInitiativeRepository projectInitiativeRepository;
    private final ProjectMilestoneRepository projectMilestoneRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final SystemAccountRepository systemAccountRepository;

    @Transactional
    public ProjectInitiativeDto createInitiative(ProjectInitiativeDto dto) {
        if (projectInitiativeRepository.existsByProjectCode(dto.getProjectCode())) {
            throw new CapacityExceededException("Project code already exists: " + dto.getProjectCode());
        }
        SystemAccount director = systemAccountRepository.findById(dto.getDirectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Director not found: " + dto.getDirectorId()));
        ProjectInitiative initiative = ProjectInitiative.builder()
                .projectCode(dto.getProjectCode())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .budgetAllocated(dto.getBudgetAllocated())
                .startDate(dto.getStartDate())
                .targetEndDate(dto.getTargetEndDate())
                .director(director)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();
        return mapToDto(projectInitiativeRepository.save(initiative));
    }

    public PortfolioAnalyticsDto getPortfolioAnalytics() {
        long totalInitiatives = projectInitiativeRepository.count();
        long totalTasks = projectTaskRepository.count();
        Long activeTasks = projectTaskRepository.countActiveTasks();
        Long loggedHours = projectTaskRepository.sumTotalLoggedHours();

        List<SystemAccount> allAccounts = systemAccountRepository.findAll();
        Map<String, Long> domainDistribution = new HashMap<>();
        for (SystemAccount acc : allAccounts) {
            domainDistribution.merge(acc.getDomainRole(), 1L, Long::sum);
        }

        return PortfolioAnalyticsDto.builder()
                .totalInitiatives(totalInitiatives)
                .totalTasks(totalTasks)
                .activeTasksCount(activeTasks != null ? activeTasks : 0L)
                .totalHoursLogged(loggedHours != null ? loggedHours : 0L)
                .domainDistribution(domainDistribution)
                .build();
    }

    public Page<ProjectInitiativeDto> listInitiatives(String query, String status, Pageable pageable) {
        String q = (query == null) ? "" : query;
        String s = (status == null) ? "ALL" : status;
        return projectInitiativeRepository.searchInitiatives(q, s, pageable).map(this::mapToDto);
    }

    public ProjectInitiativeDto getInitiativeById(Long id) {
        ProjectInitiative initiative = projectInitiativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Initiative not found: " + id));
        return mapToDto(initiative);
    }

    @Transactional
    public ProjectInitiativeDto updateInitiative(Long id, ProjectInitiativeDto dto) {
        ProjectInitiative initiative = projectInitiativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Initiative not found: " + id));
        initiative.setTitle(dto.getTitle());
        initiative.setDescription(dto.getDescription());
        initiative.setBudgetAllocated(dto.getBudgetAllocated());
        initiative.setStartDate(dto.getStartDate());
        initiative.setTargetEndDate(dto.getTargetEndDate());
        initiative.setStatus(dto.getStatus());
        if (dto.getBudgetConsumed() != null) {
            initiative.setBudgetConsumed(dto.getBudgetConsumed());
        }
        return mapToDto(projectInitiativeRepository.save(initiative));
    }

    @Transactional
    public void deleteInitiative(Long id) {
        if (!projectInitiativeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Initiative not found: " + id);
        }
        projectTaskRepository.deleteByInitiativeId(id);
        projectMilestoneRepository.deleteByInitiativeId(id);
        projectInitiativeRepository.deleteById(id);
    }

    private ProjectInitiativeDto mapToDto(ProjectInitiative initiative) {
        return ProjectInitiativeDto.builder()
                .id(initiative.getId())
                .projectCode(initiative.getProjectCode())
                .title(initiative.getTitle())
                .description(initiative.getDescription())
                .budgetAllocated(initiative.getBudgetAllocated())
                .budgetConsumed(initiative.getBudgetConsumed())
                .startDate(initiative.getStartDate())
                .targetEndDate(initiative.getTargetEndDate())
                .directorId(initiative.getDirector().getId())
                .status(initiative.getStatus())
                .build();
    }
}
