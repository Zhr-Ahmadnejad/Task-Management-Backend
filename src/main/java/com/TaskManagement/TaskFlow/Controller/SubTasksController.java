package com.TaskManagement.TaskFlow.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagement.TaskFlow.Service.SubTasksService;

@RestController
@RequestMapping("/api/user/boards/tasks/subtasks")
public class SubTasksController {

    private final SubTasksService subTasksService;

    @Autowired
    public SubTasksController(SubTasksService subTasksService) {
        this.subTasksService = subTasksService;

    }

    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(@RequestHeader("Authorization") String token, @PathVariable Long subTaskId) {
        return subTasksService.deleteSubTask(token, subTaskId);

    }

}
