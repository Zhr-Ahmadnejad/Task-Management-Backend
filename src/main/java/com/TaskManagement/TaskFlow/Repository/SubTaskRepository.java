package com.TaskManagement.TaskFlow.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.TaskFlow.Model.SubTasks;

import java.util.List;


@Repository
public interface SubTaskRepository extends JpaRepository<SubTasks, Long> {
    void deleteByTaskId(Long taskId);
    List<SubTasks> findByTaskId(Long taskId);
}
