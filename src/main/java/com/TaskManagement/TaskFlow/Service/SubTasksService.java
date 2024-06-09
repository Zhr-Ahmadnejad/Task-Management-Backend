package com.TaskManagement.TaskFlow.Service;

import org.springframework.http.ResponseEntity;

public interface SubTasksService {

    ResponseEntity<?> deleteSubTask(String token, Long subTaskId);

}
