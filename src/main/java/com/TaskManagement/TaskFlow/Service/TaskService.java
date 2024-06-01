package com.TaskManagement.TaskFlow.Service;

import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.TaskRepository;
import com.TaskManagement.TaskFlow.Repository.UserRepository;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.naming.NameNotFoundException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, TokenService tokenService, UserRepository userRepository,
            UserService userService) {
        this.taskRepository = taskRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Tasks> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Tasks> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Tasks createTask(String token, TaskDto taskDtO) throws NameNotFoundException {
        try {
            String[] parts = token.split("\\s+");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String extractedToken = parts[1];

                if (tokenService.validateToken(extractedToken)) {
                    // Extract email from token
                    String userEmail = tokenService.extractEmailFromToken(extractedToken);
                    Users user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Tasks task = new Tasks();
                    task.setTaskName(taskDtO.getTaskName());
                    task.setDescription(taskDtO.getDescription());
                    //todo set board id and state id and subtasks id
                    task.setUser(user);
                    return taskRepository.save(task);

                } else {
                    throw new ExceptionAdapter(extractedToken, null);
                }
            } else {
                throw new Exception("Invalid token format");
            }
        } catch (Exception e) {
            // مدیریت استثناء و یا ارسال آن به بالا
            throw new RuntimeException("An error occurred while updating user", e);
        }
    }

    public Tasks updateTask(Long taskId, Tasks newTask) {
        return taskRepository.findById(taskId).map(task -> {
            task.setTaskName(newTask.getTaskName());
            task.setDescription(newTask.getDescription());
            task.setUser(newTask.getUser());
            task.setStates(newTask.getStates());
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
}
