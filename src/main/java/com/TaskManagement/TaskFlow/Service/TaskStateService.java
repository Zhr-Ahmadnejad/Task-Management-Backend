package com.TaskManagement.TaskFlow.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.TaskStateDto;
import com.TaskManagement.TaskFlow.Vo.TaskStateVo;

public interface TaskStateService {
    ResponseEntity<List<TaskStateVo>> createTaskState(String token,TaskStateDto taskStateDTO);
}