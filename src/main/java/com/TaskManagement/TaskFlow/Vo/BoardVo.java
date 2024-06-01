package com.TaskManagement.TaskFlow.Vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardVo {
    private Long id;
    private String boardName;
    private Long userId;
    private Long boardId;

    // constructors
    public BoardVo(Long id, String boardName, Long userId, Long boardId) {
        this.id = id;
        this.boardName = boardName;
        this.userId = userId;
        this.boardId = boardId;
    }
}
