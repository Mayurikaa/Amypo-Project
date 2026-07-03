package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public AuthRequestDto() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
