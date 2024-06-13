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
import com.TaskManagement.TaskFlow.Repository.SubTaskRepository;
import com.TaskManagement.TaskFlow.Repository.TaskRepository;
import com.TaskManagement.TaskFlow.Repository.TaskStateRepository;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Service.TaskService;
import com.TaskManagement.TaskFlow.Service.TaskStateService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.SubTaskVo;
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
    private final SubTaskRepository subTaskRepository;
    private final TaskStateRepository taskStateRepository;

    @Autowired
    public TaskServiceImp(TaskRepository taskRepository, TokenService tokenService,
            UserService userService, BoardService boardService, TaskStateService taskStateService,
            SubTaskRepository subTaskRepository, TaskStateRepository taskStateRepository) {
        this.taskRepository = taskRepository;
        this.tokenService = tokenService;
        this.userService = userService;
        this.boardService = boardService;
        this.taskStateService = taskStateService;
        this.subTaskRepository = subTaskRepository;
        this.taskStateRepository = taskStateRepository;

    }

    @Override
    public ResponseEntity<?> getAllTasks(String token, TaskDto taskDto) {
        try {
            String extractedToken = tokenService.validateToken(token);
            // Extract email from token
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = new Boards();
            board = boardService.findBoardsById(taskDto.getBoardId());
            TaskStates taskState = new TaskStates();
            taskState = taskStateService.findTaskStateById(taskDto.getTaskStateId());
            List<Tasks> tasks = new ArrayList<>();
            tasks = taskRepository.findByBoardAndUserAndState(board, user, taskState);
            List<TaskVo> taskVos = new ArrayList<>();
            for (Tasks task : tasks) {
                List<SubTasks> subTasks = subTaskRepository.findByTask(task);
                taskVos.add(mapEntitieToVo(task, subTasks));
            }
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

    @Override
    public Optional<Tasks> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public ResponseEntity<?> createTask(String token, TaskDto taskDtO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = new Boards();
            board = boardService.findBoardsById(taskDtO.getBoardId());
            TaskStates taskState = new TaskStates();
            taskState = taskStateService.findTaskStateById(taskDtO.getTaskStateId());
            Tasks task = new Tasks();
            task.setTaskName(taskDtO.getTaskName());
            task.setDescription(taskDtO.getDescription());
            task.setBoard(board);
            task.setState(taskState);
            task.setUser(user);
            Tasks saveTask = taskRepository.save(task);
            List<SubTasks> savSubTasks = new ArrayList<>();
            for (String subtaskName : taskDtO.getSubTasks()) {
                SubTasks subTasks = new SubTasks();
                subTasks.setTitle(subtaskName);
                subTasks.setTask(saveTask);
                subTasks.setActive(true);
                SubTasks saveSubTasks = subTaskRepository.save(subTasks);
                savSubTasks.add(saveSubTasks);
            }
            TaskVo taskVo = mapEntitieToVo(saveTask, savSubTasks);
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

    @Override
    public ResponseEntity<?> updateTask(String token, Long taskId, TaskDto taskDtO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Tasks task = taskRepository.findById(taskId).orElse(null);
            if (task.getUser() != user) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("The board with this ID is not found for this user.");
            } else {
                if (taskDtO.getTaskName() != null) {
                    task.setTaskName(taskDtO.getTaskName());
                }
                if (taskDtO.getDescription() != null) {
                    task.setDescription(taskDtO.getDescription());
                }
                if (taskDtO.getTaskStateId() != null) {
                    if (taskStateRepository.findById(taskDtO.getTaskStateId()).orElse(null) != null) {
                        task.setState(taskStateRepository.findById(taskDtO.getTaskStateId()).get());
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(" This task state is not found for this board.");
                    }
                }
                Tasks saveTask = taskRepository.save(task);
                if (taskDtO.getSubTasks() != null) {
                    List<SubTasks> savSubTasks = new ArrayList<>();
                    for (String subtaskName : taskDtO.getSubTasks()) {
                        SubTasks subTasks = new SubTasks();
                        subTasks.setTitle(subtaskName);
                        subTasks.setTask(saveTask);
                        subTasks.setActive(true);
                        SubTasks saveSubTasks = subTaskRepository.save(subTasks);
                        savSubTasks.add(saveSubTasks);
                    }
                } 
                    TaskVo taskVo = mapEntitieToVo(saveTask, subTaskRepository.findByTask(saveTask));
                    return new ResponseEntity<>(taskVo, HttpStatus.CREATED);

            }
        }

        catch (Exception e) {
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
    public Tasks addSubTaskToTask(Long taskId, SubTasks subTask) {
        Tasks task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.getSubTasks().add(subTask);
        return taskRepository.save(task);
    }

    @Override
    public ResponseEntity<?> deleteTask(String token, Long taskId) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Tasks task = taskRepository.findById(taskId).orElse(null);
            if (task != null && task.getUser().equals(user)) {
                taskRepository.delete(task);
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Task deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to delete this task or the task was not found");
            }
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

    private TaskVo mapEntitieToVo(Tasks task, List<SubTasks> subTasks) {
        TaskVo taskVo = new TaskVo();
        taskVo.setId(task.getId());
        taskVo.setTaskName(task.getTaskName());
        taskVo.setDescription(task.getDescription());
        taskVo.setStateId(task.getState().getId());
        taskVo.setBoardId(task.getBoard().getId());
        List<SubTaskVo> subTaskVos = new ArrayList<>();
        for (SubTasks subTask : subTasks) {
            SubTaskVo subtaskVo = new SubTaskVo();
            subtaskVo.setActive(subTask.isActive());
            subtaskVo.setId(subTask.getId());
            subtaskVo.setTaskId(subTask.getTask().getId());
            subtaskVo.setTitle(subTask.getTitle());
            subTaskVos.add(subtaskVo);
        }
        taskVo.setSubTasks(subTaskVos);

        return taskVo;

    }

}
