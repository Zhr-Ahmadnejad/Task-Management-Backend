package com.TaskManagement.TaskFlow.Service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;

public interface TaskService {

    Optional<Tasks> getTaskById(Long taskId);

    ResponseEntity<?> createTask(String token, @Valid TaskDto taskDTO);

    Tasks updateTask(Long taskId, Tasks task);

    ResponseEntity<?> deleteTask(String token, Long taskId);

    Tasks addSubTaskToTask(Long taskId, SubTasks subTask);

    ResponseEntity<?> getAllTasks(String token, TaskDto taskDTO);

}
