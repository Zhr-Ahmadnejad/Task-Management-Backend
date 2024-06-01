package com.TaskManagement.TaskFlow.Vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStateVo {
    private Long id;
    private String stateName;
    private Long boardId;
    private Long userId;

    public TaskStateVo(Long id , String stateName , Long boardId , Long userId){
        this.id = id;
        this.stateName = stateName;
        this.boardId = boardId;
        this.userId = userId;
    }

    public TaskStateVo() {
        //TODO Auto-generated constructor stub
    }
    
}
