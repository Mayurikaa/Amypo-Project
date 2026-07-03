package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SystemAccountDto {

    private Long id;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String fullName;

    private String password;

    @NotBlank
    private String domainRole;

    private Boolean isActive;

    public SystemAccountDto() {}

    public SystemAccountDto(Long id, String email, String fullName, String password, String domainRole, Boolean isActive) {
        this.id = id; this.email = email; this.fullName = fullName;
        this.password = password; this.domainRole = domainRole; this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDomainRole() { return domainRole; }
    public void setDomainRole(String domainRole) { this.domainRole = domainRole; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String email;
        private String fullName;
        private String password;
        private String domainRole;
        private Boolean isActive;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder domainRole(String domainRole) { this.domainRole = domainRole; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public SystemAccountDto build() {
            return new SystemAccountDto(id, email, fullName, password, domainRole, isActive);
        }
    }
}
