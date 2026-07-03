package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.SystemAccountDto;
import com.example.demo.service.SystemAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SystemAccountService systemAccountService;

    public AuthController(SystemAccountService systemAccountService) {
        this.systemAccountService = systemAccountService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto dto) {
        AuthResponseDto response = systemAccountService.authenticate(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<SystemAccountDto> register(@Valid @RequestBody SystemAccountDto dto) {
        SystemAccountDto created = systemAccountService.provisionAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
