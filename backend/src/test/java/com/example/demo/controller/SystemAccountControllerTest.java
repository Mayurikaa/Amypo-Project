package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.SystemAccountDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.SystemAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SystemAccountController.class)
class SystemAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemAccountService systemAccountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper;
    private SystemAccountDto sampleDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        sampleDto = SystemAccountDto.builder()
                .id(1L).email("user@example.com").fullName("Test User")
                .domainRole("PROJECT_MANAGER").isActive(true).build();
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void provisionAccount_success_returns201() throws Exception {
        SystemAccountDto request = SystemAccountDto.builder()
                .email("user@example.com").fullName("Test User")
                .password("pass123").domainRole("PROJECT_MANAGER").build();

        when(systemAccountService.provisionAccount(any())).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/accounts/provision")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void provisionAccount_forbiddenRole_returns403() throws Exception {
        SystemAccountDto request = SystemAccountDto.builder()
                .email("user@example.com").fullName("Test User")
                .password("pass").domainRole("PROJECT_MANAGER").build();

        mockMvc.perform(post("/api/v1/accounts/provision")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void getAccountById_found_returns200() throws Exception {
        when(systemAccountService.getAccountById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void getAccountById_notFound_returns404() throws Exception {
        when(systemAccountService.getAccountById(999L))
                .thenThrow(new ResourceNotFoundException("Account not found: 999"));

        mockMvc.perform(get("/api/v1/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void listAccounts_returns200() throws Exception {
        Page<SystemAccountDto> page = new PageImpl<>(List.of(sampleDto));
        when(systemAccountService.listAccounts(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/accounts")
                        .param("role", "ALL").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("user@example.com"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void updateAccount_success_returns200() throws Exception {
        SystemAccountDto request = SystemAccountDto.builder()
                .email("user@example.com").fullName("Updated User")
                .password("newpass").domainRole("PROJECT_MANAGER").build();

        when(systemAccountService.updateAccount(eq(1L), any())).thenReturn(sampleDto);

        mockMvc.perform(put("/api/v1/accounts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void toggleStatus_success_returns200() throws Exception {
        when(systemAccountService.toggleAccountStatus(1L)).thenReturn(sampleDto);

        mockMvc.perform(patch("/api/v1/accounts/1/status").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void deleteAccount_success_returns204() throws Exception {
        doNothing().when(systemAccountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/v1/accounts/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PROJECT_DIRECTOR")
    void deleteAccount_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Account not found: 999"))
                .when(systemAccountService).deleteAccount(999L);

        mockMvc.perform(delete("/api/v1/accounts/999").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
