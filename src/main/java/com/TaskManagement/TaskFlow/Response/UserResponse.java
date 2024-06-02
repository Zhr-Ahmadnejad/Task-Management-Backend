package com.TaskManagement.TaskFlow.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;




@AllArgsConstructor
@RequiredArgsConstructor
@Data
@NonNull
public class UserResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    
    public UserResponse(String string) {
    }
}