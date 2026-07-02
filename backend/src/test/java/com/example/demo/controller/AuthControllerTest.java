package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.SystemAccountDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;
import com.example.demo.service.SystemAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemAccountService systemAccountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_success_returns200WithToken() throws Exception {
        AuthRequestDto request = new AuthRequestDto("director@gmail.com", "password");
        AuthResponseDto response = AuthResponseDto.builder()
                .token("jwt-token").id(1L).email("director@gmail.com")
                .fullName("Executive Director").domainRole("PROJECT_DIRECTOR").build();

        when(systemAccountService.authenticate(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.domainRole").value("PROJECT_DIRECTOR"));
    }

    @Test
    void login_invalidCredentials_returns403() throws Exception {
        AuthRequestDto request = new AuthRequestDto("bad@example.com", "wrongpass");

        when(systemAccountService.authenticate(any()))
                .thenThrow(new UnauthorizedActionException("Invalid credentials."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_accountNotFound_returns404() throws Exception {
        AuthRequestDto request = new AuthRequestDto("missing@example.com", "pass");

        when(systemAccountService.authenticate(any()))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_blankFields_returns400() throws Exception {
        AuthRequestDto request = new AuthRequestDto("", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_success_returns201() throws Exception {
        SystemAccountDto request = SystemAccountDto.builder()
                .email("new@example.com").fullName("New User")
                .password("pass123").domainRole("PROJECT_MANAGER").build();

        SystemAccountDto response = SystemAccountDto.builder()
                .id(2L).email("new@example.com").fullName("New User")
                .domainRole("PROJECT_MANAGER").isActive(true).build();

        when(systemAccountService.provisionAccount(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void register_blankFields_returns400() throws Exception {
        SystemAccountDto request = SystemAccountDto.builder()
                .email("").fullName("").password("pass").domainRole("").build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
