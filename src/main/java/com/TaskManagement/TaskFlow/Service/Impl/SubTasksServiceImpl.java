package com.TaskManagement.TaskFlow.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.SubTaskRepository;
import com.TaskManagement.TaskFlow.Service.SubTasksService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;

@Service
public class SubTasksServiceImpl implements SubTasksService {

    private final UserService userService;
    private final TokenService tokenService;
    private final SubTaskRepository subTaskRepository;

    @Autowired
    public SubTasksServiceImpl(UserService userService, TokenService tokenService,
            SubTaskRepository subTaskRepository) {
        this.tokenService = tokenService;
        this.subTaskRepository = subTaskRepository;
        this.userService = userService;
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

}
