package com.TaskManagement.TaskFlow.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {
    private List<String> taskStates;
    private String boardName;
}
