package com.TaskManagement.TaskFlow.Service;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Model.Users;


public interface SecurityService {
    ResponseEntity<?> loginUser(String email, String password);
    ResponseEntity<?> registerUser(Users user);
}
