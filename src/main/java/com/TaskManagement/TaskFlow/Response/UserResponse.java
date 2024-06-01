package com.TaskManagement.TaskFlow.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;




@AllArgsConstructor
@RequiredArgsConstructor
@Data
@NonNull
public class UserResponse {

    public UserResponse(String string) {
        //TODO Auto-generated constructor stub
    }
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}