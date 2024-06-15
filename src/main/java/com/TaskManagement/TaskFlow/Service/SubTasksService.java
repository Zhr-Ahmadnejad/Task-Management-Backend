package com.TaskManagement.TaskFlow.Service;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.SubTaskDto;

public interface SubTasksService {

    ResponseEntity<?> deleteSubTask(String token, Long subTaskId);

    ResponseEntity<?> createTask(String token, SubTaskDto subTaskDTO);

    ResponseEntity<?> updateIsActive(String token, Long subTaskId, SubTaskDto subTaskDTO);

}
