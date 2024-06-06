package com.TaskManagement.TaskFlow.Service;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.TaskStateDto;
import com.TaskManagement.TaskFlow.Dto.TaskStatesDto;
import com.TaskManagement.TaskFlow.Model.TaskStates;

public interface TaskStateService {
    ResponseEntity<?> createTaskState(String token,TaskStatesDto taskStateDTO);

    ResponseEntity<?> updateTaskState(String token, TaskStateDto taskStateDto, Long taskStateId);

    TaskStates findTaskStateById(Long stateId);
}