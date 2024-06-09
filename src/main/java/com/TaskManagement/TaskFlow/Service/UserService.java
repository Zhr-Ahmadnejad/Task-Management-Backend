package com.TaskManagement.TaskFlow.Service;

import java.util.List;
import java.util.Optional;

import javax.naming.NameNotFoundException;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Model.Users;

public interface UserService {

    Users findUserByEmail(String userEmail);

    Long findIdByEmail(String userEmail);

    List<Users> getAllUsers();

    Optional<Users> getUserById(Long userId);

    Users createUser(Users user);

    Users updateUser(String token, @Valid Users newUser) throws NameNotFoundException;

    ResponseEntity<?> deleteUser(String token, Long userId);
}
