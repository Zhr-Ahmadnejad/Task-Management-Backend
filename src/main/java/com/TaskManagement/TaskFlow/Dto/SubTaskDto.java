package com.TaskManagement.TaskFlow.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubTaskDto {
    private String title;
    private boolean isActive;
    public Long getTaskId;
}
