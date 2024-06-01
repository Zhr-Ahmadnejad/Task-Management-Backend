package com.TaskManagement.Vo;


@Getter
@Setter
public class TaskVo {
    private Long id;
    private String taskName;
    private String description;
    private Long userId;
    private Long stateId;
    private Long boardId;


    public TaskVo(Long id , String taskName , String description , Long userId , Long stateId , Long boardId){
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.userId = userId;
        this.stateId = stateId;
        this.boardId = boardId;

    }



}
