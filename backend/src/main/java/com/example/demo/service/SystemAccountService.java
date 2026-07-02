package com.example.demo.service;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.SystemAccountDto;
import com.example.demo.entity.SystemAccount;
import com.example.demo.exception.CapacityExceededException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemAccountService {

    private final SystemAccountRepository systemAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final List<String> VALID_ROLES = List.of(
            "PROJECT_DIRECTOR", "PROJECT_MANAGER", "TEAM_CONTRIBUTOR"
    );

    @Transactional
    public SystemAccountDto provisionAccount(SystemAccountDto dto) {
        if (systemAccountRepository.existsByEmail(dto.getEmail())) {
            throw new CapacityExceededException("Email already registered: " + dto.getEmail());
        }
        if (!VALID_ROLES.contains(dto.getDomainRole())) {
            throw new UnauthorizedActionException("Invalid domain role: " + dto.getDomainRole());
        }
        SystemAccount account = SystemAccount.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .domainRole(dto.getDomainRole())
                .isActive(true)
                .build();
        SystemAccount saved = systemAccountRepository.save(account);
        return mapToDto(saved);
    }

    public AuthResponseDto authenticate(AuthRequestDto dto) {
        SystemAccount account = systemAccountRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + dto.getEmail()));
        if (!passwordEncoder.matches(dto.getPassword(), account.getPasswordHash())) {
            throw new UnauthorizedActionException("Invalid credentials.");
        }
        if (!account.getIsActive()) {
            throw new UnauthorizedActionException("ACCESS_REVOKED: Account has been deactivated.");
        }
        String token = jwtTokenProvider.createToken(account.getEmail(), account.getDomainRole());
        return AuthResponseDto.builder()
                .token(token)
                .id(account.getId())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .domainRole(account.getDomainRole())
                .build();
    }

    @Transactional
    public SystemAccountDto toggleAccountStatus(Long id) {
        SystemAccount account = systemAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
        account.setIsActive(!account.getIsActive());
        return mapToDto(systemAccountRepository.save(account));
    }

    public Page<SystemAccountDto> listAccounts(String role, Pageable pageable) {
        if ("ALL".equalsIgnoreCase(role)) {
            return systemAccountRepository.findAll(pageable).map(this::mapToDto);
        }
        return systemAccountRepository.findByDomainRole(role, pageable).map(this::mapToDto);
    }

    public SystemAccountDto getAccountById(Long id) {
        SystemAccount account = systemAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
        return mapToDto(account);
    }

    @Transactional
    public SystemAccountDto updateAccount(Long id, SystemAccountDto dto) {
        SystemAccount account = systemAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
        account.setFullName(dto.getFullName());
        account.setDomainRole(dto.getDomainRole());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getIsActive() != null) {
            account.setIsActive(dto.getIsActive());
        }
        return mapToDto(systemAccountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!systemAccountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found: " + id);
        }
        systemAccountRepository.deleteById(id);
    }

    private SystemAccountDto mapToDto(SystemAccount account) {
        return SystemAccountDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .domainRole(account.getDomainRole())
                .isActive(account.getIsActive())
                .build();
    }
}
