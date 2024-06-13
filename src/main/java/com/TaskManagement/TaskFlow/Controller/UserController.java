package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Dto.UserDto;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.naming.NameNotFoundException;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token,@PathVariable Long userId) {
        return userService.getUserById(token,userId);
    }

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateUser( @RequestHeader("Authorization") String token, @RequestBody UserDto newUser) throws NameNotFoundException {
            return userService.updateUser(token, newUser);
  
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return userService.deleteUser(token, id);

    }
}
