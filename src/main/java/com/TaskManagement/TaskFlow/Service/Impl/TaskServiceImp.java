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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Transactional
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
    public ResponseEntity<?> getAllTasks(String token, Long taskStateId, Long boardId) {
        try {
            // اعتبارسنجی توکن و استخراج ایمیل کاربر از توکن
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
    
            // پیدا کردن برد و وضعیت تسک
            Boards board = boardService.findBoardsById(boardId);
            TaskStates taskState = taskStateService.findTaskStateById(taskStateId);
    
            // بررسی اعتبار برد و وضعیت تسک
            if (board == null || taskState == null || taskState.getBoard() != board || taskState.getUser() != user) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Data isn't true or not associated with the user.");
            }
    
            // پیدا کردن تمام تسک‌ها بر اساس برد، کاربر و وضعیت تسک
            List<Tasks> tasks = taskRepository.findByBoardAndUserAndState(board, user, taskState);
            List<TaskVo> taskVos = new ArrayList<>();
    
            // تبدیل تسک‌ها به VO
            for (Tasks task : tasks) {
                List<SubTasks> subTasks = subTaskRepository.findByTaskId(task.getId());
                List<Long> dependentTaskIds = task.getDependentTasks().stream()
                        .map(Tasks::getId)
                        .collect(Collectors.toList());
    
                TaskVo taskVo = mapEntityToVo(task, subTasks, dependentTaskIds);
                taskVos.add(taskVo);
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
                throw new RuntimeException("An error occurred while fetching tasks", e);
            }
        }
    }
    

    @Override
    public Optional<Tasks> getTaskById(String token, Long taskId) {
        return taskRepository.findById(taskId);
    }

@Override
public ResponseEntity<?> createTask(String token, TaskDto taskDto) {
    try {
        String extractedToken = tokenService.validateToken(token);
        String userEmail = tokenService.extractEmailFromToken(extractedToken);
        Users user = userService.findUserByEmail(userEmail);
        Boards board = boardService.findBoardsById(taskDto.getBoardId());
        TaskStates taskState = taskStateService.findTaskStateById(taskDto.getTaskStateId());

        if (board == null || taskState == null || taskState.getBoard() != board || taskState.getUser() != user) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Data isn't true");
        }

        // ایجاد تسک جدید
        Tasks task = new Tasks();
        task.setTaskName(taskDto.getTaskName());
        task.setDescription(taskDto.getDescription());
        task.setBoard(board);
        task.setState(taskState);
        task.setUser(user);

        // اضافه کردن وابستگی‌های تسک‌ها
        if (taskDto.getDependentTaskIds() != null) {
            Set<Tasks> dependentTasks = taskDto.getDependentTaskIds().stream()
                    .map(taskId -> taskRepository.findById(taskId).orElse(null))
                    .filter(Objects::nonNull)  // حذف nulls
                    .collect(Collectors.toSet());
            task.setDependentTasks(dependentTasks);
        }

        Tasks saveTask = taskRepository.save(task);

        // ایجاد زیر تسک‌ها
        List<SubTasks> savSubTasks = new ArrayList<>();
        if (taskDto.getSubTasks() != null) {
            for (String subtaskName : taskDto.getSubTasks()) {
                SubTasks subTasks = new SubTasks();
                subTasks.setTitle(subtaskName);
                subTasks.setTask(saveTask);
                subTasks.setActive(true);
                SubTasks saveSubTasks = subTaskRepository.save(subTasks);
                savSubTasks.add(saveSubTasks);
            }
        }

        // تبدیل به VO
        List<Long> dependentTaskIds = saveTask.getDependentTasks().stream()
                .map(Tasks::getId)
                .collect(Collectors.toList());
        TaskVo taskVo = mapEntityToVo(saveTask, savSubTasks, dependentTaskIds);

        return new ResponseEntity<>(taskVo, HttpStatus.CREATED);

    } catch (Exception e) {
        if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                || e instanceof TokenValidationException) {
            // Handle token related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } else {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while creating task", e);
        }
    }
}

