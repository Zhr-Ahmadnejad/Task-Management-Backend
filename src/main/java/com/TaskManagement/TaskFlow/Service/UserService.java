package com.TaskManagement.TaskFlow.Service;

import java.util.List;

import javax.naming.NameNotFoundException;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.UserDto;
import com.TaskManagement.TaskFlow.Model.Users;

public interface UserService {

    Users findUserByEmail(String userEmail);

    Long findIdByEmail(String userEmail);

    List<Users> getAllUsers();

    ResponseEntity<?> getUserById(String token, Long userId);

    Users createUser(Users user);

    ResponseEntity<?> updateUser(String token, UserDto newUser) throws NameNotFoundException;

    ResponseEntity<?> deleteUser(String token, Long userId);
}
