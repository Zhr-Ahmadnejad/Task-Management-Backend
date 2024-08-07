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
    public ResponseEntity<?> getAllTasks(@RequestHeader("Authorization") String token , @RequestHeader Long boardId , @RequestHeader Long taskStateId) {
        try{
            ResponseEntity<?> response = taskService.getAllTasks(token , taskStateId , boardId);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Tasks> getTaskById(@RequestHeader("Authorization") String token , @PathVariable Long taskId) {
        Optional<Tasks> task = taskService.getTaskById(token,taskId);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/start")
    public ResponseEntity<?> getTasksinStart(@RequestHeader("Authorization") String token ) {
        try {
            ResponseEntity<?> response = taskService.getTasksinStart(token);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
    public ResponseEntity<?> updateTask(@RequestHeader("Authorization") String token, @PathVariable Long taskId, @RequestBody TaskDto task) {
        return taskService.updateTask(token,taskId, task);
    }

    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<Tasks> addSubTaskToTask(@PathVariable Long taskId, @RequestBody SubTasks subTask) {
        Tasks updatedTask = taskService.addSubTaskToTask(taskId, subTask);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@RequestHeader("Authorization") String token, @PathVariable Long taskId) {
        return taskService.deleteTask(token, taskId);

    }

    
    @GetMapping("/boardId/{boardId}")
    public ResponseEntity<?> getTasksByBoardId(@RequestHeader("Authorization") String token , @PathVariable Long boardId) {
        try {
            ResponseEntity<?> response = taskService.getTasksByBoardId(token, boardId);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
