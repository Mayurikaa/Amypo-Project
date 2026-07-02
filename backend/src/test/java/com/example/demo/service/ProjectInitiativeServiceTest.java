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
class ProjectInitiativeServiceTest {

    @Mock private ProjectInitiativeRepository projectInitiativeRepository;
    @Mock private ProjectMilestoneRepository projectMilestoneRepository;
    @Mock private ProjectTaskRepository projectTaskRepository;
    @Mock private SystemAccountRepository systemAccountRepository;

    @InjectMocks
    private ProjectInitiativeService projectInitiativeService;

    private SystemAccount director;
    private ProjectInitiative sampleInitiative;

    @BeforeEach
    void setUp() {
        director = SystemAccount.builder()
                .id(1L).email("director@example.com")
                .fullName("Director").domainRole("PROJECT_DIRECTOR").isActive(true).build();

        sampleInitiative = ProjectInitiative.builder()
                .id(10L).projectCode("PRJ-001").title("Initiative One")
                .description("Desc").budgetAllocated(BigDecimal.valueOf(10000))
                .budgetConsumed(BigDecimal.ZERO).startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(3))
                .director(director).status("ACTIVE").build();
    }

    @Test
    void createInitiative_success() {
        ProjectInitiativeDto dto = ProjectInitiativeDto.builder()
                .projectCode("PRJ-002").title("New Initiative")
                .budgetAllocated(BigDecimal.valueOf(5000))
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(2))
                .directorId(1L).status("ACTIVE").build();

        when(projectInitiativeRepository.existsByProjectCode("PRJ-002")).thenReturn(false);
        when(systemAccountRepository.findById(1L)).thenReturn(Optional.of(director));
        when(projectInitiativeRepository.save(any())).thenReturn(sampleInitiative);

        ProjectInitiativeDto result = projectInitiativeService.createInitiative(dto);

        assertNotNull(result);
        verify(projectInitiativeRepository).save(any());
    }

    @Test
    void createInitiative_duplicateCode_throwsCapacityExceededException() {
        ProjectInitiativeDto dto = ProjectInitiativeDto.builder()
                .projectCode("PRJ-001").title("Duplicate")
                .budgetAllocated(BigDecimal.valueOf(1000))
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(1))
                .directorId(1L).status("ACTIVE").build();

        when(projectInitiativeRepository.existsByProjectCode("PRJ-001")).thenReturn(true);

        assertThrows(CapacityExceededException.class,
                () -> projectInitiativeService.createInitiative(dto));
    }

    @Test
    void createInitiative_directorNotFound_throwsResourceNotFoundException() {
        ProjectInitiativeDto dto = ProjectInitiativeDto.builder()
                .projectCode("PRJ-NEW").title("New")
                .budgetAllocated(BigDecimal.valueOf(1000))
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(1))
                .directorId(999L).status("ACTIVE").build();

        when(projectInitiativeRepository.existsByProjectCode("PRJ-NEW")).thenReturn(false);
        when(systemAccountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectInitiativeService.createInitiative(dto));
    }

    @Test
    void getInitiativeById_found_returnsDto() {
        when(projectInitiativeRepository.findById(10L)).thenReturn(Optional.of(sampleInitiative));

        ProjectInitiativeDto result = projectInitiativeService.getInitiativeById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("PRJ-001", result.getProjectCode());
    }

    @Test
    void getInitiativeById_notFound_throwsResourceNotFoundException() {
        when(projectInitiativeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectInitiativeService.getInitiativeById(999L));
    }

    @Test
    void listInitiatives_returnsPage() {
        Page<ProjectInitiative> page = new PageImpl<>(List.of(sampleInitiative));
        when(projectInitiativeRepository.searchInitiatives(any(), any(), any())).thenReturn(page);

        Page<ProjectInitiativeDto> result = projectInitiativeService.listInitiatives("", "ALL", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateInitiative_success() {
        ProjectInitiativeDto dto = ProjectInitiativeDto.builder()
                .title("Updated Title").description("Updated Desc")
                .budgetAllocated(BigDecimal.valueOf(20000))
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(6))
                .status("ON_HOLD").build();

        when(projectInitiativeRepository.findById(10L)).thenReturn(Optional.of(sampleInitiative));
        when(projectInitiativeRepository.save(any())).thenReturn(sampleInitiative);

        ProjectInitiativeDto result = projectInitiativeService.updateInitiative(10L, dto);

        assertNotNull(result);
        verify(projectInitiativeRepository).save(any());
    }

    @Test
    void updateInitiative_notFound_throwsResourceNotFoundException() {
        ProjectInitiativeDto dto = ProjectInitiativeDto.builder()
                .title("T").budgetAllocated(BigDecimal.ONE)
                .startDate(LocalDate.now()).targetEndDate(LocalDate.now().plusDays(1))
                .status("ACTIVE").build();

        when(projectInitiativeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectInitiativeService.updateInitiative(999L, dto));
    }

    @Test
    void deleteInitiative_success() {
        when(projectInitiativeRepository.existsById(10L)).thenReturn(true);

        projectInitiativeService.deleteInitiative(10L);

        verify(projectTaskRepository).deleteByInitiativeId(10L);
        verify(projectMilestoneRepository).deleteByInitiativeId(10L);
        verify(projectInitiativeRepository).deleteById(10L);
    }

    @Test
    void deleteInitiative_notFound_throwsResourceNotFoundException() {
        when(projectInitiativeRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> projectInitiativeService.deleteInitiative(999L));
    }

    @Test
    void getPortfolioAnalytics_returnsDto() {
        when(projectInitiativeRepository.count()).thenReturn(5L);
        when(projectTaskRepository.count()).thenReturn(20L);
        when(projectTaskRepository.countActiveTasks()).thenReturn(10L);
        when(projectTaskRepository.sumTotalLoggedHours()).thenReturn(100L);
        when(systemAccountRepository.findAll()).thenReturn(List.of(director));

        PortfolioAnalyticsDto result = projectInitiativeService.getPortfolioAnalytics();

        assertNotNull(result);
        assertEquals(5L, result.getTotalInitiatives());
        assertEquals(20L, result.getTotalTasks());
        assertEquals(10L, result.getActiveTasksCount());
        assertEquals(100L, result.getTotalHoursLogged());
    }
}
