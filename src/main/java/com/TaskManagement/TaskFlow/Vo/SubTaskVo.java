package com.TaskManagement.TaskFlow.Vo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubTaskVo {
    private Long id;
    private String title;
    private boolean isActive;
    private Long taskId;
}
