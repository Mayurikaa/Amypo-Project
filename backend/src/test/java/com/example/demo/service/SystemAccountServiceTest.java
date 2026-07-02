package com.example.demo.service;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.SystemAccountDto;
import com.example.demo.entity.SystemAccount;
import com.example.demo.exception.CapacityExceededException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemAccountServiceTest {

    @Mock
    private SystemAccountRepository systemAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private SystemAccountService systemAccountService;

    private SystemAccount sampleAccount;

    @BeforeEach
    void setUp() {
        sampleAccount = SystemAccount.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashed")
                .fullName("Test User")
                .domainRole("PROJECT_MANAGER")
                .isActive(true)
                .build();
    }

    @Test
    void provisionAccount_success() {
        SystemAccountDto dto = SystemAccountDto.builder()
                .email("new@example.com")
                .fullName("New User")
                .password("pass123")
                .domainRole("PROJECT_MANAGER")
                .build();

        when(systemAccountRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(systemAccountRepository.save(any())).thenReturn(sampleAccount);

        SystemAccountDto result = systemAccountService.provisionAccount(dto);

        assertNotNull(result);
        verify(systemAccountRepository).save(any());
    }

    @Test
    void provisionAccount_duplicateEmail_throwsCapacityExceededException() {
        SystemAccountDto dto = SystemAccountDto.builder()
                .email("test@example.com")
                .fullName("Test")
                .password("pass")
                .domainRole("PROJECT_MANAGER")
                .build();

        when(systemAccountRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(CapacityExceededException.class, () -> systemAccountService.provisionAccount(dto));
    }

    @Test
    void provisionAccount_invalidRole_throwsUnauthorizedActionException() {
        SystemAccountDto dto = SystemAccountDto.builder()
                .email("new@example.com")
                .fullName("Test")
                .password("pass")
                .domainRole("INVALID_ROLE")
                .build();

        when(systemAccountRepository.existsByEmail("new@example.com")).thenReturn(false);

        assertThrows(UnauthorizedActionException.class, () -> systemAccountService.provisionAccount(dto));
    }

    @Test
    void authenticate_success() {
        AuthRequestDto dto = new AuthRequestDto("test@example.com", "password");

        when(systemAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtTokenProvider.createToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponseDto response = systemAccountService.authenticate(dto);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void authenticate_accountNotFound_throwsResourceNotFoundException() {
        AuthRequestDto dto = new AuthRequestDto("missing@example.com", "pass");

        when(systemAccountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> systemAccountService.authenticate(dto));
    }

    @Test
    void authenticate_wrongPassword_throwsUnauthorizedActionException() {
        AuthRequestDto dto = new AuthRequestDto("test@example.com", "wrongpass");

        when(systemAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThrows(UnauthorizedActionException.class, () -> systemAccountService.authenticate(dto));
    }

    @Test
    void authenticate_inactiveAccount_throwsAccessRevoked() {
        sampleAccount.setIsActive(false);
        AuthRequestDto dto = new AuthRequestDto("test@example.com", "password");

        when(systemAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);

        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class,
                () -> systemAccountService.authenticate(dto));
        assertTrue(ex.getMessage().startsWith("ACCESS_REVOKED:"));
    }

    @Test
    void toggleAccountStatus_flipsActiveFlag() {
        when(systemAccountRepository.findById(1L)).thenReturn(Optional.of(sampleAccount));
        when(systemAccountRepository.save(any())).thenReturn(sampleAccount);

        systemAccountService.toggleAccountStatus(1L);

        verify(systemAccountRepository).save(any());
    }

    @Test
    void toggleAccountStatus_notFound_throwsResourceNotFoundException() {
        when(systemAccountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> systemAccountService.toggleAccountStatus(99L));
    }

    @Test
    void listAccounts_allRoles_returnsPage() {
        Page<SystemAccount> page = new PageImpl<>(List.of(sampleAccount));
        when(systemAccountRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SystemAccountDto> result = systemAccountService.listAccounts("ALL", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listAccounts_filteredByRole_returnsPage() {
        Page<SystemAccount> page = new PageImpl<>(List.of(sampleAccount));
        when(systemAccountRepository.findByDomainRole(eq("PROJECT_MANAGER"), any())).thenReturn(page);

        Page<SystemAccountDto> result = systemAccountService.listAccounts("PROJECT_MANAGER", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAccountById_found_returnsDto() {
        when(systemAccountRepository.findById(1L)).thenReturn(Optional.of(sampleAccount));

        SystemAccountDto result = systemAccountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAccountById_notFound_throwsResourceNotFoundException() {
        when(systemAccountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> systemAccountService.getAccountById(99L));
    }

    @Test
    void deleteAccount_success() {
        when(systemAccountRepository.existsById(1L)).thenReturn(true);

        systemAccountService.deleteAccount(1L);

        verify(systemAccountRepository).deleteById(1L);
    }

    @Test
    void deleteAccount_notFound_throwsResourceNotFoundException() {
        when(systemAccountRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> systemAccountService.deleteAccount(99L));
    }
}