@Override
public ResponseEntity<?> updateTask(String token, Long taskId, TaskDto taskDto) {
    try {
        String extractedToken = tokenService.validateToken(token);
        String userEmail = tokenService.extractEmailFromToken(extractedToken);
        Users user = userService.findUserByEmail(userEmail);
        Tasks task = taskRepository.findById(taskId).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with this ID not found.");
        }

        if (task.getUser() != user) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The task does not belong to this user.");
        }

        // به‌روزرسانی ویژگی‌های تسک
        if (taskDto.getTaskName() != null) {
            task.setTaskName(taskDto.getTaskName());
        }
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getTaskStateId() != null) {
            TaskStates newTaskState = taskStateRepository.findById(taskDto.getTaskStateId()).orElse(null);
            if (newTaskState == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("This task state is not found.");
            }
            task.setState(newTaskState);
        }

        // به‌روزرسانی وابستگی‌های تسک
        if (taskDto.getDependentTaskIds() != null) {
            Set<Tasks> newDependentTasks = taskDto.getDependentTaskIds().stream()
                    .map(depTaskId -> taskRepository.findById(depTaskId).orElse(null))
                    .filter(Objects::nonNull)  // حذف nulls
                    .collect(Collectors.toSet());
            task.setDependentTasks(newDependentTasks);
        }

        // به‌روزرسانی زیر تسک‌ها
        if (taskDto.getSubTasks() != null) {
            subTaskRepository.deleteByTaskId(taskId);
            for (String subtaskName : taskDto.getSubTasks()) {
                SubTasks subTasks = new SubTasks();
                subTasks.setTitle(subtaskName);
                subTasks.setTask(task);
                subTasks.setActive(true);
                subTaskRepository.save(subTasks);
            }
        }

        // ذخیره‌ی تسک به روز شده
        Tasks saveTask = taskRepository.save(task);

        // بازیابی زیر تسک‌ها برای ارسال به کاربر
        List<SubTasks> savSubTasks = subTaskRepository.findByTaskId(saveTask.getId());

        // تبدیل به VO
        List<Long> dependentTaskIds = saveTask.getDependentTasks().stream()
                .map(Tasks::getId)
                .collect(Collectors.toList());

        TaskVo taskVo = mapEntityToVo(saveTask, savSubTasks, dependentTaskIds);

        return new ResponseEntity<>(taskVo, HttpStatus.OK);

    } catch (Exception e) {
        if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                || e instanceof TokenValidationException) {
            // Handle token related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } else {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while updating task", e);
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
                // حذف زیر تسک‌ها
                subTaskRepository.deleteByTaskId(taskId);
    
                // حذف وابستگی‌ها: اگر تسک‌هایی به این تسک وابسته بودند، آنها را از لیست وابستگی‌هایشان حذف می‌کنیم
                Set<Tasks> dependentTasks = task.getDependentTasks();
                for (Tasks dependentTask : dependentTasks) {
                    dependentTask.getDependentTasks().remove(task);
                    taskRepository.save(dependentTask);
                }
    
                // حذف تسک
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
                throw new RuntimeException("An error occurred while deleting the task", e);
            }
        }
    }
    

    @Override
    public ResponseEntity<?> getTasksinStart(String token) {
        try {
            // اعتبارسنجی توکن و استخراج ایمیل کاربر از توکن
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
    
            // پیدا کردن وضعیت‌های تسک که نام آنها "شروع" است
            List<TaskStates> taskStates = taskStateRepository.findByStateNameAndUser("شروع", user);
    
            List<TaskVo> taskVos = new ArrayList<>();
            for (TaskStates taskState : taskStates) {
                // پیدا کردن تسک‌ها بر اساس وضعیت تسک
                List<Tasks> tasks = taskRepository.findByState(taskState);
                for (Tasks task : tasks) {
                    // پیدا کردن زیر تسک‌های هر تسک
                    List<SubTasks> subTasks = subTaskRepository.findByTaskId(task.getId());
    
                    // پیدا کردن ID‌های تسک‌های وابسته به این تسک
                    List<Long> dependentTaskIds = task.getDependentTasks().stream()
                            .map(Tasks::getId)
                            .collect(Collectors.toList());
    
                    // تبدیل تسک‌ها به VO و اضافه کردن ID‌های وابسته
                    TaskVo taskVo = mapEntityToVo(task, subTasks, dependentTaskIds);
                    taskVos.add(taskVo);
                }
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
                throw new RuntimeException("An error occurred while fetching tasks", e);
            }
        }
    }

    @Override
    public ResponseEntity<?> getTasksByBoardId(String token, Long boardId) {
    try {
        String extractedToken = tokenService.validateToken(token);
        String userEmail = tokenService.extractEmailFromToken(extractedToken);
        Users user = userService.findUserByEmail(userEmail);
        // پیدا کردن برد با ID مشخص شده
        Boards board = boardService.findBoardsById(boardId);
        if (board != null && board.getUser().equals(user)) {
            
        // دریافت تسک‌های مربوط به برد
        Set<Tasks> tasks = board.getTasks();
        List<TaskVo> taskVos = new ArrayList<>();

        for (Tasks task : tasks) {
            List<SubTasks> subTasks = subTaskRepository.findByTaskId(task.getId());
            // پیدا کردن ID‌های تسک‌های وابسته به این تسک
            List<Long> dependentTaskIds = task.getDependentTasks().stream()
                    .map(Tasks::getId)
                    .collect(Collectors.toList());

            // تبدیل تسک‌ها به VO
            TaskVo taskVo = mapEntityToVo(task, subTasks, dependentTaskIds);
            taskVos.add(taskVo);
        }

        return new ResponseEntity<>(taskVos, HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Board with the specified ID not found.");

    } catch (Exception e) {
        if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                || e instanceof TokenValidationException) {
            // Handle token related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } else {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while fetching tasks by board ID", e);
        }
    }
}

    

    private TaskVo mapEntityToVo(Tasks task, List<SubTasks> subTasks, List<Long> dependentTaskIds) {
        TaskVo taskVo = new TaskVo();
        taskVo.setId(task.getId());
        taskVo.setTaskName(task.getTaskName());
        taskVo.setDescription(task.getDescription());
        taskVo.setTaskStateId(task.getState().getId());
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
        taskVo.setDependentTaskIds(dependentTaskIds);  // اضافه کردن ID های وابسته
    
        return taskVo;
    }

}
