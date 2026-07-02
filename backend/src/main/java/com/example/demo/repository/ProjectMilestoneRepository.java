package com.example.demo.repository;

import com.example.demo.entity.ProjectMilestone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, Long> {

    Page<ProjectMilestone> findByInitiativeId(Long initiativeId, Pageable pageable);

    void deleteByInitiativeId(Long initiativeId);

    @Query("SELECT m FROM ProjectMilestone m WHERE " +
           "(:initiativeId IS NULL OR m.initiative.id = :initiativeId) AND " +
           "(:status = 'ALL' OR m.status = :status) AND " +
           "(:query IS NULL OR :query = '' OR LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ProjectMilestone> searchMilestones(@Param("query") String query,
                                             @Param("initiativeId") Long initiativeId,
                                             @Param("status") String status,
                                             Pageable pageable);
}
