package com.TaskManagement.TaskFlow.Dto;

import java.util.List; 
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStateDto {
    private List<String> stateName;
    private Long boardId; 
}
