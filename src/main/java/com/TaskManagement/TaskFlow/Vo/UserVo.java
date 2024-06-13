package com.TaskManagement.TaskFlow.Vo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVo {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
