package com.TaskManagement.TaskFlow.Service;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.TaskStateDto;
import com.TaskManagement.TaskFlow.Model.TaskStates;

public interface TaskStateService {
    ResponseEntity<?> createTaskState(String token, TaskStateDto taskStateDTO);

    ResponseEntity<?> updateTaskState(String token, TaskStateDto taskStateDto, Long taskStateId);

    TaskStates findTaskStateById(Long stateId);

    ResponseEntity<?> deleteTaskState(String token, Long taskStateId);
}