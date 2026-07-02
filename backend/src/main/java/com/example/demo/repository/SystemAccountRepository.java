package com.example.demo.repository;

import com.example.demo.entity.SystemAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Long> {

    Optional<SystemAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<SystemAccount> findByDomainRole(String domainRole, Pageable pageable);
}
