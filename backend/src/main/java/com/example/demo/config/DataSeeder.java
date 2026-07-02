package com.example.demo.config;

import com.example.demo.entity.SystemAccount;
import com.example.demo.repository.SystemAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDirector(SystemAccountRepository systemAccountRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (systemAccountRepository.findByEmail("director@gmail.com").isEmpty()) {
                SystemAccount director = SystemAccount.builder()
                        .email("director@gmail.com")
                        .passwordHash(passwordEncoder.encode("password"))
                        .fullName("Executive Director Evelyn Vance")
                        .domainRole("PROJECT_DIRECTOR")
                        .isActive(true)
                        .build();
                systemAccountRepository.save(director);
            }
        };
    }
}
