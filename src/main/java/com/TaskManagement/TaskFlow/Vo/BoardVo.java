package com.TaskManagement.TaskFlow.Vo;

import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardVo {
    private Long id;
    private String boardName;
    private List<TaskStateVo> taskStates;

    // constructors
    public BoardVo(Long id, String boardName, List<TaskStateVo> taskStates) {
        this.id = id;
        this.boardName = boardName;
        this.taskStates = taskStates;
    }

    public BoardVo(Long id, String boardName, Long userId) {
        this.id = id;
        this.boardName = boardName;
    }

    public BoardVo() {
    }
}
