package com.example.demo.repository;

import com.example.demo.entity.TaskSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

    Page<TaskSubmission> findByTaskId(Long taskId, Pageable pageable);

    void deleteByTaskId(Long taskId);
}
