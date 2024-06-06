package com.TaskManagement.TaskFlow.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TaskManagement.TaskFlow.Model.TaskStates;

public interface TaskStateRepository extends  JpaRepository<TaskStates, Long> {
}
