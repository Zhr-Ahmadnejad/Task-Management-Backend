package com.TaskManagement.TaskFlow.Vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskVo {
    private Long id;
    private String taskName;
    private String description;
    private Long taskStateId;
    private Long boardId;
    private List<SubTaskVo> subTasks;
    private String priority;

    public TaskVo(Long id, String taskName, String description, Long taskStateId, Long boardId, List<SubTaskVo> subTasks, String priority) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.taskStateId = taskStateId;
        this.boardId = boardId;
        this.subTasks = subTasks;
        this.priority = priority;

    }

    public TaskVo() {
    }
}
