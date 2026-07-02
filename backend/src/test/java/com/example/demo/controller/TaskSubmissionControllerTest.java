package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.SubmissionReviewDto;
import com.example.demo.dto.TaskSubmissionDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.TaskSubmissionService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskSubmissionController.class)
class TaskSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskSubmissionService taskSubmissionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper;
    private TaskSubmissionDto sampleDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleDto = TaskSubmissionDto.builder()
                .id(1L).taskId(10L).contributorId(5L)
                .hoursSpent(4).submissionNotes("Work done")
                .completionStatus("PENDING_REVIEW")
                .submittedAt(LocalDateTime.now()).build();
    }

    @Test
    @WithMockUser(roles = "TEAM_CONTRIBUTOR")
    void submitWork_success_returns201() throws Exception {
        TaskSubmissionDto request = TaskSubmissionDto.builder()
                .taskId(10L).contributorId(5L).hoursSpent(4)
                .submissionNotes("Work done").completionStatus("PENDING_REVIEW").build();

        when(taskSubmissionService.submitWork(any())).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/submissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.completionStatus").value("PENDING_REVIEW"));
    }

    @Test
    @WithMockUser(roles = "TEAM_CONTRIBUTOR")
    void submitWork_taskNotFound_returns404() throws Exception {
        TaskSubmissionDto request = TaskSubmissionDto.builder()
                .taskId(999L).contributorId(5L).hoursSpent(4)
                .submissionNotes("Work done").completionStatus("PENDING_REVIEW").build();

        when(taskSubmissionService.submitWork(any()))
                .thenThrow(new ResourceNotFoundException("Task not found: 999"));

        mockMvc.perform(post("/api/v1/submissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void reviewSubmission_success_returns200() throws Exception {
        SubmissionReviewDto reviewDto = new SubmissionReviewDto("Great work", "APPROVED");

        when(taskSubmissionService.reviewSubmission(eq(1L), any())).thenReturn(sampleDto);

        mockMvc.perform(put("/api/v1/submissions/1/review")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void reviewSubmission_notFound_returns404() throws Exception {
        SubmissionReviewDto reviewDto = new SubmissionReviewDto("Feedback", "REJECTED");

        when(taskSubmissionService.reviewSubmission(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Submission not found: 999"));

        mockMvc.perform(put("/api/v1/submissions/999/review")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void listSubmissions_withTaskId_returns200() throws Exception {
        Page<TaskSubmissionDto> page = new PageImpl<>(List.of(sampleDto));
        when(taskSubmissionService.listSubmissionsByTask(eq(10L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/submissions")
                        .param("taskId", "10").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].taskId").value(10));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void listSubmissions_withoutTaskId_returns200() throws Exception {
        Page<TaskSubmissionDto> page = new PageImpl<>(List.of(sampleDto));
        when(taskSubmissionService.listSubmissionsByTask(isNull(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/submissions")
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk());
    }
}
