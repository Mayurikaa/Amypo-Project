package com.example.demo.controller;

import com.example.demo.dto.SystemAccountDto;
import com.example.demo.service.SystemAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class SystemAccountController {

    private final SystemAccountService systemAccountService;

    @PostMapping("/provision")
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<SystemAccountDto> provisionAccount(@Valid @RequestBody SystemAccountDto dto) {
        SystemAccountDto created = systemAccountService.provisionAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemAccountDto> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(systemAccountService.getAccountById(id));
    }

    @GetMapping
    public ResponseEntity<Page<SystemAccountDto>> listAccounts(
            @RequestParam(defaultValue = "ALL") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(systemAccountService.listAccounts(role, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<SystemAccountDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody SystemAccountDto dto) {
        return ResponseEntity.ok(systemAccountService.updateAccount(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<SystemAccountDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(systemAccountService.toggleAccountStatus(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_DIRECTOR')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        systemAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
