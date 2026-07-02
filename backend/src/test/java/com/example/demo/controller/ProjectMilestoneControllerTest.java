package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.ProjectMilestoneDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProjectMilestoneService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectMilestoneController.class)
class ProjectMilestoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectMilestoneService projectMilestoneService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper;
    private ProjectMilestoneDto sampleDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleDto = ProjectMilestoneDto.builder()
                .id(1L).initiativeId(1L).title("Milestone One")
                .targetDate(LocalDate.now().plusMonths(1))
                .allocatedHours(40).status("ACTIVE").build();
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void createMilestone_success_returns201() throws Exception {
        when(projectMilestoneService.createMilestone(any())).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/milestones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Milestone One"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void createMilestone_initiativeNotFound_returns404() throws Exception {
        when(projectMilestoneService.createMilestone(any()))
                .thenThrow(new ResourceNotFoundException("Initiative not found: 999"));

        mockMvc.perform(post("/api/v1/milestones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void listMilestones_returns200() throws Exception {
        Page<ProjectMilestoneDto> page = new PageImpl<>(List.of(sampleDto));
        when(projectMilestoneService.listMilestones(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/milestones")
                        .param("status", "ALL").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Milestone One"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void updateMilestone_success_returns200() throws Exception {
        when(projectMilestoneService.updateMilestone(eq(1L), any())).thenReturn(sampleDto);

        mockMvc.perform(put("/api/v1/milestones/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void deleteMilestone_success_returns204() throws Exception {
        doNothing().when(projectMilestoneService).deleteMilestone(1L);

        mockMvc.perform(delete("/api/v1/milestones/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void deleteMilestone_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Milestone not found: 999"))
                .when(projectMilestoneService).deleteMilestone(999L);

        mockMvc.perform(delete("/api/v1/milestones/999").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
