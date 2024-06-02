package com.TaskManagement.TaskFlow.Vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStateVo {
    private Long id;
    private String stateName;
    private Long boardId;

    public TaskStateVo(Long id , String stateName , Long boardId ){
        this.id = id;
        this.stateName = stateName;
        this.boardId = boardId;
    }

    public TaskStateVo() {
    }
    
}
