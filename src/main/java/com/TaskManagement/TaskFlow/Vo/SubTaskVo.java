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

    public SubTaskVo(Long id , String title , boolean isActive , Long taskId){
        this.id = id;
        this.title = title;
        this.isActive = isActive;
        this.taskId = taskId;
    }
    public SubTaskVo(){}
}
