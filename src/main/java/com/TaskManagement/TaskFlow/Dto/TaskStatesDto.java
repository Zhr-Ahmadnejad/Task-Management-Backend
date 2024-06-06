package com.TaskManagement.TaskFlow.Dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatesDto {
    private List<String> statesName;
    private Long boardId;
}
