package com.TaskManagement.TaskFlow.Dto;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStateDto {
    private Long stateId;
    private List<String> statesName;
    private String stateName;
    private Long boardId; 
}
