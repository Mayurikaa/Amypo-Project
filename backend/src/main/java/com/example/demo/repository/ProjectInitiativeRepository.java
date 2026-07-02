package com.example.demo.repository;

import com.example.demo.entity.ProjectInitiative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectInitiativeRepository extends JpaRepository<ProjectInitiative, Long> {

    boolean existsByProjectCode(String projectCode);

    @Query("SELECT p FROM ProjectInitiative p WHERE " +
           "(:status = 'ALL' OR p.status = :status) AND " +
           "(:query IS NULL OR :query = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.projectCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ProjectInitiative> searchInitiatives(@Param("query") String query,
                                               @Param("status") String status,
                                               Pageable pageable);
}
