package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;
import com.TaskManagement.TaskFlow.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/board/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(@RequestHeader("Authorization") String token , @RequestBody TaskDto taskDTO) {
        try{
            ResponseEntity<?> response = taskService.getAllTasks(token , taskDTO);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Tasks> getTaskById(@PathVariable Long taskId) {
        Optional<Tasks> task = taskService.getTaskById(taskId);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<?> createTask(@RequestHeader("Authorization") String token, @RequestBody TaskDto taskDTO) {
        try {
            ResponseEntity<?> response = taskService.createTask(token, taskDTO);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Tasks> updateTask(@PathVariable Long taskId, @RequestBody Tasks task) {
        Tasks updatedTask = taskService.updateTask(taskId, task);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<Tasks> addSubTaskToTask(@PathVariable Long taskId, @RequestBody SubTasks subTask) {
        Tasks updatedTask = taskService.addSubTaskToTask(taskId, subTask);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }
}
