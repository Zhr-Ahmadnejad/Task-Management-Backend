package com.TaskManagement.TaskFlow.Dto;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskDto {

    @NotEmpty(message = "Task name cannot be empty")
    private String taskName;

    private String description;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "State ID cannot be null")
    private Long stateId;

    @NotNull(message = "Board ID cannot be null")
    private Long boardId;

    private Set<Long> subTaskIds;

    // Constructors
    
}
