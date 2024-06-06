package com.TaskManagement.TaskFlow.Service.Impl;

import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.TaskStates;
import com.TaskManagement.TaskFlow.Model.Tasks;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.TaskRepository;
import com.TaskManagement.TaskFlow.Repository.UserRepository;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Service.TaskService;
import com.TaskManagement.TaskFlow.Service.TaskStateService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.TaskVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;
    private final TokenService tokenService;
    private final UserService userService;
    private final BoardService boardService;
    private final TaskStateService taskStateService;


    @Autowired
    public TaskServiceImp(TaskRepository taskRepository, TokenService tokenService,
            UserService userService, BoardService boardService, TaskStateService taskStateService) {
        this.taskRepository = taskRepository;
        this.tokenService = tokenService;
        this.userService = userService;
        this.boardService = boardService;
        this.taskStateService = taskStateService;
    }

    public ResponseEntity<?> getAllTasks(String token , TaskDto taskDto) {
        try{
            String extractedToken = tokenService.validateToken(token);
            // Extract email from token
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = new Boards();
            board = boardService.findBoardsById(taskDto.getBoardId());
            TaskStates taskState = new TaskStates();
            taskState = taskStateService.findTaskStateById(taskDto.getStateId());
            List<Tasks> tasks = new ArrayList<>();
            tasks = taskRepository.findByBoardAndUserAndState(board, user, taskState);
            List<TaskVo> taskVos = mapEntitiesToVos(tasks);
            return new ResponseEntity<>(taskVos, HttpStatus.OK);

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

    public Optional<Tasks> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public ResponseEntity<?> createTask(String token, TaskDto taskDtO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            // Extract email from token
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = new Boards();
            board = boardService.findBoardsById(taskDtO.getBoardId());
            TaskStates taskState = new TaskStates();
            taskState = taskStateService.findTaskStateById(taskDtO.getStateId());
            Tasks task = new Tasks();
            task.setTaskName(taskDtO.getTaskName());
            task.setDescription(taskDtO.getDescription());
            task.setBoard(board);
            task.setState(taskState);
            task.setUser(user);
            TaskVo taskVo = mapEntitieToVo(taskRepository.save(task));
            return new ResponseEntity<>(taskVo, HttpStatus.CREATED);

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

    public Tasks updateTask(Long taskId, Tasks newTask) {
        return taskRepository.findById(taskId).map(task -> {
            task.setTaskName(newTask.getTaskName());
            task.setDescription(newTask.getDescription());
            task.setUser(newTask.getUser());
            task.setState(newTask.getState());
            task.setSubTasks(newTask.getSubTasks());
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Tasks addSubTaskToTask(Long taskId, SubTasks subTask) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.getSubTasks().add(subTask);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private TaskVo mapEntitieToVo(Tasks task){
        TaskVo taskVo = new TaskVo();
        taskVo.setId(task.getId());
        taskVo.setTaskName(task.getTaskName());
        taskVo.setDescription(task.getDescription());
        taskVo.setStateId(task.getState().getId());
        taskVo.setBoardId(task.getBoard().getId());
        return taskVo;

    }
    private List<TaskVo> mapEntitiesToVos(List<Tasks> tasks){
        List<TaskVo> taskVos = new ArrayList<>();
        for (Tasks task : tasks ){
            TaskVo taskVo = new TaskVo();
            taskVo.setId(task.getId());
            taskVo.setTaskName(task.getTaskName());
            taskVo.setDescription(task.getDescription());
            taskVo.setStateId(task.getState().getId());
            taskVo.setBoardId(task.getBoard().getId());
            taskVos.add(taskVo);
        }
        return taskVos;

    }

}
