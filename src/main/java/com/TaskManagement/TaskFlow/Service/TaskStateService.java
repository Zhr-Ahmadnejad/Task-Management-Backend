package com.TaskManagement.TaskFlow.Service;

import javax.naming.NameNotFoundException;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Dto.TaskStateDto;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.TaskStates;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.BoardRepository;
import com.TaskManagement.TaskFlow.Repository.TaskStateRepository;
import com.TaskManagement.TaskFlow.Repository.UserRepository;
import com.TaskManagement.TaskFlow.Vo.BoardVo;
import com.TaskManagement.TaskFlow.Vo.TaskStateVo;

@Service
public class TaskStateService {

    private final TaskStateRepository taskStateRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public TaskStateService(TaskStateRepository taskStateRepository, UserService userService, TokenService tokenService,
            UserRepository userRepository, BoardRepository boardRepository) {
        this.taskStateRepository = taskStateRepository;
        this.tokenService = tokenService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public ResponseEntity<TaskStateVo> createTaskState(TaskStateDto taskStateDto, String token)
            throws NameNotFoundException {
        try {
            String[] parts = token.split("\\s+");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String extractedToken = parts[1];

                if (tokenService.validateToken(extractedToken)) {
                    String userEmail = tokenService.extractEmailFromToken(extractedToken);
                    Users user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new NameNotFoundException("User not found"));
                    Boards board = boardRepository.findById(taskStateDto.getBoardId())
                            .orElseThrow(() -> new NameNotFoundException("Board not found"));
                    TaskStates taskStates = new TaskStates();
                    taskStates.setStateName(taskStateDto.getStateName());
                    taskStates.setUser(user);
                    taskStates.setBoard(board);
                    TaskStateVo taskStateVo = new TaskStateVo(taskStates.getId(), taskStates.getStateName(),
                            board.getId(), user.getId());
                    return ResponseEntity.ok(taskStateVo);
                } else {
                    throw new ExceptionAdapter(extractedToken, null);
                }

            } else {
                throw new IllegalArgumentException("Invalid token format");
            }

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while create task state", e);
        }
    }
}
