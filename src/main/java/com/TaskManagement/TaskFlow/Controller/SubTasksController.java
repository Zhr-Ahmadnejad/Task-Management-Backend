package com.TaskManagement.TaskFlow.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagement.TaskFlow.Dto.SubTaskDto;
import com.TaskManagement.TaskFlow.Dto.TaskDto;
import com.TaskManagement.TaskFlow.Service.SubTasksService;
import org.springframework.web.bind.annotation.PutMapping;


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

    @PostMapping()
    public ResponseEntity<?> createSubTask(@RequestHeader("Authorization") String token, @RequestBody SubTaskDto subTaskDTO) {
        try {
            ResponseEntity<?> response = subTasksService.createTask(token, subTaskDTO);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{subTaskId}")
    public ResponseEntity<?> updateIsActive(@RequestHeader("Authorization") String token, @PathVariable Long subTaskId ,  @RequestBody SubTaskDto subTaskDTO){
        try {
            ResponseEntity<?> response = subTasksService.updateIsActive(token, subTaskId, subTaskDTO);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }


}
