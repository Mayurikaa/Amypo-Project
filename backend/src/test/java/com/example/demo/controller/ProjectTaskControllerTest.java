package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.ProjectTaskDto;
import com.example.demo.exception.CapacityExceededException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProjectTaskService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectTaskController.class)
class ProjectTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectTaskService projectTaskService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper;
    private ProjectTaskDto sampleTaskDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleTaskDto = ProjectTaskDto.builder()
                .id(1L).taskCode("TASK-001").initiativeId(1L)
                .title("Sample Task").priority("HIGH").estimatedHours(8)
                .loggedHours(0).dueDate(LocalDate.now().plusDays(7))
                .status("PENDING").build();
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void spawnPipelineTask_success_returns201() throws Exception {
        ProjectTaskDto requestDto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(1L).title("Sample Task")
                .priority("HIGH").estimatedHours(8)
                .dueDate(LocalDate.now().plusDays(7)).build();

        when(projectTaskService.createTask(any())).thenReturn(sampleTaskDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskCode").value("TASK-001"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void spawnPipelineTask_duplicateCode_returns400() throws Exception {
        ProjectTaskDto requestDto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(1L).title("Sample Task")
                .priority("HIGH").estimatedHours(8)
                .dueDate(LocalDate.now().plusDays(7)).build();

        when(projectTaskService.createTask(any()))
                .thenThrow(new CapacityExceededException(
                        "Task Code parameter matching collision: Code reference already maps to active pipeline entry."));

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void spawnPipelineTask_capacityExceeded_returns400() throws Exception {
        ProjectTaskDto requestDto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(1L).title("Sample Task")
                .priority("HIGH").estimatedHours(8).assigneeId(10L)
                .dueDate(LocalDate.now().plusDays(7)).build();

        when(projectTaskService.createTask(any()))
                .thenThrow(new CapacityExceededException(
                        "Resource workload constraint parameters blocked: Requested assignment exceeds " +
                        "candidate's active maximum continuous operational limit of 40 weekly hours."));

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void spawnPipelineTask_initiativeNotFound_returns404() throws Exception {
        ProjectTaskDto requestDto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(999L).title("Sample Task")
                .priority("HIGH").estimatedHours(8)
                .dueDate(LocalDate.now().plusDays(7)).build();

        when(projectTaskService.createTask(any()))
                .thenThrow(new ResourceNotFoundException("Initiative not found: 999"));

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "TEAM_CONTRIBUTOR")
    void listPipelineTasks_success_returns200() throws Exception {
        Page<ProjectTaskDto> page = new PageImpl<>(List.of(sampleTaskDto));
        when(projectTaskService.listTasks(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks/")
                        .param("status", "ALL")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].taskCode").value("TASK-001"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void updatePipelineTask_success_returns200() throws Exception {
        ProjectTaskDto requestDto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(1L).title("Updated Task")
                .priority("MEDIUM").estimatedHours(6)
                .dueDate(LocalDate.now().plusDays(10)).build();

        when(projectTaskService.updateTask(eq(1L), any())).thenReturn(sampleTaskDto);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void updatePipelineTask_notFound_returns404() throws Exception {
        ProjectTaskDto requestDto = ProjectTaskDto.builder()
                .taskCode("TASK-001").initiativeId(1L).title("Updated Task")
                .priority("MEDIUM").estimatedHours(6)
                .dueDate(LocalDate.now().plusDays(10)).build();

        when(projectTaskService.updateTask(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Task not found: 999"));

        mockMvc.perform(put("/api/v1/tasks/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void purgeTaskReference_success_returns204() throws Exception {
        doNothing().when(projectTaskService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/tasks/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(projectTaskService).deleteTask(1L);
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void purgeTaskReference_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found: 999"))
                .when(projectTaskService).deleteTask(999L);

        mockMvc.perform(delete("/api/v1/tasks/999").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void updateTaskStatus_validStatus_returns200() throws Exception {
        when(projectTaskService.updateTaskStatus(eq(1L), eq("IN_PROGRESS"))).thenReturn(sampleTaskDto);

        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "IN_PROGRESS"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void assignTask_success_returns200() throws Exception {
        when(projectTaskService.assignTask(1L, 10L)).thenReturn(sampleTaskDto);

        mockMvc.perform(put("/api/v1/tasks/1/assign/10").with(csrf()))
                .andExpect(status().isOk());
    }
}
