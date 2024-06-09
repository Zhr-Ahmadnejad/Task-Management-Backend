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
    private Long stateId;
    private Long boardId;
    private List<SubTaskVo> subTasks;

    public TaskVo(Long id, String taskName, String description, Long stateId, Long boardId, List<SubTaskVo> subTasks) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.stateId = stateId;
        this.boardId = boardId;
        this.subTasks = subTasks;

    }

    public TaskVo() {
    }
}
