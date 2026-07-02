package com.example.demo.repository;

import com.example.demo.entity.ProjectTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    Optional<ProjectTask> findByTaskCode(String taskCode);

    Page<ProjectTask> findByInitiativeId(Long initiativeId, Pageable pageable);

    Page<ProjectTask> findByAssigneeId(Long assigneeId, Pageable pageable);

    void deleteByInitiativeId(Long initiativeId);

    void deleteByMilestoneId(Long milestoneId);

    @Query("SELECT p FROM ProjectTask p WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.taskCode) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR :status = 'ALL' OR p.status = :status) AND " +
           "(:assigneeId IS NULL OR p.assignee.id = :assigneeId)")
    Page<ProjectTask> searchTasks(@Param("query") String query,
                                   @Param("status") String status,
                                   @Param("assigneeId") Long assigneeId,
                                   Pageable pageable);

    @Query("SELECT SUM(p.loggedHours) FROM ProjectTask p")
    Long sumTotalLoggedHours();

    @Query("SELECT COUNT(p) FROM ProjectTask p WHERE p.status IN ('PENDING', 'IN_PROGRESS')")
    Long countActiveTasks();

    @Query("SELECT COALESCE(SUM(p.estimatedHours - p.loggedHours), 0) FROM ProjectTask p " +
           "WHERE p.assignee.id = :assigneeId AND p.status IN ('PENDING', 'IN_PROGRESS')")
    Long calculateRemainingHoursForAssignee(@Param("assigneeId") Long assigneeId);
}
