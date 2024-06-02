package com.TaskManagement.TaskFlow.Controller;

import java.util.List;

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
import com.TaskManagement.TaskFlow.Vo.TaskStateVo;

@RestController
@RequestMapping("/api/user/TaskState")
public class TaskStateController {

    private final TaskStateService taskStateService;

    @Autowired
    public TaskStateController(TaskStateService taskStateService) {
        this.taskStateService = taskStateService;
    }

    @PostMapping
    public ResponseEntity<List<TaskStateVo>> createState(@RequestHeader("Authorization") String token,
            @RequestBody TaskStateDto taskStateDto) {
        try {
            ResponseEntity<List<TaskStateVo>> response = taskStateService.createTaskState(token , taskStateDto);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
