package com.TaskManagement.TaskFlow.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagement.TaskFlow.Dto.TaskStateDto;
import com.TaskManagement.TaskFlow.Service.TaskStateService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/user/TaskState")
public class TaskStateController {

    private final TaskStateService taskStateService;

    @Autowired
    public TaskStateController(TaskStateService taskStateService) {
        this.taskStateService = taskStateService;
    }

    @PostMapping
    public ResponseEntity<?> createState(@RequestHeader("Authorization") String token,
            @RequestBody TaskStateDto taskStateDto) {
        try {
            ResponseEntity<?> response = taskStateService.createTaskState(token , taskStateDto);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{taskStateId}")
    public ResponseEntity<?> putMethodName(@RequestHeader("Authorization") String token ,@PathVariable Long taskStateId, @RequestBody TaskStateDto taskStateDto) {
        try{
            ResponseEntity<?> response = taskStateService.updateTaskState(token , taskStateDto , taskStateId);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

}
