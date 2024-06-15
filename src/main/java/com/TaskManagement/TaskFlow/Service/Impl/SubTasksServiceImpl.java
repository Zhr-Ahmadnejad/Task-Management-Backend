package com.TaskManagement.TaskFlow.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Dto.SubTaskDto;
import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.SubTaskRepository;
import com.TaskManagement.TaskFlow.Repository.TaskRepository;
import com.TaskManagement.TaskFlow.Service.SubTasksService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.SubTaskVo;

@Service
public class SubTasksServiceImpl implements SubTasksService {

    private final UserService userService;
    private final TokenService tokenService;
    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public SubTasksServiceImpl(UserService userService, TokenService tokenService,
            SubTaskRepository subTaskRepository, TaskRepository taskRepository) {
        this.tokenService = tokenService;
        this.subTaskRepository = subTaskRepository;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @Override
    public ResponseEntity<?> deleteSubTask(String token, Long subTaskId) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            SubTasks subTask = subTaskRepository.findById(subTaskId).orElse(null);
            if (subTask != null && subTask.getTask().getUser().equals(user)) {
                subTaskRepository.delete(subTask);
                return ResponseEntity.status(HttpStatus.OK)
                        .body("subTask  deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to delete this subTask or the subTask was not found");
            }
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while delete subTask ", e);
            }
        }
    }

    @Override
    public ResponseEntity<?> createTask(String token, SubTaskDto subTaskDTO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            Tasks task = taskRepository.findById(subTaskDTO.getTaskId).orElse(null);
            SubTasks subTask = new SubTasks();
            subTask.setTitle(subTaskDTO.getTitle());
            subTask.setActive(true);
            subTask.setTask(task);
            SubTasks saveSubTasks = subTaskRepository.save(subTask);
            SubTaskVo subTaskVo = mapEntitieToVo(saveSubTasks);
            return new ResponseEntity<>(subTaskVo, HttpStatus.CREATED);
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while update subTask ", e);
            }
        }
    }

    @Override
    public ResponseEntity<?> updateIsActive(String token, Long subTaskId, SubTaskDto subTaskDTO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            SubTasks subTask = subTaskRepository.findById(subTaskId).orElse(null);
            if (subTask != null && subTask.getTask().getUser().equals(user)) {
                subTask.setActive(subTaskDTO.isActive());
                SubTasks saveSubTasks = subTaskRepository.save(subTask);
                SubTaskVo subTaskVo = mapEntitieToVo(saveSubTasks);
                return new ResponseEntity<>(subTaskVo, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to delete this subTask or the subTask was not found");
            }
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while update subTask ", e);
            }
        }
    }

    private SubTaskVo mapEntitieToVo(SubTasks subTask) {
        SubTaskVo subTaskVo = new SubTaskVo();
        subTaskVo.setActive(subTask.isActive());
        subTaskVo.setId(subTask.getId());
        subTaskVo.setTaskId(subTask.getTask().getId());
        subTaskVo.setTitle(subTask.getTitle());
        return subTaskVo;
    }

}
