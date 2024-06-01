package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;
import com.TaskManagement.TaskFlow.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.naming.NameNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user/board/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Tasks>> getAllTasks() {
        List<Tasks> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Tasks> getTaskById(@PathVariable Long taskId) {
        Optional<Tasks> task = taskService.getTaskById(taskId);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<String> createTask(@RequestHeader(value = "token") String token, @Valid @RequestBody TaskDto taskDTO, BindingResult bindingResult) throws NameNotFoundException {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        taskService.createTask(token , taskDTO );
        return new ResponseEntity<>("Task saved successfully", HttpStatus.CREATED);
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
