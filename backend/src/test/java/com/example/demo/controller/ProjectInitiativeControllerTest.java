package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.PortfolioAnalyticsDto;
import com.example.demo.dto.ProjectInitiativeDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProjectInitiativeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectInitiativeController.class)
class ProjectInitiativeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectInitiativeService projectInitiativeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper;
    private ProjectInitiativeDto sampleDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleDto = ProjectInitiativeDto.builder()
                .id(1L).projectCode("PRJ-001").title("Initiative One")
                .budgetAllocated(BigDecimal.valueOf(10000))
                .budgetConsumed(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plusMonths(3))
                .directorId(1L).status("ACTIVE").build();
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void createInitiative_success_returns201() throws Exception {
        when(projectInitiativeService.createInitiative(any())).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/initiatives")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectCode").value("PRJ-001"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void getInitiativeById_found_returns200() throws Exception {
        when(projectInitiativeService.getInitiativeById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/initiatives/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void getInitiativeById_notFound_returns404() throws Exception {
        when(projectInitiativeService.getInitiativeById(999L))
                .thenThrow(new ResourceNotFoundException("Initiative not found: 999"));

        mockMvc.perform(get("/api/v1/initiatives/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void listInitiatives_returns200() throws Exception {
        Page<ProjectInitiativeDto> page = new PageImpl<>(List.of(sampleDto));
        when(projectInitiativeService.listInitiatives(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/initiatives")
                        .param("status", "ALL").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].projectCode").value("PRJ-001"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void getAnalytics_returns200() throws Exception {
        PortfolioAnalyticsDto analytics = PortfolioAnalyticsDto.builder()
                .totalInitiatives(5L).totalTasks(20L)
                .activeTasksCount(10L).totalHoursLogged(100L)
                .domainDistribution(Map.of("PROJECT_DIRECTOR", 1L)).build();

        when(projectInitiativeService.getPortfolioAnalytics()).thenReturn(analytics);

        mockMvc.perform(get("/api/v1/initiatives/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInitiatives").value(5));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void updateInitiative_success_returns200() throws Exception {
        when(projectInitiativeService.updateInitiative(eq(1L), any())).thenReturn(sampleDto);

        mockMvc.perform(put("/api/v1/initiatives/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void deleteInitiative_success_returns204() throws Exception {
        doNothing().when(projectInitiativeService).deleteInitiative(1L);

        mockMvc.perform(delete("/api/v1/initiatives/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void deleteInitiative_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Initiative not found: 999"))
                .when(projectInitiativeService).deleteInitiative(999L);

        mockMvc.perform(delete("/api/v1/initiatives/999").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
