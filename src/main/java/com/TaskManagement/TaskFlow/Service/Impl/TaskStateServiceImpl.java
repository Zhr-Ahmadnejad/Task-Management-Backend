package com.TaskManagement.TaskFlow.Service.Impl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Dto.TaskStateDto;
import com.TaskManagement.TaskFlow.Dto.TaskStatesDto;
import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.TaskStates;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.TaskStateRepository;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Service.TaskStateService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.TaskStateVo;

@Service
public class TaskStateServiceImpl implements TaskStateService {

    private final TaskStateRepository taskStateRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final BoardService boardService;

    @Autowired
    public TaskStateServiceImpl(TaskStateRepository taskStateRepository, UserService userService,
            TokenService tokenService, BoardService boardService) {
        this.taskStateRepository = taskStateRepository;
        this.userService = userService;
        this.tokenService = tokenService;
        this.boardService = boardService;
    }

    @Override
    public ResponseEntity<?> createTaskState(String token, TaskStatesDto taskStateDTO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            // Extract email from token
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = new Boards();
            board = boardService.findBoardsById(taskStateDTO.getBoardId());
            List<TaskStates> savedTaskStates = new ArrayList<>();
            for (String taskName : taskStateDTO.getStatesName()) { // تغییر اینجا
                TaskStates taskState = new TaskStates();
                taskState.setStateName(taskName);
                taskState.setBoard(board); // تغییر اینجا
                taskState.setUser(user);
                // Save to database
                TaskStates savedTaskState = taskStateRepository.save(taskState);
                savedTaskStates.add(savedTaskState);
            }
            // Map Entity to VO and return as ResponseEntity
            List<TaskStateVo> taskStateVOs = mapEntitiesToVOs(savedTaskStates);
            return new ResponseEntity<>(taskStateVOs, HttpStatus.CREATED);
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while updating user", e);
            }
        }
    }

    @Override
    public ResponseEntity<?> updateTaskState(String token, TaskStateDto taskStateDto, Long taskStateId) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = boardService.findBoardsById(taskStateDto.getBoardId());
            TaskStates taskState = findTaskStateById(taskStateId);
            if (taskState.getUser() != user) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("The taskState with this ID is not found for this user.");
            } else if (taskState.getBoard() != board) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("The task state with this ID is not found for this board.");
            } else {
                taskState.setStateName(taskStateDto.getStateName());
                TaskStates saveTaskStates = taskStateRepository.save(taskState);
                TaskStateVo taskStateVo = mapEntitieToVo(saveTaskStates);
                return new ResponseEntity<>(taskStateVo, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while updating task state", e);
            }
        }
    }

    public TaskStates findTaskStateById(Long id) {
        return taskStateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private List<TaskStateVo> mapEntitiesToVOs(List<TaskStates> taskStates) {
        List<TaskStateVo> taskStateVOs = new ArrayList<>();
        for (TaskStates taskState : taskStates) {
            TaskStateVo taskStateVO = new TaskStateVo();
            taskStateVO.setId(taskState.getId());
            taskStateVO.setStateName(taskState.getStateName());
            taskStateVO.setBoardId(taskState.getBoard().getId());
            taskStateVOs.add(taskStateVO);
        }
        return taskStateVOs;
    }

    private TaskStateVo mapEntitieToVo(TaskStates taskState){
        TaskStateVo taskStateVO = new TaskStateVo();
        taskStateVO.setId(taskState.getId());
        taskStateVO.setStateName(taskState.getStateName());
        taskStateVO.setBoardId(taskState.getBoard().getId());
        return taskStateVO;

    }

}