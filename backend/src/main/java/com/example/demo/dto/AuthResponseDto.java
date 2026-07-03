package com.example.demo.dto;

public class AuthResponseDto {

    private String token;
    private Long id;
    private String email;
    private String fullName;
    private String domainRole;

    public AuthResponseDto() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDomainRole() { return domainRole; }
    public void setDomainRole(String domainRole) { this.domainRole = domainRole; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private Long id;
        private String email;
        private String fullName;
        private String domainRole;

        public Builder token(String token) { this.token = token; return this; }
        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder domainRole(String domainRole) { this.domainRole = domainRole; return this; }

        public AuthResponseDto build() {
            AuthResponseDto d = new AuthResponseDto();
            d.token = this.token; d.id = this.id; d.email = this.email;
            d.fullName = this.fullName; d.domainRole = this.domainRole;
            return d;
        }
    }
}
