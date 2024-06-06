package com.TaskManagement.TaskFlow.Dto;

import java.util.Set;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskDto {

    private String taskName;

    private String description;

    private Long stateId;

    private Long boardId;
    
}
