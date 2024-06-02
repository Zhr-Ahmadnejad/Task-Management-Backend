package com.TaskManagement.TaskFlow.Vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardVo {
    private Long id;
    private String boardName;

    // constructors
    public BoardVo(Long id, String boardName, Long userId, Long boardId) {
        this.id = id;
        this.boardName = boardName;
    }

    public BoardVo() {
    }
}
