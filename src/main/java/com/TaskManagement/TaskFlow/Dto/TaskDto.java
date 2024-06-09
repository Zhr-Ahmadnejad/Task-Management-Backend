package com.TaskManagement.TaskFlow.Dto;



import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskDto {

    private String taskName;

    private String description;

    private Long taskStateId;

    private Long boardId;

    private List<String> subTasks;
    
}
