package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemAccountDto {

    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;

    private String password;

    @NotBlank
    private String domainRole;

    private Boolean isActive;
}
