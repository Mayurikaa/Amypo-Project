package com.example.demo.service;

import com.example.demo.dto.ProjectMilestoneDto;
import com.example.demo.entity.ProjectInitiative;
import com.example.demo.entity.ProjectMilestone;
import com.example.demo.entity.SystemAccount;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProjectInitiativeRepository;
import com.example.demo.repository.ProjectMilestoneRepository;
import com.example.demo.repository.ProjectTaskRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMilestoneServiceTest {

    @Mock private ProjectMilestoneRepository projectMilestoneRepository;
    @Mock private ProjectInitiativeRepository projectInitiativeRepository;
    @Mock private ProjectTaskRepository projectTaskRepository;

    @InjectMocks
    private ProjectMilestoneService projectMilestoneService;

    private ProjectInitiative sampleInitiative;
    private ProjectMilestone sampleMilestone;

    @BeforeEach
    void setUp() {
        SystemAccount director = SystemAccount.builder()
                .id(1L).email("director@example.com")
                .fullName("Director").domainRole("PROJECT_DIRECTOR").isActive(true).build();

        sampleInitiative = ProjectInitiative.builder()
                .id(1L).projectCode("PRJ-001").title("Initiative One")
                .budgetAllocated(BigDecimal.valueOf(10000))
                .budgetConsumed(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(3))
                .director(director).status("ACTIVE").build();

        sampleMilestone = ProjectMilestone.builder()
                .id(5L).initiative(sampleInitiative)
                .title("Milestone One").targetDate(LocalDate.now().plusMonths(1))
                .allocatedHours(40).status("ACTIVE").build();
    }

    @Test
    void createMilestone_success() {
        ProjectMilestoneDto dto = ProjectMilestoneDto.builder()
                .initiativeId(1L).title("New Milestone")
                .targetDate(LocalDate.now().plusMonths(2))
                .allocatedHours(20).status("ACTIVE").build();

        when(projectInitiativeRepository.findById(1L)).thenReturn(Optional.of(sampleInitiative));
        when(projectMilestoneRepository.save(any())).thenReturn(sampleMilestone);

        ProjectMilestoneDto result = projectMilestoneService.createMilestone(dto);

        assertNotNull(result);
        verify(projectMilestoneRepository).save(any());
    }

    @Test
    void createMilestone_initiativeNotFound_throwsResourceNotFoundException() {
        ProjectMilestoneDto dto = ProjectMilestoneDto.builder()
                .initiativeId(999L).title("Milestone")
                .targetDate(LocalDate.now().plusMonths(1))
                .allocatedHours(10).status("ACTIVE").build();

        when(projectInitiativeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectMilestoneService.createMilestone(dto));
    }

    @Test
    void getMilestoneById_found_returnsDto() {
        when(projectMilestoneRepository.findById(5L)).thenReturn(Optional.of(sampleMilestone));

        ProjectMilestoneDto result = projectMilestoneService.getMilestoneById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Milestone One", result.getTitle());
    }

    @Test
    void getMilestoneById_notFound_throwsResourceNotFoundException() {
        when(projectMilestoneRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectMilestoneService.getMilestoneById(999L));
    }

    @Test
    void listMilestones_returnsPage() {
        Page<ProjectMilestone> page = new PageImpl<>(List.of(sampleMilestone));
        when(projectMilestoneRepository.searchMilestones(any(), any(), any(), any())).thenReturn(page);

        Page<ProjectMilestoneDto> result = projectMilestoneService.listMilestones("", 1L, "ALL", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateMilestone_success() {
        ProjectMilestoneDto dto = ProjectMilestoneDto.builder()
                .title("Updated Milestone").targetDate(LocalDate.now().plusMonths(2))
                .allocatedHours(30).status("COMPLETED").build();

        when(projectMilestoneRepository.findById(5L)).thenReturn(Optional.of(sampleMilestone));
        when(projectMilestoneRepository.save(any())).thenReturn(sampleMilestone);

        ProjectMilestoneDto result = projectMilestoneService.updateMilestone(5L, dto);

        assertNotNull(result);
        verify(projectMilestoneRepository).save(any());
    }

    @Test
    void updateMilestone_notFound_throwsResourceNotFoundException() {
        ProjectMilestoneDto dto = ProjectMilestoneDto.builder()
                .title("T").targetDate(LocalDate.now().plusDays(1))
                .allocatedHours(5).status("ACTIVE").build();

        when(projectMilestoneRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectMilestoneService.updateMilestone(999L, dto));
    }

    @Test
    void deleteMilestone_success() {
        when(projectMilestoneRepository.existsById(5L)).thenReturn(true);

        projectMilestoneService.deleteMilestone(5L);

        verify(projectTaskRepository).deleteByMilestoneId(5L);
        verify(projectMilestoneRepository).deleteById(5L);
    }

    @Test
    void deleteMilestone_notFound_throwsResourceNotFoundException() {
        when(projectMilestoneRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> projectMilestoneService.deleteMilestone(999L));
    }
}
