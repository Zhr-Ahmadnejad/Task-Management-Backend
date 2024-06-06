package com.TaskManagement.TaskFlow.Service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;

public interface TaskService {

    List<Tasks> getAllTasks();

    Optional<Tasks> getTaskById(Long taskId);

    ResponseEntity<?> createTask(String token, @Valid TaskDto taskDTO);

    Tasks updateTask(Long taskId, Tasks task);

    void deleteTask(Long taskId);

    Tasks addSubTaskToTask(Long taskId, SubTasks subTask);

}
